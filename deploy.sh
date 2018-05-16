#!/bin/bash

set -e
set -x

JENKINS_WORKSPACE="$( cd "$(dirname "$0")" ; pwd -P )"

cd /home/commonsos

. .local_environment

[ -d logs ] || mkdir -p logs

rm -rf commonsos-api
mkdir -p commonsos-api
unzip $JENKINS_WORKSPACE/build/distributions/commonsos-api.zip

killall java || echo "No previous instance running"

pushd commonsos-api
java -Xmx1024m -Dmode=production -Dfile.encoding=UTF-8 -jar commonsos-api.jar  >> ../logs/stdouterr.log 2>&1 &
popd
