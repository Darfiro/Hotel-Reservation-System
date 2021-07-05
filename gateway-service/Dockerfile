FROM openjdk:8-jdk-alpine
VOLUME /tmp
ENV PORT=8006
EXPOSE 8006

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} gateway-service.jar

ENTRYPOINT ["java","-Dserver.port=${PORT:-8006}","-Djava.security.egd=file:/dev/./urandom","-XX:+UseContainerSupport","-Xmx512m","-Xms128m","-jar","/gateway-service.jar"]
