# Build JAR
FROM  eclipse-temurin:17-jdk-jammy AS builder

WORKDIR /app

RUN apt-get update && apt-get install -y --no-install-recommends maven && rm -rf /var/lib/apt/lists/*

COPY pom.xml .

RUN mvn dependency:go-offline -B

COPY  src ./src

RUN mvn clean package -DskipTests


# Run App
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8081

ENTRYPOINT [ "java","-jar","app.jar" ]

