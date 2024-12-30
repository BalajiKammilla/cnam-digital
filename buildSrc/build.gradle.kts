plugins {
    `kotlin-dsl`
}

val kotlinVersion: String by project
val quarkusPluginVersion: String by project

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    // ---{ kotlin }---

    // https://kotlinlang.org/docs/gradle-configure-project.html
    // https://plugins.gradle.org/plugin/org.jetbrains.kotlin.jvm
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:${kotlinVersion}")
    // https://kotlinlang.org/docs/all-open-plugin.html
    // https://plugins.gradle.org/plugin/org.jetbrains.kotlin.plugin.allopen
    implementation("org.jetbrains.kotlin:kotlin-allopen:${kotlinVersion}")
    // https://kotlinlang.org/docs/no-arg-plugin.html#jpa-support
    // https://plugins.gradle.org/plugin/org.jetbrains.kotlin.plugin.noarg
    implementation("org.jetbrains.kotlin:kotlin-noarg:${kotlinVersion}")

    // ---{ quarkus }---

    implementation("io.quarkus:io.quarkus.gradle.plugin:${quarkusPluginVersion}")

    // ---{ axion-release }---
    implementation("pl.allegro.tech.build:axion-release-plugin:1.15.3")

    // Test Sets
    // - https://plugins.gradle.org/plugin/org.unbroken-dome.test-sets
    implementation("org.unbroken-dome.gradle-plugins:gradle-testsets-plugin:4.0.0")
}
