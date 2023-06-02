plugins {
    java
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "dev.emortal"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()

    maven("https://repo.emortal.dev/snapshots")
    maven("https://repo.emortal.dev/releases")

    maven("https://jitpack.io")
    maven("https://packages.confluent.io/maven/")
}

dependencies {
    implementation("com.google.protobuf:protobuf-java-util:3.21.12")

    implementation("dev.emortal.minestom:core:92c1464")
    implementation("dev.emortal.minestom:game-sdk:818e2c2")

//    implementation("net.kyori:adventure-text-minimessage:4.13.0")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}
