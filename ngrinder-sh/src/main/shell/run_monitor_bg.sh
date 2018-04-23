#!/bin/sh
curpath=`dirname $0`
nohup ${curpath}/run_monitor.sh -o $@ > /dev/null & 2>&1