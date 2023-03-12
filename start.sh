#!/bin/bash



#验证是否已经启动
if [[ $pid_count -ne 0 ]]; then
  echo "$APP_NAME is running, can't start again!"; exit 1;
fi

if [[ -n "$SERVER_PORT" ]]; then
  JAVA_OPTS="-DSERVER_PORT=$SERVER_PORT $JAVA_OPTS"
fi

if [[ -n "$SERVER_JMX_PORT" ]]; then
  JAVA_OPTS="-Djava.rmi.server.hostname=$RMI_SERVER_IP -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.port=$SERVER_JMX_PORT $JAVA_OPTS"
fi

JAVA_OPTS="-Xms${HEAP_INIT:-256m} $JAVA_OPTS"

JAVA_OPTS="-Xmx${HEAP_MAX:-256m} $JAVA_OPTS"

JAVA_OPTS="-Xmn${HEAP_YOUNG_MAX:-128m} $JAVA_OPTS"

#指定JVM内存
JAVA_OPTS="-server -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=. $JAVA_OPTS"

nohup java $JAVA_OPTS -jar ${APP_HOME}/${APP_NAME}.jar >/dev/null 2>&1 &
pid=$!
monitoring_health 90
if [[ $? -eq 0 ]]; then
  echo "starting ${APP_NAME}[${pid}] success!"
  exit 0;
fi

echo "starting ${APP_NAME} failed!"
exit 1
