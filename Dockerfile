FROM java:8
#项目维护人员
MAINTAINER gaozw 18843096270@163.com

# 设置时区为Asia/Shanghai，可以根据需要更改
ENV TIME_ZONE=Asia/Shanghai

# 更新时区
RUN ln -snf /usr/share/zoneinfo/$TIME_ZONE /etc/localtime && echo $TIME_ZONE > /etc/timezone

##构建参数
#ARG  version
ARG  profile
#ARG  servicename
ARG  projectname
#
##环境变量
#ENV jarName "${servicename}_${version}.jar"


#设置容器工作目录
WORKDIR /app

COPY ./target/${projectname}.jar /app/
#COPY ./start.sh  /app/
#COPY ./restart.sh  /app/
#COPY ./stop.sh  /app/



# 暴露端口
EXPOSE 8092

# 设置Java虚拟机初始内存和最大内存
#-server: 启用Java HotSpot虚拟机的服务器模式，该模式针对长时间运行的应用程序进行了优化以提高性能。
#-Xms512m: 设置Java堆的初始大小为512兆字节。
#-Xmx1024m: 设置Java堆的最大大小为1024兆字节。
#-XX:+UseG1GC: 启用G1垃圾收集器。G1（Garbage-First）是一种相对新的垃圾收集器，旨在提供更可预测的停顿时间和更好的性能。
#-XX:+HeapDumpOnOutOfMemoryError: 在发生内存溢出错误时生成堆转储文件。这对于分析内存问题非常有用。
#-XX:HeapDumpPath=/dumps/oom_dump.hprof: 指定内存转储文件。在这个例子中，堆转储文件将被写入 /dumps/oom_dump.hprof 目录文件中。
ENV JAVA_OPTS="-server -Xms512m -Xmx1024m -XX:+UseG1GC -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/dumps/oom_dump.hprof"
ENV APP_ENV="--spring.profiles.active=${profile}"
ENV jarName="${projectname}.jar"
# 创建内存转储文件和日志文件存储目录
RUN mkdir /app/dumps
RUN mkdir /app/logs

RUN echo "java -DEnv=${profile}   /app/${projectname}.jar"

VOLUME ./docker:/app/

# 设置容器启动时执行的命令
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -Dlogging.file=/app/logs/application.log -jar /app/$jarName $APP_ENV"]



#ENTRYPOINT ["sh","/app/start.sh"]

#-t 指定镜像名称 . 表示使用当前目录下的Dockerfile
#docker build -t 镜像名称 .
#查看镜像
#docker images

#列出所有容器
#docker ps -a

#docker start 容器名称/ID
#docker stop  容器名称/ID
#docker kill 容器名称/ID
#docker rm   容器名称/ID
#docker rmi  镜像名称/ID
#VOLUME 外部路径:容器路径 （一个就是容器路径）
# docker build --build-arg profile=dev --build-arg projectname=kd-1.0.0 -t kd-docker-app --no-cache .
#docker history 镜像名称

# docker run  -d --name 容器名称 -p 端口映射（80:90）镜像名称

#docker run -v <宿主机目录>:<容器内目录> <镜像名称>  （如何在Docker容器中挂载宿主机目录）

#docker pull <repository>:<tag>

#docker image prune

#docker exec -it 容器名称 /bin/bash

