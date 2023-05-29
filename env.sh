#!/bin/bash
cd `dirname $0`/

############ 应用按需修改的部分 ############
#应用名称，不指定则默认取最后一个jar文件名, 即springboot one jar打包后xxx.jar中xxx的部分
APP_NAME=kd-1.0.0
#指定应用端口号，不填则使用应用配置文件指定的端口
SERVER_PORT=
#获取本机ip地址，用于jmx连接使用
RMI_SERVER_IP=`ifconfig -a|grep inet|grep -v 127.0.0.1|grep -v inet6|awk '{print $2}'|tr -d "addr:"`
#指定JMX端口，不填则不开启JMX功能
SERVER_JMX_PORT=7855
#初始堆内存大小Xms，默认256m
HEAP_INIT=512m
#最大堆内存大小Xmx，默认256m
HEAP_MAX=512m
#年轻代最大堆内存大小Xmn，默认128m
HEAP_YOUNG_MAX=256m
#PID FILE
PID_FILE="/var/run/${APP_NAME}.pid"
#LOCK FILE
LOCK_FILE="/var/lock/subsys/${APP_NAME}"
########################################

#应用目录
APP_HOME=$PWD/target/

#如果未设置应用名，默认取最后一个jar文件名
if [[ -z "$APP_NAME" ]]; then
  APP_NAME=`ls|grep "jar$"|tail -n 1`
  APP_NAME=${APP_NAME%.jar}
fi

#通过APP_NAME取得应用进程pid
pid=`ps aux|grep "${APP_NAME}.jar"|grep -v grep|awk '{print $2}'`

#取得应用进程数量（0未启动、1启动，大于1说明程序重复起动）
pid_count=`echo $pid|wc -w`

monitoring_health() {
  WAITING_TIME=$1
  #默认等待60秒
  if [[ -z ${WAITING_TIME} || ${WAITING_TIME} -le 0 ]]; then
    WAITING_TIME=60
  fi
  #取得应用实际配置的端口号，用于健康检查
  unzip -q -o -j ${APP_NAME}.jar BOOT-INF/classes/bootstrap.yaml -d /tmp/${APP_NAME}
  HEALTH_PORT=$(sed -n -e '/server.port/p' /tmp/${APP_NAME}/bootstrap.yaml | awk '{print $2}' | sed 's/\r//g')
  HEALTH_ADDR="http://127.0.0.1:${HEALTH_PORT}/actuator/health"
  /bin/rm -rf /tmp/${APP_NAME}
  if [[ -n ${HEALTH_PORT} ]]; then
    echo -n "monitoring health "
    #检测进程是否完全启动
    for i in $(seq ${WAITING_TIME}); do
        echo -n "."
        sleep 1
        http_status=$(curl -s -w '::%{http_code}' -I ${HEALTH_ADDR} | xargs echo | awk -F"::" '{print $2}')
        if [[ ${http_status} -eq 200 ]]; then
            echo " success with seconds($i)"
            return 0
        fi
    done
    echo " failed with timeout(${WAITING_TIME})!"
    return 1
  else
    return 0
  fi
}
