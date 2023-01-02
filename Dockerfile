FROM adoptopenjdk/openjdk11:ubi
EXPOSE 8080
COPY target/validation-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]