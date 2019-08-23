FROM ubuntu:16.04

MAINTAINER linran

ADD seckill-0.0.1-SNAPSHOT.jar seckill.jar

RUN apt-get update && apt-get install && apt install openjdk-8-jdk

ENTRYPOINT ["java", "-jar", "/seckill.jar"]
