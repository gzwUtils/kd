FROM java:8

MAINTAINER gaozw private_gzw1314@163.com
#构建参数
ARG  version
ARG  profile
ARG  servicename
ARG  projectname

#环境变量
ENV jarName "${servicename}_${version}.jar"


#设置容器工作目录
WORKDIR /app

COPY ${projectname}/target/*.jar /app



RUN echo "java -Denv=${profile} -server -Xms2g -Xms2g    -jar /app/$jarName" > /app/startup.sh

ENTRYPOINT ["sh","/app/startup.sh"]
