FROM --platform=$TARGETPLATFORM eclipse-temurin:21-jre-alpine

RUN mkdir /app
WORKDIR /app

# Download wget
RUN #apk add --no-cache wget

COPY build/libs/*-all.jar /app/minesweeper.jar

ENTRYPOINT ["java"]
CMD ["-jar", "/app/minesweeper.jar"]
