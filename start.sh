#!/bin/bash
java -Xmx1024m -Dmode=test -Dfile.encoding=UTF-8 -jar commonsos-api.jar >> ../logs/stdouterr.log 2>&1 &
