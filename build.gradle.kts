import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val kotestVersion = "4.1.0.293-SNAPSHOT"

plugins {
    java
    kotlin("jvm") version "1.3.61"
}

group = "io.github.jmoore34.quinerelay"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    testCompile("junit", "junit", "4.12")
    listOf("runner-junit5", "assertions-core", "runner-console"/*, "property"*/).forEach {
        testImplementation("io.kotest:kotest-$it-jvm:$kotestVersion")
    }
    implementation("com.github.ajalt:mordant:1.2.1")
}

tasks.withType<Test> { useJUnitPlatform() }

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}
tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

