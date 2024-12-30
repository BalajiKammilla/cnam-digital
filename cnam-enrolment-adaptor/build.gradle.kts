plugins {
    id("cnam.kotlin")
    id("cnam.kotlin-jpa")
    id("cnam.quarkus-bom")
    id("cnam.quarkus-resteasy")
}

dependencies {
    implementation("dev.dry:dry-common")
    implementation("dev.dry:dry-alert")
    implementation("dev.dry:dry-audit")
    implementation("dev.dry:dry-core-jackson")
    implementation("dev.dry:dry-core")
    implementation("dev.dry:dry-user")
    implementation("dev.dry:dry-core-libphonenumber")

    implementation(project(":cnam-enrolment"))

    implementation(Lib.JAKARTA_INJECT_API)
    implementation(Lib.JAKARTA_WS_RS_API)
    implementation(Lib.JAKARTA_VALIDATION_API)
    implementation(Lib.MICROPROFILE_OPENAPI_API)
    implementation(Lib.QUARKUS_ARC)

    implementation("io.quarkus:quarkus-cache")
    implementation("io.quarkus:quarkus-scheduler")
    //implementation("io.quarkus:quarkus-vertx")
    implementation("io.quarkiverse.minio:quarkus-minio:3.1.0.Final")

    // ---{ Validation }---
    implementation("io.quarkus:quarkus-hibernate-validator")
    implementation("com.github.mhshams:jnbis:2.1.2")

    testImplementation(Lib.QUARKUS_JUNIT)
    testImplementation(Lib.OPEN_TABLE_EMBEDED_POSTGRESS)
    testImplementation(Lib.POSTGRESQL_DRIVER)
}
