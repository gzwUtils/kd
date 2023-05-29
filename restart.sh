#!/bin/bash

#加载env配置脚本文件
source `dirname $0`/env.sh
#先stop应用
sh stop.sh
#再start应用
sh start.sh
if [[ $? -eq 0 ]]; then
  echo "restart success"
  exit 0
fi

echo "restart failed"
exit 1
