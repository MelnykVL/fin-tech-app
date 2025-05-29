val javaVersion = providers.gradleProperty("javaVersion").map(String::toInt)

plugins {
    `java-library`
    `maven-publish`
}

repositories {
    mavenLocal()
    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
}

group = "edu.example"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(javaVersion.map(JavaLanguageVersion::of))
    }
}

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc>() {
    options.encoding = "UTF-8"
}
