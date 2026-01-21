FROM  eclipse-temurin:21.0.3_9-jdk

# 定义主目录环境变量
ENV API_ROOT_PATH=/home/app/xianyum/api

# 只需要创建主目录（子目录会随宿主机挂载自动同步）
RUN mkdir -p ${API_ROOT_PATH} \
    && chmod 755 ${API_ROOT_PATH}

#维护者信息
LABEL  maintainer="zhangwei"

# 复制文件到容器
ADD ./xianyum-starter/target/xianyum-api-0.0.1-SNAPSHOT.jar  ${API_ROOT_PATH}/app.jar

# 声明需要暴露的端口
EXPOSE 9190

WORKDIR ${API_ROOT_PATH}

# 配置容器启动后执行的命令
ENTRYPOINT ["java", "-Duser.timezone=GMT+08", "-Djava.timezone=GMT+08", "-jar", "${API_ROOT_PATH}/app.jar"]

