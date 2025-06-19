import org.springframework.boot.gradle.plugin.SpringBootPlugin

plugins {
  java
  alias(libs.plugins.spring.boot)
}

val javaVersion = providers.gradleProperty("javaVersion").map(String::toInt)

group = "edu.example"
version = "0.0.1"
description = "individuals"

java {
  toolchain {
    languageVersion = javaVersion.map(JavaLanguageVersion::of)
    vendor = JvmVendorSpec.ADOPTIUM
  }
  sourceCompatibility = JavaVersion.VERSION_21
}

configurations {
  compileOnly {
    extendsFrom(configurations.annotationProcessor.get())
  }
}

repositories {
  mavenCentral()
}

dependencies {
  implementation(platform(SpringBootPlugin.BOM_COORDINATES))
  implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
  implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
  implementation("org.springframework.boot:spring-boot-starter-webflux")
  implementation("org.springframework.boot:spring-boot-starter-validation")
  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("org.springframework.boot:spring-boot-testcontainers")
  testImplementation("org.testcontainers:postgresql")
  testImplementation(libs.keycloak.testcontainers)
  testImplementation("io.projectreactor:reactor-test")
  testImplementation("org.springframework.security:spring-security-test")
  testImplementation("org.testcontainers:junit-jupiter")
  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
  compileOnly(libs.lombok)
  annotationProcessor(libs.lombok)
}

tasks.withType<Test> {
  useJUnitPlatform()
}