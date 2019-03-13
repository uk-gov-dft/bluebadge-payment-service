FROM openjdk:8-jre-stretch
ARG JAR_FILE
ARG SCHEMA_FILE
ADD ${JAR_FILE} app.jar
ADD ${SCHEMA_FILE} schema.zip
ADD https://github.com/ufoscout/docker-compose-wait/releases/download/2.2.1/wait /wait
RUN chmod +x /wait
