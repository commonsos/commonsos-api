#!/bin/bash

[ -d ../logs ] || mkdir -p ../logs

. ~/.local_environment
java -Xmx1024m -Dfile.encoding=UTF-8 -jar commonsos-api.jar >> ../logs/stdouterr.log 2>&1 &

echo $! > ~/.commonsos-api-pid
echo "Starting process id: `cat ~/.commonsos-api-pid`"