#!/bin/bash
PID=`cat pid`
echo "Stopping pid $PID"
kill -9 $PID
