# ===== Stage 1: Build the app =====
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

# Copy pom.xml first to leverage Docker cache
COPY demo/pom.xml ./demo/

# Download dependencies (cached layer)
RUN cd demo && mvn dependency:go-offline

# Copy source code
COPY demo/src ./demo/src/

# Build the app and skip tests
RUN cd demo && mvn clean package -DskipTests

# ===== Stage 2: Prepare Tomcat (offline) =====
FROM ubuntu:22.04 AS tomcat

WORKDIR /usr/local

# Unzip Tomcat
COPY apache-tomcat-10.1.46.tar.gz .

RUN set -eux && \
    tar -xzf apache-tomcat-10.1.46.tar.gz && \
    rm -f apache-tomcat-10.1.46.tar.gz && \
    mv apache-tomcat-10.1.46 tomcat

# ===== Stage 3: Final runtime image =====
FROM ubuntu:22.04

LABEL maintainer="ShuYaHsieh <shuyahsieh318@gmail.com>" \
      version="1.0.0" \
      description="Spring Boot WAR deployment with Tomcat 10 (offline) & JDK17 (Online)"

# Install JRE for running the app
RUN apt-get update && \
    apt-get install -y openjdk-17-jre-headless curl && \
    rm -rf /var/lib/apt/lists/*

# Copy Tomcat from preparation stage
COPY --from=tomcat /usr/local/tomcat /usr/local/tomcat

# Copy Spring Boot WAR from build stage
COPY --from=builder /app/demo/target/onlineShop.war /usr/local/tomcat/webapps/onlineShop.war

ENV SPRING_PROFILES_ACTIVE=docker
ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
ENV PATH=$JAVA_HOME/bin:$PATH
ENV CATALINA_HOME=/usr/local/tomcat
ENV PATH=$CATALINA_HOME/bin:$PATH

WORKDIR $CATALINA_HOME

EXPOSE 8080

CMD ["bin/catalina.sh", "run"]
