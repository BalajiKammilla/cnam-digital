plugins {
    id("cnam.kotlin")
    id("cnam.quarkus-bom")
}

dependencies {
    implementation("dev.dry:dry-common")
    implementation("dev.dry:dry-core")

    implementation(project(":cnam-enrolment"))

    implementation(Lib.JAKARTA_INJECT_API)
    implementation(Lib.MICROPROFILE_OPENAPI_API)
    implementation(Lib.QUARKUS_ARC)

    implementation("org.bouncycastle:bcprov-jdk15on:1.70")
    implementation("org.bouncycastle:bcmail-jdk15on:1.70")

    implementation("org.http4k:http4k-core:3.251.0")
}
