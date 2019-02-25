import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.github.ben-manes.versions") version Plugin.VERSIONS

    kotlin("jvm") version Plugin.KOTLIN

    id("com.github.spotbugs") version Plugin.SPOTBUGS_PLUGIN

    id("com.github.johnrengelman.shadow") version Plugin.SHADOW_JAR

    idea
}

group = "com.github.bjoernpetersen"
version = "0.1.0"

spotbugs {
    isIgnoreFailures = true
    toolVersion = Plugin.SPOTBUGS_TOOL
}

idea {
    module {
        isDownloadJavadoc = true
    }
}

tasks {
    "compileKotlin"(KotlinCompile::class) {
        kotlinOptions.jvmTarget = "1.8"
    }

    "compileTestKotlin"(KotlinCompile::class) {
        kotlinOptions.jvmTarget = "1.8"
    }

    "test"(Test::class) {
        useJUnitPlatform()
    }
}

dependencies {
    implementation(
        group = "io.github.microutils",
        name = "kotlin-logging",
        version = Lib.KOTLIN_LOGGING) {
        exclude("org.slf4j")
        exclude("org.jetbrains")
        exclude("org.jetbrains.kotlin")
    }
    compileOnly(
        group = "com.github.bjoernpetersen",
        name = "musicbot",
        version = Lib.MUSICBOT)

    testImplementation(kotlin("stdlib-jdk8"))
    testRuntime(
        group = "org.slf4j",
        name = "slf4j-simple",
        version = Lib.SLF4J)
    testImplementation(
        group = "org.junit.jupiter",
        name = "junit-jupiter-api",
        version = Lib.JUNIT)
    testRuntime(
        group = "org.junit.jupiter",
        name = "junit-jupiter-engine",
        version = Lib.JUNIT)
}

repositories {
    jcenter()
}
