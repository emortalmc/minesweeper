FROM --platform=$BUILDPLATFORM eclipse-temurin:20-jre

RUN mkdir /app
WORKDIR /app

# Add libraries required for pyroscope
RUN apt-get install wget \
    libstdc++6 libstdc++ # Add libraries required for pyroscope

COPY build/libs/*-all.jar /app/minesweeper.jar

CMD ["java", "--enable-preview", "-jar", "/app/minesweeper.jar"]
