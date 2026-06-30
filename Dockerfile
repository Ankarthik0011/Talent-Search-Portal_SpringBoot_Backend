FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY target/talent-search-backend-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 9095

ENTRYPOINT ["java","-jar","app.jar"]
