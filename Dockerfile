FROM java:8-jre-alpine
ARG JAR_NAME
COPY "build/libs/${JAR_NAME}" "/usr/src/app/app.jar"
EXPOSE 8980 8981 5005
ADD https://s3.eu-west-2.amazonaws.com/uk-gov-dft-common-artefacts/import-rds-certs.sh /import-rds-certs.sh
RUN echo ${JAR_NAME} && apk --no-cache add coreutils openssl curl && chmod +x /import-rds-certs.sh && /import-rds-certs.sh
CMD ["java","-jar","/usr/src/app/app.jar"]
