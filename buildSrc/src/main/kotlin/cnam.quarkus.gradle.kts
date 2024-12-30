plugins {
    id("cnam.kotlin")
    id("io.quarkus")
    id("kotlin-allopen")
    id("kotlin-noarg")
}

dependencies {
    // ---{ Quarkus }---

    implementation(enforcedPlatform("io.quarkus.platform:quarkus-bom:${Versions.quarkusPlatformBom}"))
    implementation("io.quarkus:quarkus-kotlin")
    implementation("io.quarkus:quarkus-arc")
    implementation("org.jboss.slf4j:slf4j-jboss-logmanager")
    implementation("io.quarkus:quarkus-micrometer-registry-prometheus")
    implementation("io.quarkus:quarkus-micrometer")

    testImplementation("io.quarkus:quarkus-junit5")

    // ---{ DRY }---

    implementation("dev.dry:dry-core-quarkus")

    testImplementation("dev.dry:dry-test-quarkus")
}

quarkus {
    buildForkOptions {
        maxHeapSize = "2g"
    }
    codeGenForkOptions {
        maxHeapSize = "128m"
    }
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.Embeddable")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.ws.rs.Path")
    annotation("jakarta.enterprise.context.ApplicationScoped")
    annotation("io.quarkus.test.junit.QuarkusTest")
    annotation("org.eclipse.microprofile.config.inject.ConfigProperties")
}

noArg {
    annotation("org.eclipse.microprofile.config.inject.ConfigProperties")
    annotation("io.quarkus.arc.config.ConfigProperties")
}
