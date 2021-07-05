FROM openjdk:8-jdk-alpine
VOLUME /tmp
ENV PORT=8004
EXPOSE 8004

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} booking-service.jar

ENTRYPOINT ["java","-Dserver.port=${PORT:-8004}","-Djava.security.egd=file:/dev/./urandom","-XX:+UseContainerSupport","-Xmx512m","-Xms128m","-jar","/booking-service.jar"]
