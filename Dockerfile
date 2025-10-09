# ===== Stage 1: install JDK 17 (online) =====
FROM ubuntu:22.04 AS jdk17

LABEL stage="jdk17"

# update packages and install OpenJDK 17 (include full JRE)
RUN apt-get update && \
    apt-get install -y openjdk-17-jdk openjdk-17-jre-headless && \
    rm -rf /var/lib/apt/lists/*

ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
ENV PATH=$JAVA_HOME/bin:$PATH

# ===== Stage 2: install Tomcat (offline) =====
FROM ubuntu:22.04 AS tomcat

LABEL stage="tomcat"

WORKDIR /usr/local

# unzip Tomcat 10.1
COPY apache-tomcat-10.1.46.tar.gz .

RUN set -eux && \
    tar -xzf apache-tomcat-10.1.46.tar.gz && \
    rm -f apache-tomcat-10.1.46.tar.gz && \
    mv apache-tomcat-10.1.46 tomcat

# ===== Stage 3: final image =====
FROM ubuntu:22.04

LABEL maintainer="ShuYaHsieh <shuyahsieh318@gmail.com>" \
      version="1.0.0" \
      description="Spring Boot WAR deployment with Tomcat 10 (offline) & JDK17 (Online)"

# install JDK
RUN apt-get update && \
    apt-get install -y openjdk-17-jdk && \
    rm -rf /var/lib/apt/lists/*

# copy Tomcat
COPY --from=tomcat /usr/local/tomcat /usr/local/tomcat

# copy Spring Boot WAR, built by Maven
COPY demo/target/onlineShop.war /usr/local/tomcat/webapps/onlineShop.war

ENV SPRING_PROFILES_ACTIVE=docker
ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
ENV PATH=$JAVA_HOME/bin:$PATH
ENV CATALINA_HOME=/usr/local/tomcat
ENV PATH=$CATALINA_HOME/bin:$PATH

WORKDIR $CATALINA_HOME

EXPOSE 8080

CMD ["bin/catalina.sh", "run"]