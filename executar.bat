javac -encoding UTF-8 *.java
java Main@echo off
chcp 65001 > nul
echo =========================================================
echo    Compilando o Projeto Java - Previsao do Tempo
echo =========================================================
javac -encoding UTF-8 *.java
if %errorlevel% neq 0 (
    echo.
    echo [ERRO] Falha na compilacao! Verifique os erros acima.
    pause
    exit /b %errorlevel%
)
echo.
echo [SUCESSO] Compilado com sucesso! Iniciando...
echo.
java Main
pause
