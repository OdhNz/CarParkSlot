FROM openjdk:17-jdk-alpine

COPY target/carparkslot-0.0.1-SNAPSHOT.jar carparkslot-1.0.0.jar

ENTRYPOINT [ "java", "-jar", "carparkslot-1.0.0.jar" ]