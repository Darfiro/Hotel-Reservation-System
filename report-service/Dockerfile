FROM openjdk:8-jdk-alpine
VOLUME /tmp
ENV PORT=8007
EXPOSE 8007

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} report-service.jar

ENTRYPOINT ["java","-Dserver.port=${PORT:-8007}","-Djava.security.egd=file:/dev/./urandom","-XX:+UseContainerSupport","-Xmx512m","-Xms128m","-jar","/report-service.jar"]
