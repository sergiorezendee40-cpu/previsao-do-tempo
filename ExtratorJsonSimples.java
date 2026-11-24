import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utilitário didático para extrair dados simples de uma resposta JSON.
 * 
 * NOTA DIDÁTICA:
 * Em projetos comerciais com Maven ou Gradle, é padrão usar bibliotecas como Jackson
 * (ObjectMapper) ou Gson para converter JSON em objetos Java automaticamente.
 * 
 * No entanto, para um projeto pequeno de estudo e para permitir a execução imediata
 * sem exigir instalação de gerenciadores de dependência externos, esta classe
 * utiliza Expressões Regulares (RegEx) para extrair valores-chave de forma transparente.
 */
public class ExtratorJsonSimples {

    /**
     * Extrai o valor de um campo de texto (String) dentro de um JSON.
     * Exemplo: "name": "São Paulo" -> retorna "São Paulo"
     */
    public static String extrairTexto(String json, String chave) {
        if (json == null) return null;
        // Padrão: "chave"\s*:\s*"([^"]*)"
        String regex = "\"" + Pattern.quote(chave) + "\"\\s*:\\s*\"([^\"]*)\"";
        Matcher matcher = Pattern.compile(regex).matcher(json);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    /**
     * Extrai o valor de um campo numérico com casas decimais (double).
     * Exemplo: "temperature_2m": 25.4 -> retorna 25.4
     */
    public static double extrairDouble(String json, String chave, double valorPadrao) {
        if (json == null) return valorPadrao;
        // Padrão: "chave"\s*:\s*(-?[0-9]+(?:\.[0-9]+)?)
        String regex = "\"" + Pattern.quote(chave) + "\"\\s*:\\s*(-?[0-9]+(?:\\.[0-9]+)?)";
        Matcher matcher = Pattern.compile(regex).matcher(json);
        if (matcher.find()) {
            try {
                return Double.parseDouble(matcher.group(1));
            } catch (NumberFormatException e) {
                return valorPadrao;
            }
        }
        return valorPadrao;
    }

    /**
     * Extrai o valor de um campo numérico inteiro (int).
     * Exemplo: "relative_humidity_2m": 72 -> retorna 72
     */
    public static int extrairInt(String json, String chave, int valorPadrao) {
        if (json == null) return valorPadrao;
        // Padrão: "chave"\s*:\s*(-?[0-9]+)
        String regex = "\"" + Pattern.quote(chave) + "\"\\s*:\\s*(-?[0-9]+)";
        Matcher matcher = Pattern.compile(regex).matcher(json);
        if (matcher.find()) {
            try {
                return Integer.parseInt(matcher.group(1));
            } catch (NumberFormatException e) {
                return valorPadrao;
            }
        }
        return valorPadrao;
    }

    /**
     * Extrai um sub-bloco de objeto JSON dentro de uma chave.
     * Exemplo: buscar o bloco "current": { ... }
     */
    public static String extrairBlocoObjeto(String json, String chaveObjeto) {
        if (json == null) return null;
        String marcador = "\"" + chaveObjeto + "\"";
        int inicioChave = json.indexOf(marcador);
        if (inicioChave == -1) return null;

        int inicioChaveAbre = json.indexOf("{", inicioChave);
        if (inicioChaveAbre == -1) return null;

        int nivel = 0;
        for (int i = inicioChaveAbre; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '{') {
                nivel++;
            } else if (c == '}') {
                nivel--;
                if (nivel == 0) {
                    return json.substring(inicioChaveAbre, i + 1);
                }
            }
        }
        return null;
    }
}
