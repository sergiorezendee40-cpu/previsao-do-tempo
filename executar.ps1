# Script PowerShell para compilar e executar o projeto
Write-Host "Compilando o Projeto Java..." -ForegroundColor Cyan
javac -encoding UTF-8 *.java

if ($LASTEXITCODE -eq 0) {
    Write-Host "`n[SUCESSO] Compilação concluída! Iniciando a aplicação...`n" -ForegroundColor Green
    java Main
} else {
    Write-Host "`n[ERRO] Falha na compilação!" -ForegroundColor Red
}
