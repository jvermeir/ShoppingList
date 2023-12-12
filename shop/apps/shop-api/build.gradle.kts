import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val h2_version: String by project
val javax_persistence_api_version: String by project
val fuel_version: String by project
val kotlinx_serialization_json_version: String by project
val springmockk_version: String by project
val ktor_version: String by project

springBoot {
  mainClass.set("nl.vermeir.shopapi.ShopApiApplicationKt")
}


plugins {
  id("org.springframework.boot") version "3.1.5"
  id("io.spring.dependency-management") version "1.1.3"
  kotlin("jvm") version "1.8.22"
  kotlin("plugin.spring") version "1.8.22"
  kotlin("plugin.jpa") version "1.8.22"
  kotlin("plugin.allopen") version "1.8.0"
  kotlin("plugin.serialization") version "1.9.10"
}

allOpen {
  annotation("jakarta.persistence.Entity")
  annotation("jakarta.persistence.Embeddable")
  annotation("jakarta.persistence.MappedSuperclass")
}

group = "nl.vermeir"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
  mavenCentral()
}

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.0.4")
  implementation("org.springframework.boot:spring-boot-starter-mustache:3.0.7")
  implementation("org.springframework.boot:spring-boot-starter-web:3.1.0")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.14.2")
  implementation("org.jetbrains.kotlin:kotlin-reflect")
  developmentOnly("org.springframework.boot:spring-boot-devtools:3.0.4")
  runtimeOnly("com.h2database:h2")
  testImplementation("org.springframework.boot:spring-boot-starter-test:3.1.0")
  testImplementation("org.springframework.boot:spring-boot-starter-test:3.1.0") {
    exclude(module = "mockito-core")
  }
  testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")
  testImplementation("com.ninja-squad:springmockk:$springmockk_version")
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinx_serialization_json_version")
  implementation("com.github.kittinunf.fuel:fuel:$fuel_version")

  implementation("com.google.code.gson:gson:2.10.1")
  implementation("io.ktor:ktor-client-core:$ktor_version")
  implementation("io.ktor:ktor-client-cio:$ktor_version")
  implementation("io.ktor:ktor-client-content-negotiation:$ktor_version")
  implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
}

tasks.withType<KotlinCompile> {
  kotlinOptions {
    freeCompilerArgs = listOf("-Xjsr305=strict")
    jvmTarget = "17"
  }
}

tasks.withType<Test> {
  useJUnitPlatform()
  testLogging.showStandardStreams = true

  testLogging {
    events;"PASSED"; "FAILED"; "SKIPPED"; "STANDARD_OUT"; "STANDARD_ERROR"
  }
  maxParallelForks = 1
}

tasks.bootBuildImage {
  builder.set("paketobuildpacks/builder-jammy-base:latest")
}
