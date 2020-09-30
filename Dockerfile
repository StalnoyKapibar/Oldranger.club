FROM maven:3.6.3-jdk-8 as build-stage
WORKDIR /oldranger/
COPY src/ src/
COPY pom.xml ./
RUN mvn clean package

FROM openjdk:8
WORKDIR /oldranger/
COPY --from=build-stage /oldranger/target/oldranger.club-0.0.1-SNAPSHOT.jar /oldranger/target/
COPY uploads/ uploads/
COPY media/ media/
COPY filesInChat/ filesInChat/
EXPOSE 8888
ENTRYPOINT ["java", "-jar", "target/oldranger.club-0.0.1-SNAPSHOT.jar", "--spring.profiles.active=prod"]

