#!/bin/sh
curpath=`pwd |awk -F '/' '{print $NF}'`
owned_user=`pwd |awk -F '/' '{print $NF}'|awk -F '-' '{print $NF}'`
if [ "$curpath" != "ngrinder-agent" ];then
    sed -i "s/.*agent.region=.*/agent.region=`echo NONE_owned_$owned_user`/" __agent.conf
    echo agent.region=$owned_user
fi
nohup ./run_agent.sh -o  -ah ~/.${curpath} --host-id ${curpath} $@ > /dev/null & 2>&1
