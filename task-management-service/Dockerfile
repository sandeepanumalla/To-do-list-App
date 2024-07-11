FROM ubuntu:latest
LABEL authors="sandeep"

RUN apt-get update && \
    apt-get install -y openjdk-17-jdk-headless

WORKDIR /usr/app

COPY ./target/task-management-service-0.0.1-SNAPSHOT.jar ./

CMD ["java", "-jar", "task-management-service-0.0.1-SNAPSHOT.jar"]
