FROM openjdk:8-jdk-alpine
COPY target/osa-backend-0.0.1-SNAPSHOT.jar osa-backend-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/osa-backend-0.0.1-SNAPSHOT.jar"]