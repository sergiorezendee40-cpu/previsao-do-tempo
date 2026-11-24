import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Serviço responsável por realizar as chamadas HTTP à API de Previsão do Tempo em Tempo Real (Open-Meteo).
 * 
 * CONCEITOS JAVA APRENDIDOS:
 * 1. java.net.http.HttpClient (Disponível nativamente a partir do Java 11)
 * 2. URLEncoder: Tratamento de caracteres especiais e acentos em URLs (ex: 'São Paulo').
 * 3. Formatação de Hora ao Vivo: java.time.LocalTime e DateTimeFormatter para carimbo de tempo em tempo real.
 * 4. API Open-Meteo em Tempo Real:
 *    - Atualizada continuamente com base em radares meteorológicos e satélites.
 *    - Fornece dados instantâneos de vento, pressão atmosférica, se é dia/noite e temperatura do ar.
 */
public class ServicoPrevisaoTempo {

    // Instância única reutilizável do HttpClient (boa prática de desempenho no Java)
    private final HttpClient httpClient;
    private static final DateTimeFormatter FORMATADOR_HORA = DateTimeFormatter.ofPattern("HH:mm:ss");

    public ServicoPrevisaoTempo() {
        // Configuramos o cliente HTTP com um tempo limite de conexão (timeout) de 10 segundos
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    /**
     * Localização geográfica simplificada para uso interno no serviço.
     */
    public record Localizacao(String nome, String estado, String pais, double latitude, double longitude) {}

    /**
     * Busca as coordenadas geográficas de uma cidade.
     */
    public Localizacao buscarCoordenadas(String nomeCidade) throws IOException, InterruptedException {
        String cidadeCodificada = URLEncoder.encode(nomeCidade.trim(), StandardCharsets.UTF_8);

        String url = String.format(
            "https://geocoding-api.open-meteo.com/v1/search?name=%s&count=1&language=pt&format=json",
            cidadeCodificada
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(10))
                .header("User-Agent", "EstudoJavaPrevisaoTempo/1.0")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("Falha ao consultar geocodificação. HTTP Status: " + response.statusCode());
        }

        String json = response.body();

        if (!json.contains("\"results\"")) {
            return null;
        }

        String blocoResultado = ExtratorJsonSimples.extrairBlocoObjeto(json, "results");
        if (blocoResultado == null) {
            blocoResultado = json;
        }

        String nome = ExtratorJsonSimples.extrairTexto(blocoResultado, "name");
        String estado = ExtratorJsonSimples.extrairTexto(blocoResultado, "admin1");
        String pais = ExtratorJsonSimples.extrairTexto(blocoResultado, "country");
        double lat = ExtratorJsonSimples.extrairDouble(blocoResultado, "latitude", 0.0);
        double lon = ExtratorJsonSimples.extrairDouble(blocoResultado, "longitude", 0.0);

        if (nome == null) nome = nomeCidade;
        if (estado == null) estado = "N/D";
        if (pais == null) pais = "Brasil";

        return new Localizacao(nome, estado, pais, lat, lon);
    }

    /**
     * Busca a previsão do tempo completa para o nome de uma cidade.
     */
    public DadosClima buscarPrevisao(String nomeCidade) throws IOException, InterruptedException {
        Localizacao loc = buscarCoordenadas(nomeCidade);
        if (loc == null) {
            throw new IllegalArgumentException("Cidade não encontrada: '" + nomeCidade + "'. Verifique a ortografia.");
        }
        return buscarClimaPorCoordenadas(loc);
    }

    /**
     * Consulta os dados meteorológicos atuais da API de previsão usando latitude e longitude em tempo real.
     */
    public DadosClima buscarClimaPorCoordenadas(Localizacao loc) throws IOException, InterruptedException {
        // Parâmetros em tempo real solicitados à API:
        // - temperature_2m: Temperatura do ar atual (°C)
        // - relative_humidity_2m: Umidade relativa atual (%)
        // - apparent_temperature: Sensação térmica atual (°C)
        // - precipitation: Chuva/precipitação acumulada na última medição (mm)
        // - weather_code: Código da WMO indicando condição atual do tempo
        // - wind_speed_10m: Velocidade do vento em km/h
        // - wind_direction_10m: Direção do vento em graus (rosa dos ventos)
        // - surface_pressure: Pressão atmosférica na superfície em hPa
        // - is_day: Indica se na localidade é dia (1) ou noite (0)
        String url = String.format(
            Locale.US,
            "https://api.open-meteo.com/v1/forecast?latitude=%.5f&longitude=%.5f&current=temperature_2m,relative_humidity_2m,apparent_temperature,precipitation,weather_code,wind_speed_10m,wind_direction_10m,surface_pressure,is_day&timezone=auto",
            loc.latitude(),
            loc.longitude()
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(10))
                .header("User-Agent", "EstudoJavaPrevisaoTempo/1.0")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("Falha ao consultar previsão do tempo. HTTP Status: " + response.statusCode());
        }

        String json = response.body();

        String blocoCurrent = ExtratorJsonSimples.extrairBlocoObjeto(json, "current");
        if (blocoCurrent == null) {
            throw new IOException("Dados atuais de clima não encontrados na resposta da API.");
        }

        double temperatura = ExtratorJsonSimples.extrairDouble(blocoCurrent, "temperature_2m", 0.0);
        double sensacao = ExtratorJsonSimples.extrairDouble(blocoCurrent, "apparent_temperature", 0.0);
        int umidade = ExtratorJsonSimples.extrairInt(blocoCurrent, "relative_humidity_2m", 0);
        double precipitacao = ExtratorJsonSimples.extrairDouble(blocoCurrent, "precipitation", 0.0);
        double vento = ExtratorJsonSimples.extrairDouble(blocoCurrent, "wind_speed_10m", 0.0);
        double direcaoGraus = ExtratorJsonSimples.extrairDouble(blocoCurrent, "wind_direction_10m", 0.0);
        double pressao = ExtratorJsonSimples.extrairDouble(blocoCurrent, "surface_pressure", 1013.25);
        int isDayInt = ExtratorJsonSimples.extrairInt(blocoCurrent, "is_day", 1);
        boolean isDia = (isDayInt == 1);
        int codigoClima = ExtratorJsonSimples.extrairInt(blocoCurrent, "weather_code", 0);
        String horarioEstacao = ExtratorJsonSimples.extrairTexto(blocoCurrent, "time");

        // Traduz o código WMO considerando dia/noite
        TradutorCondicaoTempo.CondicaoInfo condicao = TradutorCondicaoTempo.traduzir(codigoClima, isDia);
        String direcaoTexto = TradutorCondicaoTempo.obterDirecaoVento(direcaoGraus);

        // Hora atual do sistema do usuário no momento da consulta
        String horaConsultaLocal = LocalTime.now().format(FORMATADOR_HORA);

        return new DadosClima(
            loc.nome(),
            loc.estado(),
            loc.pais(),
            loc.latitude(),
            loc.longitude(),
            temperatura,
            sensacao,
            umidade,
            precipitacao,
            vento,
            direcaoTexto,
            pressao,
            isDia,
            codigoClima,
            condicao.descricao(),
            condicao.emoji(),
            horarioEstacao != null ? horarioEstacao.replace("T", " ") : "Agora",
            horaConsultaLocal
        );
    }
}
