import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("java")
    id("kotlin")
    id("pl.allegro.tech.build.axion-release")
    kotlin("jvm")
}

val JVM_TARGET = "11"
val JAVA_VERSION = JavaVersion.VERSION_11
java {
    sourceCompatibility = JAVA_VERSION
    targetCompatibility = JAVA_VERSION
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = JVM_TARGET
    }
}

/*
tasks.withType<JavaExec> {
    jvmArgs = listOf("-Xms8g", "-Xmx8g")
}*/

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

scmVersion {
    branchVersionIncrementer.put("feature/.*", "incrementMinor")
    branchVersionIncrementer.put("bugfix/.*", "incrementPatch")
    versionIncrementer("incrementMinor")
    checks {
        uncommittedChanges.set(false)
    }
}

group = "civ.cnam"
version = "1.0.0-alpha.6"

repositories {
    mavenLocal()
    mavenCentral()
    gradlePluginPortal()
    maven {
        name = "DRY-Framework"
        url = uri("https://maven.pkg.github.com/dry-framework/dry-framework")
        credentials {
            username = System.getenv("DRY_USERNAME")
            password = System.getenv("DRY_TOKEN")
        }
    }
}

dependencies {
    // ---{ kotlin }---
    // ========================================================================

    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    //implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // ---{ dry }---
    // ========================================================================

    implementation(platform("dev.dry:dry-bom:${Versions.dryBom}"))
    implementation("dev.dry:dry-common")

    // ---{ junit }---
    // ========================================================================

    testImplementation(platform("org.junit:junit-bom:${Versions.junitBom}"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    // ---{ mockito }---
    // ========================================================================

    testImplementation(platform("org.mockito:mockito-bom:${Versions.mokitoBom}"))
    testImplementation("org.mockito:mockito-core")
    testImplementation("org.mockito:mockito-junit-jupiter")
    testImplementation("org.mockito.kotlin:mockito-kotlin:${Versions.mokitoKotlin}")

    // ---{ assertj }---
    // ========================================================================

    testImplementation("org.assertj:assertj-core:${Versions.assertj}")
}

tasks.test {
    useJUnitPlatform()
}

tasks.processResources {
    //filesMatching("META-INF/microprofile-config.properties") {
    filesMatching("application.properties") {
        expand(project.properties)
        //inputs.upToDateWhen { false }
        /*expand(mapOf(
            "buildInfoGroup" to group,
            "buildInfoVersion" to version
        ))*/
    }
}
