#!/bin/sh
curpath=`dirname $0`
cd ${curpath}
local_ip=`ifconfig|grep Bcast|head -n 1|awk '{print $2}'|awk -F ":" '{print $2}'`
sed -i "3c monitor.binding_ip=`echo $local_ip`" __agent.conf
java -server -cp "lib/*" org.ngrinder.NGrinderAgentStarter --mode monitor --command run $@