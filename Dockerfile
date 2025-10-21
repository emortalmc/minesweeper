FROM eclipse-temurin:25-jre-alpine

RUN mkdir /app
WORKDIR /app

COPY build/libs/*-all.jar /app/minesweeper.jar

ENTRYPOINT ["java"]
CMD ["-jar", "/app/minesweeper.jar"]
