plugins {
    kotlin("jvm") version "1.9.23"
    kotlin("plugin.serialization") version "2.1.20-Beta2"
}

group = "org.zhendz"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
    maven("https://mvn.mchv.eu/repository/mchv/")

}

dependencies {
    // Import BOM - version '3.4.0+td.1.8.26' (make sure this is correct!)
    implementation(platform("it.tdlight:tdlight-java-bom:3.4.0+td.1.8.26"))


    implementation("it.tdlight:tdlight-java") // Java 8 is supported if you use the following dependency classifier: `jdk8`
    implementation("it.tdlight:tdlight-natives"){artifact { classifier = "windows_amd64"}}

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")
    implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")
    implementation("com.github.demidko:telegram-storage:2025.02.15")
    implementation("com.github.sanctus-corvus:LuminaKt:0.1.1-2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")
    implementation("org.slf4j:slf4j-simple:2.0.16")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(19)
}