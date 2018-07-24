#!/bin/bash
if [ ! -f  ~/.commonsos-api-pid ]
then
    exit 0
fi

PID=`cat ~/.commonsos-api-pid`
echo "Stopping pid $PID"
kill -9 $PID