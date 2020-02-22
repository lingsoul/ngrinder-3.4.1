#!/bin/sh
curpath=`pwd`
cd ${curpath}
os=`uname -a`

if ( echo ${os} |grep -q "inux" );then
    local_ip=`/sbin/ifconfig eth0|grep 'inet addr' |awk '{print $2}'|awk -F ":" '{print $2}'`
    sed -i "s/.*monitor.binding_ip=.*/monitor.binding_ip=`echo $local_ip`/" __agent.conf
    echo linux replace ip: $local_ip
elif ( echo ${os} |grep -q "Darwin" );then
    local_ip=`/sbin/ifconfig en0|grep cast|awk '{print $2}'|awk -F ":" '{print $1}'`
    sed -i "" "s/.*monitor.binding_ip=.*/monitor.binding_ip=`echo $local_ip`/" __agent.conf
    echo mac replace ip: $local_ip
fi

java -server -cp "lib/*" org.ngrinder.NGrinderAgentStarter --mode monitor --command run $@
