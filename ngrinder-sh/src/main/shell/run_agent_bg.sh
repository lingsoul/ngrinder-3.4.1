#!/bin/sh
curpath=`pwd |awk -F '/' '{print $NF}'`
nohup ./run_agent.sh -o  -ah ~/.${curpath} --host-id ${curpath} $@ > /dev/null & 2>&1
