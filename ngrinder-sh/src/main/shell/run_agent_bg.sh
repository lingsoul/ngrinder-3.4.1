#!/bin/sh
curpath=`dirname $0`
nohup ${curpath}/run_agent.sh -o$@ > /dev/null & 2>&1
