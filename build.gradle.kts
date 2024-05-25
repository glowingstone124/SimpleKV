plugins {
    kotlin("jvm") version "1.9.23"
}

group = "ind.glowingstone"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-cbor:1.5.0")
    implementation("ch.qos.logback:logback-classic:1.2.11")
    implementation("org.slf4j:slf4j-api:1.7.32")
    implementation(platform("org.http4k:http4k-bom:5.21.0.0"))
    implementation("org.http4k:http4k-core")
    implementation("org.http4k:http4k-server-apache")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}