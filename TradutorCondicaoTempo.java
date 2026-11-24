/**
 * Classe responsável por traduzir os códigos WMO (World Meteorological Organization)
 * retornados pela API Open-Meteo em descrições em português, emojis e direções de vento.
 * 
 * CONCEITO JAVA APRENDIDO: 'Switch Expression' (Java 14+)
 * O switch moderno permite retornar valores diretamente com '->', sem necessidade de
 * usar 'break' repetidamente, evitando erros comuns e deixando o código muito mais limpo.
 */
public class TradutorCondicaoTempo {

    /**
     * Registro auxiliar para encapsular a descrição textual e o emoji correspondente.
     */
    public record CondicaoInfo(String descricao, String emoji) {}

    /**
     * Mapeia o código numérico WMO para uma descrição em português e emoji,
     * considerando se no local é dia ou noite para ajustar os ícones em tempo real.
     * 
     * @param codigo Código numérico de condição meteorológica da WMO.
     * @param isDia Indica se na localidade é dia (true) ou noite (false).
     * @return Objeto CondicaoInfo com a descrição e o emoji.
     */
    public static CondicaoInfo traduzir(int codigo, boolean isDia) {
        return switch (codigo) {
            case 0 -> new CondicaoInfo("Céu limpo", isDia ? "☀️" : "🌙");
            case 1 -> new CondicaoInfo("Quase limpo", isDia ? "🌤️" : "🌙☁️");
            case 2 -> new CondicaoInfo("Parcialmente nublado", isDia ? "⛅" : "☁️");
            case 3 -> new CondicaoInfo("Nublado / Encoberto", "☁️");
            case 45 -> new CondicaoInfo("Nevoeiro / Névoa", "🌫️");
            case 48 -> new CondicaoInfo("Nevoeiro com depósito de gelo", "🌫️❄️");
            case 51 -> new CondicaoInfo("Garoa leve", "🌦️");
            case 53 -> new CondicaoInfo("Garoa moderada", "🌦️");
            case 55 -> new CondicaoInfo("Garoa densa", "🌧️");
            case 56, 57 -> new CondicaoInfo("Garoa congelante", "🌨️");
            case 61 -> new CondicaoInfo("Chuva fraca", "🌦️");
            case 63 -> new CondicaoInfo("Chuva moderada", "🌧️");
            case 65 -> new CondicaoInfo("Chuva forte", "🌧️🌧️");
            case 66, 67 -> new CondicaoInfo("Chuva congelante", "🌨️");
            case 71 -> new CondicaoInfo("Queda de neve fraca", "❄️");
            case 73 -> new CondicaoInfo("Queda de neve moderada", "❄️❄️");
            case 75 -> new CondicaoInfo("Queda de neve intensa", "🌨️❄️");
            case 77 -> new CondicaoInfo("Grãos de neve", "🌨️");
            case 80 -> new CondicaoInfo("Pancadas de chuva fracas", "🌦️");
            case 81 -> new CondicaoInfo("Pancadas de chuva moderadas", "🌧️");
            case 82 -> new CondicaoInfo("Pancadas de chuva violentas", "⛈️");
            case 85, 86 -> new CondicaoInfo("Pancadas de neve", "🌨️");
            case 95 -> new CondicaoInfo("Tempestade com trovoadas", "⛈️");
            case 96, 99 -> new CondicaoInfo("Tempestade com granizo", "⛈️🌨️");
            default -> new CondicaoInfo("Condição não classificada (" + codigo + ")", "❓");
        };
    }

    /**
     * Converte o ângulo em graus (0° a 360°) retornado pela API para o ponto cardeal
     * correspondente na rosa dos ventos.
     * 
     * @param graus Direção do vento em graus (ex: 0 = Norte, 90 = Leste, 180 = Sul, 270 = Oeste).
     * @return Ponto cardeal com seta indicativa.
     */
    public static String obterDirecaoVento(double graus) {
        if (graus < 0 || graus > 360) return "Variável";
        if (graus >= 337.5 || graus < 22.5) return "Norte (N ⬆️)";
        if (graus < 67.5) return "Nordeste (NE ↗️)";
        if (graus < 112.5) return "Leste (L ➡️)";
        if (graus < 157.5) return "Sudeste (SE ↘️)";
        if (graus < 202.5) return "Sul (S ⬇️)";
        if (graus < 247.5) return "Sudoeste (SO ↙️)";
        if (graus < 292.5) return "Oeste (O ⬅️)";
        return "Noroeste (NO ↖️)";
    }
}
