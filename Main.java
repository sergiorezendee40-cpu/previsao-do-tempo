import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Ponto de entrada da aplicação de estudo de Java com Previsão do Tempo em Tempo Real.
 * 
 * CONCEITOS JAVA APRENDIDOS NESTE PROJETO:
 * 1. java.net.http.HttpClient e requisições REST assíncronas/síncronas (Java 11+)
 * 2. Concorrência e Multithreading: 'Thread' e 'AtomicBoolean' para monitoramento ao vivo
 * 3. Modern Java 'record' como Data Transfer Object (DTO)
 * 4. 'Switch Expressions' modernas com operador seta '->'
 * 5. Manipulação de tempo com java.time.LocalTime e DateTimeFormatter
 * 6. Tratamento robusto de exceções e controle de fluxo
 */
public class Main {

    public static void main(String[] args) {
        // Habilita saída em UTF-8 para exibição correta de acentos e emojis no terminal Windows
        try {
            System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        } catch (Exception ignored) {}

        ServicoPrevisaoTempo servico = new ServicoPrevisaoTempo();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("=========================================================");
            System.out.println("    ☀️  CONSULTOR DE PREVISÃO DO TEMPO EM TEMPO REAL 🌦️  ");
            System.out.println("                Projeto de Estudo em Java                ");
            System.out.println("=========================================================");
            System.out.println("Escolha uma opção de estudo:");
            System.out.println("  [1] Consulta Rápida (pesquisar uma cidade)");
            System.out.println("  [2] 🟢 Modo Monitor em Tempo Real (atualização contínua)");
            System.out.println("  [0] Sair");
            System.out.print("\nOpção desejada: ");

            String opcao = scanner.nextLine().replace("\uFEFF", "").trim();

            if (opcao.equals("0") || opcao.equalsIgnoreCase("sair")) {
                System.out.println("\nEncerrando o programa. Bons estudos em Java! 👋");
                break;
            }

            switch (opcao) {
                case "1" -> executarConsultaRapida(servico, scanner);
                case "2" -> executarModoMonitorTempoReal(servico, scanner);
                default -> System.out.println("⚠️ Opção inválida! Escolha 1, 2 ou 0.\n");
            }
        }

