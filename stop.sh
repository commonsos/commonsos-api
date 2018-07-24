#!/bin/bash
if [ ! -f  ~/.commonsos-api-pid ]
then
    exit 0
fi

PID=`cat ~/.commonsos-api-pid`
echo "Stopping pid $PID"
kill -9 $PID

sleep 0.5

while kill -0 "$PID" 2&>1 >/dev/null; do
  sleep 0.5
done
