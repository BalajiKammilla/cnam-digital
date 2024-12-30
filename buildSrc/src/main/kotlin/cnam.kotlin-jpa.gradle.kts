plugins {
    id("cnam.kotlin")
    id("kotlin-allopen")
    id("kotlin-noarg")
}

dependencies {
    implementation(Lib.JAKARTA_PERSISTENCE_API)
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
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.Embeddable")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("org.eclipse.microprofile.config.inject.ConfigProperties")
    annotation("io.quarkus.arc.config.ConfigProperties")
}
