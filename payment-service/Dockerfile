FROM openjdk:8-jdk-alpine
VOLUME /tmp
ENV PORT=8003
EXPOSE 8003

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} payment-service.jar

ENTRYPOINT ["java","-Dserver.port=${PORT:-8003}","-Djava.security.egd=file:/dev/./urandom","-XX:+UseContainerSupport","-Xmx512m","-Xms128m","-jar","/payment-service.jar"]
