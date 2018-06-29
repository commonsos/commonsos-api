#!/bin/bash
if [ ! -f  pid ]
then
    exit 0
fi

PID=`cat pid`
echo "Stopping pid $PID"
kill -9 $PID
exit 0