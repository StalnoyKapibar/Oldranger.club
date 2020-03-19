FROM openjdk:8-jdk-alpine
VOLUME /oldranger/
WORKDIR /oldranger/
ARG JAR_FILE=target/oldranger.club-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} target/app.jar
COPY uploads/ uploads/
COPY media/ media/
EXPOSE 8888
ENTRYPOINT ["java", "-jar", "target/app.jar"]