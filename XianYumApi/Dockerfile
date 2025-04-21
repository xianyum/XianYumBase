FROM  openjdk:21

# 将本地文件夹挂在到当前容器
# VOLUME /tmp

#第2部分： 维护者信息
LABEL  mainatiner="zhangwei"

# 复制文件到容器
ADD ./xianyum-starter/target/xianyum-api-0.0.1-SNAPSHOT.jar  /home/app/xianyum/api/app.jar

# 声明需要暴露的端口
EXPOSE 9190

# 配置容器启动后执行的命令
ENTRYPOINT ["java", "-Duser.timezone=GMT+08", "-Djava.timezone=GMT+08", "-jar", "/home/app/xianyum/api/app.jar"]

