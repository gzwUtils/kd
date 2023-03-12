#!/bin/bash
#取得应用pid
source `dirname $0`/env.sh

#验证是否已经启动
if [ $pid_count -eq 0 ]
    then echo "$APP_NAME is not running, can't stop!"; exit 0;
fi

echo -n "stopping ${APP_NAME}[${pid}] "

#关闭
kill $pid

#检测进程是否关闭，默认等待60秒
for i in $(seq 60); do
    echo -n "."
    sleep 1
    source $APP_HOME/env.sh
    if [ $pid_count -eq 0 ]
        then echo "stopping $APP_NAME success with seconds($i)"; exit 0;
    fi
done

echo "stopping $APP_NAME failed, please check the thread of $APP_NAME"
exit 1
