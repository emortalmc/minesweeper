FROM --platform=$BUILDPLATFORM eclipse-temurin:17-jre

RUN mkdir /app
WORKDIR /app

COPY build/libs/*-all.jar /app/minesweeper.jar

CMD ["java", "-jar", "/app/minesweeper.jar"]
