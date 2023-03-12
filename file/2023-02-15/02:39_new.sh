echo 正在监控tomcat日志 # 控制输出字符串
tomcatDir=/home/demo/tomcat-8.3
cd $tomcatDir/logs
#查看近20行的日志
findNum=`tail -n 90  catalina.out |grep -c "断开的管道"`  #-c 获取包含关键字的行数   grep 检索文件中包含关键字的行. 文本过滤器
if[[$findNum -gt 0]];then
 echo '正在杀死tomcat'
   kill -9  `ps -ef|grep "$tomcatDir/bin/"|grep -v grep|awk '{print $2}'`
   sleep 5
 $tomcatDir/bin ./startup.sh
else 
 echo 程序运行良好
