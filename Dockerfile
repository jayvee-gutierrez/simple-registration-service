FROM openjdk:17
COPY target/registration-service-1.0.0.jar registration-service-1.0.0.jar
ENTRYPOINT ["java","-jar","/registration-service-1.0.0.jar"]