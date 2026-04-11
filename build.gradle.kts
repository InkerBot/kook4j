buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("io.github.cdimascio:dotenv-kotlin:6.5.1")
    }
}

plugins {
    id("java-library")
    id("maven-publish")
}

group = "bot.inker.kook4j"

val buildNumber: String? = System.getenv("BUILD_NUMBER")
version = if (buildNumber != null) "0.1.$buildNumber" else "0.1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
    withSourcesJar()
    withJavadocJar()
}

repositories {
    mavenCentral()
}

dependencies {
    api("com.squareup.okhttp3:okhttp:5.3.2")
    api("com.google.code.gson:gson:2.13.2")
    implementation("org.slf4j:slf4j-api:2.0.17")

    testImplementation(platform("org.junit:junit-bom:6.0.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testRuntimeOnly("org.slf4j:slf4j-simple:2.0.17")
}

tasks.test {
    useJUnitPlatform()

    val dotenv = io.github.cdimascio.dotenv.Dotenv.configure()
        .directory(rootProject.projectDir.absolutePath)
        .ignoreIfMissing()
        .load()
    dotenv.entries().forEach { systemProperty(it.key, it.value) }
    // CI fallback: if a key was not set by .env, read from the actual environment
    listOf("KOOK_TOKEN").forEach { key ->
        if (systemProperties[key] == null) {
            System.getenv(key)?.let { systemProperty(key, it) }
        }
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
    repositories {
        if (System.getenv("MAVEN_REPO_URL") != null) {
            maven {
                url = uri(System.getenv("MAVEN_REPO_URL"))
                credentials {
                    username = System.getenv("MAVEN_USERNAME")
                    password = System.getenv("MAVEN_PASSWORD")
                }
            }
        }
    }
}
