plugins {
    kotlin("jvm") version "2.1.0"
    id("me.champeau.jmh") version "0.7.1"
}

group = "org.example"
version = "0.1.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))

    jmh("org.openjdk.jmh:jmh-core:1.36")
    jmh("org.openjdk.jmh:jmh-generator-annprocess:1.36")
    jmh(kotlin("stdlib"))
    jmh(kotlin("reflect"))
    jmh("org.jetbrains.kotlinx:kotlinx-io-core:0.6.0")
    jmh("org.jetbrains.kotlinx:kotlinx-io-bytestring:0.6.0")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(20)
}

jmh {
    profilers.set(listOf("gc"))
    includes.add(".*Benchmark.*")
    failOnError.set(true)
    resultFormat.set("JSON")
    @Suppress("DEPRECATION")
    resultsFile.set(project.file("${project.buildDir}/reports/jmh/results.json"))
}