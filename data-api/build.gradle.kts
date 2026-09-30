plugins {
    java
    id("org.springframework.boot") version "4.1.1"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.winwin"
version = "0.0.1-SNAPSHOT"
description = "data-api"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Request validation.
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // REST API and JSON.
    implementation("org.springframework.boot:spring-boot-starter-webmvc")

    // Validation tests.
    testImplementation("org.springframework.boot:spring-boot-starter-validation-test")

    // MVC tests and MockMvc.
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")

    // JUnit test launcher.
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
