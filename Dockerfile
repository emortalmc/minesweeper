FROM eclipse-temurin:17-jre-alpine

RUN mkdir /app
WORKDIR /app

COPY build/libs/*-all.jar /app/minesweeper.jar

CMD ["java", "-jar", "/app/minesweeper.jar"]
