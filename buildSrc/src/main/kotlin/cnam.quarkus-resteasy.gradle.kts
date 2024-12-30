plugins {
    id("cnam.kotlin")
    id("cnam.quarkus-bom")
}

dependencies {
    // ---{ RESTEasy }---

    implementation("io.quarkus:quarkus-resteasy-reactive")
    implementation("io.quarkus:quarkus-resteasy-reactive-jackson")

    // ---{ Jackson }---

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
}
