# Compila tudo para a pasta out/ e executa o menu (ou o problema passado como argumento)
# Uso: .\run.ps1            -> menu
#      .\run.ps1 barbeiro   -> executa direto
$ErrorActionPreference = "Stop"
Set-Location $PSScriptRoot
$fontes = Get-ChildItem -Recurse -Filter *.java src | ForEach-Object { $_.FullName }
javac -encoding UTF-8 -d out $fontes
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
java "-Dstdout.encoding=UTF-8" -cp out app.Main @args
