FROM --platform=$TARGETPLATFORM eclipse-temurin:21-jre-alpine

RUN mkdir /app
WORKDIR /app

COPY build/libs/*-all.jar /app/minesweeper.jar

ENTRYPOINT ["java"]
CMD ["-jar", "/app/minesweeper.jar"]
