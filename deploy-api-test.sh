#!/bin/bash
set -e
set -x

DISTRIBUTION_FILE='commonsos-api.zip'
BUILD_NUMBER=$1
GIT_COMMIT=$2

VERSIONED_FOLDER="$HOME/commonsos-api-$BUILD_NUMBER-$GIT_COMMIT"

echo "Unpacking to $VERSIONED_FOLDER"
unzip "$DISTRIBUTION_FILE" -d /tmp/
mv /tmp/commonsos-api "$VERSIONED_FOLDER"

echo "Linking current installation to $VERSIONED_FOLDER"
rm commonsos-api || true
ln -sfv "${VERSIONED_FOLDER}" commonsos-api

pushd ~/commonsos-api
./stop.sh || true
./start.sh
popd

echo "Done"