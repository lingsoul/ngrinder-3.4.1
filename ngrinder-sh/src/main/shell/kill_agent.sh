#!/bin/sh
curpath=`pwd |awk -F '/' '{print $NF}'`
agent_pid=`ps -ef | grep 'org.ngrinder.NGrinderAgentStarter' | grep 'mode=agent' | grep -v grep | grep $curpath | awk '{print $2}'`
echo kill agent pid: $agent_pid
kill -9 $agent_pid
echo kill agent success!
