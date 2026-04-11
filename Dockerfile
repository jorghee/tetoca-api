FROM eclipse-temurin:21-jdk

WORKDIR /tetoca

COPY ./target/tetoca-api-0.0.1-SNAPSHOT.jar tetoca-api.jar

EXPOSE 8080

CMD ["java", "-jar", "tetoca-api.jar"]
