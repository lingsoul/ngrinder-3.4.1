#!/bin/sh
curpath=`dirname $0`
cd ${curpath}
os=`uname -o`
if ( echo ${os} |grep -q "inux" );then
    local_ip=`/sbin/ifconfig eth0|grep 'inet addr' |awk '{print $2}'|awk -F ":" '{print $2}'`
    sed -i "s/.*monitor.binding_ip=.*/monitor.binding_ip=`echo $local_ip`/" __agent.conf
    echo replace ip: $local_ip
fi
java -server -cp "lib/*" org.ngrinder.NGrinderAgentStarter --mode monitor --command run $@
