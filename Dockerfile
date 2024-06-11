FROM ubuntu:latest
LABEL authors="Henrique Melo"


RUN apt-get update && \
    apt-get install -y wget curl gnupg && \
    wget https://download.java.net/java/GA/jdk21/35/GPL/openjdk-21_linux-x64_bin.tar.gz && \
    tar -xvf openjdk-21_linux-x64_bin.tar.gz && \
    mv jdk-21 /opt/ && \
    rm openjdk-21_linux-x64_bin.tar.gz && \
    apt-get clean

ENV JAVA_HOME /opt/jdk-21
ENV PATH $JAVA_HOME/bin:$PATH

WORKDIR /app

COPY target/conta-api.jar /app/conta-api.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "conta-api.jar"]