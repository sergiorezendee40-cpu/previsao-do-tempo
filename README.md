# 🌦️ Projeto de Estudo em Java: Previsão do Tempo em Tempo Real

Este projeto foi desenvolvido para o estudo prático de **Java Moderno (Java 11+)** consumindo a **API REST pública Open-Meteo**, trazendo dados meteorológicos **em tempo real** e um modo de **monitoramento contínuo ao vivo**.

---

## 🎯 Novidades da Versão em Tempo Real
- **Modo Monitor ao Vivo:** Acompanhe uma cidade com atualizações automáticas periódicas na tela (ex: a cada 5, 10 ou 15 segundos).
- **Indicador de Variação de Temperatura:** O programa compara a leitura atual com a anterior e mostra se a temperatura subiu (`🔺 +0.3 °C`), desceu (`🔻 -0.2 °C`) ou se manteve estável (`➖ Estável`).
- **Detecção de Dia / Noite:** Ajusta ícones dinamicamente (ex: ☀️ Céu limpo de dia vs 🌙 Céu limpo à noite).
- **Dados Meteorológicos Completos:**
  - 🌡️ Temperatura Atual e Sensação Térmica
  - 💧 Umidade Relativa do Ar (%)
  - 🌧️ Precipitação / Chuva em tempo real (mm)
  - 💨 Velocidade e Direção do Vento (N, NE, L, SE, S, SO, O, NO com emojis)
  - 🧭 Pressão Atmosférica na superfície (hPa)
  - ⏰ Carimbo de data/hora da consulta com segundos (`HH:mm:ss`)

---

## 💡 Novos Conceitos de Java Abordados no Código

1. **Multithreading e Concorrência (`Thread`)**:
   - Criação de uma *Thread em segundo plano* para executar o loop de consulta à API sem travar o teclado ou a interface do usuário.
2. **Thread-Safety com `AtomicBoolean`**:
   - Controle seguro de parada do loop entre threads diferentes através de variáveis atômicas (`emExecucao.set(false)`).
3. **Manipulação de Tempo (`java.time.LocalTime` e `DateTimeFormatter`)**:
   - Captura e formatação do horário exato do sistema com segundos a cada ciclo de atualização.
4. **`record` (Java 14+)**:
   - DTOs imutáveis para transporte e formatação limpa dos dados.
5. **`Switch Expressions` com Mapeamento Dia/Noite (Java 14+)**:
   - Expressões modernas de seleção retornando objetos compostos.
6. **Robustez com BOM (`\uFEFF`)**:
   - Tratamento de caracteres especiais de entrada em ambientes Windows PowerShell.

---

## 📁 Estrutura do Projeto

| Arquivo | Descrição |
|---|---|
| [**`Main.java`**](./Main.java) | Ponto de entrada com menu interativo, consulta pontual e modo monitor com concorrência (`Thread` e `AtomicBoolean`). |
| [**`ServicoPrevisaoTempo.java`**](./ServicoPrevisaoTempo.java) | Cliente HTTP assíncrono/síncrono consultando geocodificação e dados ao vivo da Open-Meteo. |
| [**`DadosClima.java`**](./DadosClima.java) | `record` que transporta os parâmetros climáticos em tempo real e cuida da exibição formatada. |
| [**`TradutorCondicaoTempo.java`**](./TradutorCondicaoTempo.java) | Traduz códigos WMO (considerando dia/noite) e calcula a direção do vento na rosa dos ventos. |
| [**`ExtratorJsonSimples.java`**](./ExtratorJsonSimples.java) | Utilitário didático para leitura e extração de chaves e objetos JSON sem dependências externas. |
| [**`executar.bat`**](./executar.bat) | Script para compilar e iniciar o projeto com 2 cliques no Windows. |
| [**`executar.ps1`**](./executar.ps1) | Script nativo para execução rápida pelo PowerShell. |

---

## 🚀 Como Executar

### Opção 1: Terminal (PowerShell ou Prompt de Comando)
```powershell
javac -encoding UTF-8 *.java
java Main
```

*(Ou no Java 22+, rode direto com: `java Main.java`)*

### Opção 2: Duplo Clique
Dê um duplo clique no arquivo [**`executar.bat`**](./executar.bat).

---

## 📺 Exemplo de Saída no Modo Tempo Real

```text
=========================================================
🟢 MONITORAMENTO EM TEMPO REAL INICIADO: São Paulo (São Paulo, Brasil)
⏱️  Atualizando automaticamente a cada 10 segundos.
👉 Pressione [ENTER] a qualquer momento para parar e voltar ao menu.
=========================================================

[10:38:26] 📡 Leitura #1 recebida em tempo real:
==================================================
📍 Previsão em Tempo Real: São Paulo - São Paulo, Brasil
🌐 Coordenadas: Lat -23.5475, Long -46.6361 | Período: ☀️ Dia
⏰ Consulta ao Vivo: 10:38:26 (Última medição: 2026-11-23 10:30)
--------------------------------------------------
🌡️  Temperatura Atual:   25.5 °C
🤔 Sensação Térmica:    29.4 °C
💧 Umidade Relativa:    74 %
🌧️  Precipitação:        0.1 mm
💨 Vento:               9.7 km/h (Noroeste (NO ↖️))
🧭 Pressão Atmosférica: 930.0 hPa
⛅ Condição do Tempo:   🌦️ Garoa leve
==================================================
👉 Pressione [ENTER] para interromper o monitoramento...

[10:38:36] 📡 Leitura #2 recebida em tempo real (➖ Estável):
...
```
