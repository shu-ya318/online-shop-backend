# ======================================
# Stage 1: 安裝 JDK 17（線上）
# ======================================
FROM ubuntu:22.04 AS jdk17

LABEL stage="jdk17"

# 更新套件並安裝 OpenJDK 17（包含完整的 JRE）
RUN apt-get update && \
    apt-get install -y openjdk-17-jdk openjdk-17-jre-headless && \
    rm -rf /var/lib/apt/lists/*

ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
ENV PATH=$JAVA_HOME/bin:$PATH

# ======================================
# Stage 2: 安裝 Tomcat
# ======================================
FROM ubuntu:22.04 AS tomcat

LABEL stage="tomcat"

WORKDIR /usr/local

# 解壓 Tomcat 10.1
COPY apache-tomcat-10.1.46.tar.gz .

RUN set -eux && \
    tar -xzf apache-tomcat-10.1.46.tar.gz && \
    rm -f apache-tomcat-10.1.46.tar.gz && \
    mv apache-tomcat-10.1.46 tomcat

# ======================================
# Stage 3: 最終映像（部署 WAR）
# ======================================
FROM ubuntu:22.04

LABEL maintainer="ShuYaHsieh <shuyahsieh318@gmail.com>" \
      version="1.0.0" \
      description="Spring Boot WAR deployment with Tomcat 10 & JDK17 (Online)"

# 直接安裝 JDK（避免複製時遺失配置文件）
RUN apt-get update && \
    apt-get install -y openjdk-17-jdk && \
    rm -rf /var/lib/apt/lists/*

# 複製 Tomcat
COPY --from=tomcat /usr/local/tomcat /usr/local/tomcat

# 複製 Spring Boot WAR，使用 Maven 建置的產出
COPY target/onlineShop.war /usr/local/tomcat/webapps/onlineShop.war

# 環境變數
ENV SPRING_PROFILES_ACTIVE=docker
ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
ENV PATH=$JAVA_HOME/bin:$PATH
ENV CATALINA_HOME=/usr/local/tomcat
ENV PATH=$CATALINA_HOME/bin:$PATH

WORKDIR $CATALINA_HOME

EXPOSE 8080

CMD ["bin/catalina.sh", "run"]