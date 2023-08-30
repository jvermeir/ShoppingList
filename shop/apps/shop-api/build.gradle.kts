import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val ktor_version: String by project
val h2_version: String by project
val javax_persistence_api_version: String by project
val kotlinx_datetime_version: String by project
val fuel_version: String by project
val kotlinx_serialization_json_version: String by project
val springmockk_version: String by project

plugins {
  id("org.springframework.boot") version "2.6.7"
  id("io.spring.dependency-management") version "1.0.11.RELEASE"
  kotlin("jvm") version "1.9.10"
  kotlin("plugin.spring") version "1.9.10"
  kotlin("plugin.serialization") version "1.9.10"
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
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinx_serialization_json_version")
  implementation("com.github.kittinunf.fuel:fuel:$fuel_version")
  implementation ("org.jetbrains.kotlinx:kotlinx-datetime:$kotlinx_datetime_version")
  implementation("javax.persistence:javax.persistence-api:$javax_persistence_api_version")
  runtimeOnly("com.h2database:h2:$h2_version")
  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation ("com.ninja-squad:springmockk:$springmockk_version")
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
