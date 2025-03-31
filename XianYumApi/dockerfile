FROM  openjdk:11-jre

# 将本地文件夹挂在到当前容器
VOLUME /tmp

# 复制文件到容器
ADD ./target/xianyum-api-0.0.1-SNAPSHOT.jar  /usr/local/docker/app.jar

# 声明需要暴露的端口
EXPOSE 91910

# 配置容器启动后执行的命令
ENTRYPOINT ["java", "-Duser.timezone=GMT+08", "-Djava.timezone=GMT+08", "-jar", "/usr/local/docker/app.jar"]
