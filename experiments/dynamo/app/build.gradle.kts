import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

// TODO: adding spring boot dependency causes CreateTable to fail with
// Exception in thread "main" java.lang.NoSuchMethodError: okhttp3.Request$Builder.tag(Lkotlin/reflect/KClass;Ljava/lang/Object;)Lokhttp3/Request$Builder;
//kotlin("plugin.spring") version "1.7.22"
//    id("io.spring.dependency-management") version "1.1.0"
//    id("org.springframework.boot") version "2.7.7"
plugins {
    kotlin("plugin.serialization") version "1.7.22"
    id("org.jetbrains.kotlin.jvm") version "1.7.22"
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")
    implementation("aws.sdk.kotlin:dynamodb:0.18.0-beta")
    testImplementation("io.kotlintest:kotlintest-runner-junit5:3.4.2")
}

group = "nl.vermeir"
version = "1.0-SNAPSHOT"

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}

application.mainClass.set("nl.vermeir.shop.CreateTableKt")