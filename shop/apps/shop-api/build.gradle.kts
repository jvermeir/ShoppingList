import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  id("org.springframework.boot") version "2.6.7"
  id("io.spring.dependency-management") version "1.0.11.RELEASE"
  kotlin("jvm") version "1.6.21"
  kotlin("plugin.spring") version "1.6.21"
  kotlin("plugin.serialization") version "1.6.21"
  id("io.kotest") version "0.3.9"
}

group = "nl.vermeir"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
  mavenCentral()
}

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
  implementation("org.springframework.boot:spring-boot-starter-web")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
  implementation("org.jetbrains.kotlin:kotlin-reflect")
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")
  implementation("com.github.kittinunf.fuel:fuel:2.3.1")
  implementation ("org.jetbrains.kotlinx:kotlinx-datetime:0.3.2")
  implementation("javax.persistence:javax.persistence-api:2.2")
  runtimeOnly("com.h2database:h2:2.1.212")
  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("io.kotest:kotest-framework-engine:5.3.0")
  testImplementation ("io.kotest:kotest-assertions-core:5.3.0")
  testImplementation ("io.kotlintest:kotlintest-runner-junit5:3.4.2")
  implementation("aws.sdk.kotlin:s3:0.17.5-beta")
  testImplementation ("com.ninja-squad:springmockk:3.1.1")
  testImplementation(kotlin("test"))
}

tasks.withType<KotlinCompile> {
  kotlinOptions {
    freeCompilerArgs = listOf("-Xjsr305=strict")
    jvmTarget = "11"
  }
}

tasks.withType<Test> {
  useJUnitPlatform()
  testLogging.showStandardStreams = true

  testLogging {
    events ;"PASSED"; "FAILED"; "SKIPPED"; "STANDARD_OUT"; "STANDARD_ERROR"
  }
  maxParallelForks = 1
}

application.mainClass.set("nl.vermeir.shopapi.DynamoTest")