        scanner.close();
    }

    /**
     * Realiza uma consulta única para a cidade digitada.
     */
    private static void executarConsultaRapida(ServicoPrevisaoTempo servico, Scanner scanner) {
        System.out.print("\nDigite o nome da cidade: ");
        String cidade = scanner.nextLine().replace("\uFEFF", "").trim();

        if (cidade.isEmpty()) {
            System.out.println("⚠️ Nome de cidade não pode ser vazio.\n");
            return;
        }

        System.out.println("🔍 Consultando dados meteorológicos em tempo real para '" + cidade + "'...");

        try {
            DadosClima dados = servico.buscarPrevisao(cidade);
            System.out.println();
            dados.imprimirFormatado();
            System.out.println();
        } catch (IllegalArgumentException e) {
            System.out.println("❌ " + e.getMessage() + "\n");
        } catch (IOException e) {
            System.out.println("❌ Erro de conexão de rede: " + e.getMessage() + "\n");
        } catch (InterruptedException e) {
            System.out.println("❌ A consulta foi interrompida.\n");
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Executa o monitoramento em tempo real com atualização automática periódica.
     * 
     * CONCEITO DE CONCORRÊNCIA JAVA:
     * Para que a tela atualize periodicamente sem travar o teclado do usuário,
     * iniciamos uma nova 'Thread' em segundo plano que executa as consultas na API,
     * enquanto a 'Thread Principal' aguarda o usuário pressionar [ENTER] para interromper.
     * 
     * O 'AtomicBoolean' garante que a alteração da variável seja visível imediatamente
     * entre as diferentes threads (visibilidade de memória e thread-safety).
     */
    private static void executarModoMonitorTempoReal(ServicoPrevisaoTempo servico, Scanner scanner) {
        System.out.print("\nDigite o nome da cidade para monitorar: ");
        String cidade = scanner.nextLine().replace("\uFEFF", "").trim();

        if (cidade.isEmpty()) {
            System.out.println("⚠️ Nome de cidade não pode ser vazio.\n");
            return;
        }

        System.out.print("Intervalo de atualização em segundos (padrão: 10s, mínimo: 5s): ");
        String entradaIntervalo = scanner.nextLine().trim();
        int intervaloSegundos = 10;
        try {
            if (!entradaIntervalo.isEmpty()) {
                intervaloSegundos = Math.max(5, Integer.parseInt(entradaIntervalo));
            }
        } catch (NumberFormatException e) {
            System.out.println("Valor inválido, usando padrão de 10 segundos.");
        }

        System.out.println("🔍 Buscando coordenadas da cidade...");
        final ServicoPrevisaoTempo.Localizacao loc;
        try {
            loc = servico.buscarCoordenadas(cidade);
            if (loc == null) {
                System.out.println("❌ Cidade não encontrada: '" + cidade + "'.\n");
                return;
            }
        } catch (Exception e) {
            System.out.println("❌ Erro ao buscar coordenadas: " + e.getMessage() + "\n");
            return;
        }

        System.out.println("\n=========================================================");
        System.out.printf("🟢 MONITORAMENTO EM TEMPO REAL INICIADO: %s (%s, %s)%n", loc.nome(), loc.estado(), loc.pais());
        System.out.printf("⏱️  Atualizando automaticamente a cada %d segundos.%n", intervaloSegundos);
        System.out.println("👉 Pressione [ENTER] a qualquer momento para parar e voltar ao menu.");
        System.out.println("=========================================================\n");

        AtomicBoolean emExecucao = new AtomicBoolean(true);
        final int intervalo = intervaloSegundos;

        // Criação e inicialização da Thread de monitoramento em segundo plano
        Thread monitorThread = new Thread(() -> {
            int contagem = 1;
            Double ultimaTemperatura = null;

            while (emExecucao.get()) {
                try {
                    // Consulta em tempo real à API usando as coordenadas já identificadas
                    DadosClima clima = servico.buscarClimaPorCoordenadas(loc);

                    // Indicador de variação de temperatura em relação à leitura anterior
                    String variacao = "";
                    if (ultimaTemperatura != null) {
                        double dif = clima.temperatura() - ultimaTemperatura;
                        if (Math.abs(dif) < 0.01) {
                            variacao = " (➖ Estável)";
                        } else if (dif > 0) {
                            variacao = String.format(" (🔺 +%.1f °C)", dif);
                        } else {
                            variacao = String.format(" (🔻 %.1f °C)", dif);
                        }
                    }
                    ultimaTemperatura = clima.temperatura();

                    System.out.printf("[%s] 📡 Leitura #%d recebida em tempo real%s:%n", 
                                      clima.horarioConsultaLocal(), contagem++, variacao);
                    clima.imprimirFormatado();
                    System.out.println("👉 Pressione [ENTER] para interromper o monitoramento...\n");

                    // Pausa entre as consultas verificando a cada segundo se o usuário cancelou
                    for (int s = 0; s < intervalo && emExecucao.get(); s++) {
                        Thread.sleep(1000);
                    }

                } catch (InterruptedException e) {
                    break;
                } catch (Exception e) {
                    System.out.println("⚠️ Falha temporária na leitura em tempo real: " + e.getMessage());
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException ignored) {
                        break;
                    }
                }
            }
        });

        // Inicia a execução concorrente
        monitorThread.start();

        // A thread principal aguarda o usuário apertar [ENTER]
        scanner.nextLine();

        // Sinaliza para a thread parar
        emExecucao.set(false);
        monitorThread.interrupt();

        try {
            // Aguarda a finalização limpa da thread
            monitorThread.join(2000);
        } catch (InterruptedException ignored) {}

        System.out.println("🛑 Monitoramento em tempo real encerrado com sucesso.\n");
    }
}
