FROM openjdk:8-jdk-alpine
VOLUME /tmp
ENV PORT=8081
EXPOSE 8081

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} session-service.jar

ENTRYPOINT ["java","-Dserver.port=${PORT:-8081}","-Djava.security.egd=file:/dev/./urandom","-XX:+UseContainerSupport","-Xmx512m","-Xms128m","-jar","/session-service.jar"]
