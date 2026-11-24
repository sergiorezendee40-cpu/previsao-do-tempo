/**
 * Modelo de dados que representa as informações meteorológicas em tempo real obtidas da API.
 * 
 * CONCEITO JAVA APRENDIDO: 'record' (Disponível a partir do Java 14/16)
 * Um 'record' é uma forma concisa de criar classes imutáveis cujo único objetivo
 * é transportar dados (Data Transfer Object - DTO).
 */
public record DadosClima(
    String cidade,
    String estado,
    String pais,
    double latitude,
    double longitude,
    double temperatura,
    double sensacaoTermica,
    int umidade,
    double precipitacao,
    double velocidadeVento,
    String direcaoVentoTexto,
    double pressaoAtmosferica,
    boolean isDia,
    int codigoClima,
    String descricaoClima,
    String emojiClima,
    String horarioEstacao,
    String horarioConsultaLocal
) {
    /**
     * Método auxiliar para exibir os dados de forma formatada e amigável no console.
     */
    public void imprimirFormatado() {
        System.out.println("==================================================");
        System.out.printf("📍 Previsão em Tempo Real: %s - %s, %s%n", cidade, estado, pais);
        System.out.printf("🌐 Coordenadas: Lat %.4f, Long %.4f | Período: %s%n", latitude, longitude, isDia ? "☀️ Dia" : "🌙 Noite");
        System.out.printf("⏰ Consulta ao Vivo: %s (Última medição: %s)%n", horarioConsultaLocal, horarioEstacao);
        System.out.println("--------------------------------------------------");
        System.out.printf("🌡️  Temperatura Atual:   %.1f °C%n", temperatura);
        System.out.printf("🤔 Sensação Térmica:    %.1f °C%n", sensacaoTermica);
        System.out.printf("💧 Umidade Relativa:    %d %%%n", umidade);
        System.out.printf("🌧️  Precipitação:        %.1f mm%n", precipitacao);
        System.out.printf("💨 Vento:               %.1f km/h (%s)%n", velocidadeVento, direcaoVentoTexto);
        System.out.printf("🧭 Pressão Atmosférica: %.1f hPa%n", pressaoAtmosferica);
        System.out.printf("⛅ Condição do Tempo:   %s %s%n", emojiClima, descricaoClima);
        System.out.println("==================================================");
    }
}
