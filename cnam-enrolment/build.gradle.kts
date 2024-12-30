plugins {
    id("cnam.kotlin")
    id("cnam.quarkus-bom")
}

dependencies {
    implementation("dev.dry:dry-common")
    implementation("dev.dry:dry-core")
    implementation("dev.dry:dry-audit")
    implementation("dev.dry:dry-alert")
    implementation("dev.dry:dry-user")

    implementation(Lib.SL4J_API)
    implementation(Lib.JAKARTA_INJECT_API)
    implementation(Lib.JAKARTA_PERSISTENCE_API)
    implementation(Lib.JAKARTA_TRANSACTION_API)
    implementation(Lib.JAKARTA_VALIDATION_API)
    implementation(Lib.JACKSON_ANNOTATIONS)
    implementation(Lib.QUARKUS_ARC)
}
