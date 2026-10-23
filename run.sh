#!/usr/bin/env sh
# Compila tudo para a pasta out/ e executa o menu (ou o problema passado como argumento)
# Uso: ./run.sh            -> menu
#      ./run.sh barbeiro   -> executa direto
set -e
cd "$(dirname "$0")"
javac -encoding UTF-8 -d out $(find src -name "*.java")
java -Dstdout.encoding=UTF-8 -cp out app.Main "$@"
