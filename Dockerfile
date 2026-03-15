FROM  eclipse-temurin:17-jdk-jammy AS builder

WORKDIR /app

COPY  target/*.jar app.jar

EXPOSE 8081

ENTRYPOINT [ "java","-jar","app.jar" ]

