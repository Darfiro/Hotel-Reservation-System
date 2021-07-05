FROM openjdk:8-jdk-alpine
VOLUME /tmp
ENV PORT=8008
EXPOSE 8008

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} hotel-service.jar

ENTRYPOINT ["java","-Dserver.port=${PORT:-8008}","-Djava.security.egd=file:/dev/./urandom","-XX:+UseContainerSupport","-Xmx512m","-Xms128m","-jar","/hotel-service.jar"]
