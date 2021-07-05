FROM openjdk:8-jdk-alpine
VOLUME /tmp
ENV PORT=8001
EXPOSE 8001

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} loyalty-service.jar

ENTRYPOINT ["java","-Dserver.port=${PORT:-8001}","-Djava.security.egd=file:/dev/./urandom","-XX:+UseContainerSupport","-Xmx512m","-Xms128m","-jar","/loyalty-service.jar"]
