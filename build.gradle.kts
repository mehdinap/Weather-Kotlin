plugins {
    kotlin("jvm") version "1.8.0"
    kotlin("plugin.serialization") version "1.8.0"
    application
}


group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
//    implementation("io.ktor:ktor-client-core:2.0.0")
//    implementation("io.ktor:ktor-client-cio:2.0.0")
//    implementation("io.ktor:ktor-client-content-negotiation:2.0.0")
//    implementation("io.ktor:ktor-serialization-kotlinx-json:2.0.0")
//    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
//    testImplementation("org.jetbrains.kotlin:kotlin-test:1.8.0")
//    testImplementation("io.ktor:ktor-client-mock:2.0.0")
    implementation("io.ktor:ktor-client-core:2.0.0")
    implementation("io.ktor:ktor-client-cio:2.0.0")
    implementation("io.insert-koin:koin-core:3.1.2")

    implementation("io.ktor:ktor-client-content-negotiation:2.0.0")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.0.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")


    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(11)
}

application {
    mainClass.set("MainKt")
}