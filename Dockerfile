FROM --platform=$BUILDPLATFORM eclipse-temurin:20-jre

RUN mkdir /app
WORKDIR /app

COPY build/libs/*-all.jar /app/minesweeper.jar

CMD ["java", "--enable-preview", "-jar", "/app/minesweeper.jar"]
