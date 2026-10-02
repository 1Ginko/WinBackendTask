plugins {
    java
    id("org.springframework.boot") version "4.1.1"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.winwin"
version = "0.0.1-SNAPSHOT"
description = "auth-api"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // JPA database access.
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // Database migrations.
    implementation("org.springframework.boot:spring-boot-starter-flyway")

    // Authentication and authorization.
    implementation("org.springframework.boot:spring-boot-starter-security")

    // Request validation.
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // REST API and JSON.
    implementation("org.springframework.boot:spring-boot-starter-webmvc")

    // PostgreSQL support for Flyway.
    implementation("org.flywaydb:flyway-database-postgresql")

    // PostgreSQL JDBC driver.
    runtimeOnly("org.postgresql:postgresql")

    // JPA tests.
    testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")

    // Flyway tests.
    testImplementation("org.springframework.boot:spring-boot-starter-flyway-test")

    // Security tests.
    testImplementation("org.springframework.boot:spring-boot-starter-security-test")

    // Validation tests.
    testImplementation("org.springframework.boot:spring-boot-starter-validation-test")

    // MVC tests and MockMvc.
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")

    // JUnit test launcher.
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // JWT creation and Bearer token validation.
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
