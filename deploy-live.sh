#!/bin/bash
set -e
set -x

DISTRIBUTION_FILE='commonsos-api.zip'
GIT_REVISION=$1
BUILD_NUMBER=$2

VERSIONED_FOLDER="$HOME/commonsos-api-$BUILD_NUMBER-$GIT_REVISIION/"

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


