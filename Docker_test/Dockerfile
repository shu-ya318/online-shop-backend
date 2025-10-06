# ======================================
# Stage 1: 基於官方 Tomcat 10 + JDK17
# ======================================
FROM tomcat:10-jdk17-temurin

LABEL maintainer="Jimmy <jimmy@example.com>" \
      version="1.0.0" \
      description="Spring Boot WAR deployment with Tomcat 10 & JDK17 (Official Image)"

# --------------------------------------
# 清空預設 webapps（可選，避免預設應用干擾）
# --------------------------------------
RUN rm -rf /usr/local/tomcat/webapps/*

# --------------------------------------
# 複製你的 WAR 到 ROOT.war
# 命名為 ROOT.war 可以直接訪問 '/' 啟動應用
# --------------------------------------
COPY onlineShop.war /usr/local/tomcat/webapps/ROOT.war

# --------------------------------------
# 設定工作目錄
# --------------------------------------
WORKDIR /usr/local/tomcat

# --------------------------------------
# 開放 Tomcat 預設 port
# --------------------------------------
EXPOSE 8080

# --------------------------------------
# 啟動 Tomcat
# --------------------------------------
CMD ["catalina.sh", "run"]
