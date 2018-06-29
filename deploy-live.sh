#!/bin/bash

wget https://github.com/commonsos/commonsos-api/archive/master.zip
unzip -o master.zip
rm master.zip

pushd commonsos-api-master
./gradlew clean test bundle
DISTRIBUTION_FILE=`readlink -f build/distributions/commonsos-api.zip`
echo "Freshly built distribution file $DISTRIBUTION_FILE"
popd


TAG=`date +"%Y-%m-%d_%T"`
VERSIONED_FOLDER="$HOME/commonsos-api-$TAG/"

echo "Unpacking to $VERSIONED_FOLDER"


#mkdir "$VERSIONED_FOLDER"
unzip "$DISTRIBUTION_FILE" -d /tmp/
mv /tmp/commonsos-api "$VERSIONED_FOLDER"

echo "Linking current installation to $VERSIONED_FOLDER"
ln -sfv "${VERSIONED_FOLDER}" ~/commonsos-api

pushd ~/commonsos-api
./stop.sh
./start.sh
popd

echo "Done"


