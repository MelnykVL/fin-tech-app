plugins {
    id("buildlogic.java-conventions")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

dependencies {
    api(libs.org.springframework.boot.spring.boot.starter.security)
    api(libs.org.springframework.boot.spring.boot.starter.web)
    api(libs.org.springframework.boot.spring.boot.starter.webflux)
    api(libs.org.projectlombok.lombok)
    testImplementation(libs.org.springframework.boot.spring.boot.starter.test)
    testImplementation(libs.org.springframework.boot.spring.boot.testcontainers)
    testImplementation(libs.io.projectreactor.reactor.test)
    testImplementation(libs.org.springframework.security.spring.security.test)
    testImplementation(libs.org.testcontainers.junit.jupiter)
}

description = "individuals"
