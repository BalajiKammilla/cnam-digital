object Lib {
    // https://mvnrepository.com/artifact/org.slf4j/slf4j-api
    const val SL4J_API = "org.slf4j:slf4j-api:2.0.6"

    // ========================================================================
    // ---{ Jakarta }---
    // ========================================================================

    // https://mvnrepository.com/artifact/jakarta.inject/jakarta.inject-api
    const val JAKARTA_INJECT_API = "jakarta.inject:jakarta.inject-api:2.0.1"

    // https://mvnrepository.com/artifact/jakarta.validation/jakarta.validation-api
    const val JAKARTA_VALIDATION_API = "jakarta.validation:jakarta.validation-api:3.0.2"

    // https://mvnrepository.com/artifact/jakarta.persistence/jakarta.persistence-api
    // https://central.sonatype.com/artifact/jakarta.persistence/jakarta.persistence-api/3.1.0
    const val JAKARTA_PERSISTENCE_API = "jakarta.persistence:jakarta.persistence-api:3.1.0"

    // https://mvnrepository.com/artifact/jakarta.transaction/jakarta.transaction-api
    const val JAKARTA_TRANSACTION_API = "jakarta.transaction:jakarta.transaction-api:2.0.1"

    // https://mvnrepository.com/artifact/jakarta.ws.rs/jakarta.ws.rs-api
    const val JAKARTA_WS_RS_API = "jakarta.ws.rs:jakarta.ws.rs-api:3.1.0"

    // https://mvnrepository.com/artifact/jakarta.enterprise/jakarta.enterprise.cdi-api
    const val JAKARTA_ENTERPRISE_CDI_API = "jakarta.enterprise:jakarta.enterprise.cdi-api:4.0.1"

    // https://mvnrepository.com/artifact/jakarta.ws.rs/jakarta.ws.rs-api
    const val JAKARTA_ANNOTATION_API = "jakarta.annotation:jakarta.annotation-api:2.1.1"

    // ========================================================================
    // ---{ Eclipse Microprofile }---
    // ========================================================================

    // https://mvnrepository.com/artifact/org.eclipse.microprofile.openapi/microprofile-openapi-api
    const val MICROPROFILE_OPENAPI_VERSION = "3.1"
    const val MICROPROFILE_OPENAPI_API =
        "org.eclipse.microprofile.openapi:microprofile-openapi-api:$MICROPROFILE_OPENAPI_VERSION"

    // ========================================================================
    // ---{ Jackson }---
    // ========================================================================

    // https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-annotations
    const val JACKSON_ANNOTATIONS = "com.fasterxml.jackson.core:jackson-annotations:2.14.2"

    // ========================================================================
    // ---{ Quarkus }---
    // ========================================================================

    const val QUARKUS_ARC = "io.quarkus:quarkus-arc"

    const val QUARKUS_SECURITY = "io.quarkus:quarkus-security"

    const val QUARKUS_JUNIT = "io.quarkus:quarkus-junit5"

    const val SMALLRYE_CONFIG_CORE = "io.smallrye.config:smallrye-config-core:3.2.1"

    // ========================================================================
    // ---{ Database }---
    // ========================================================================

    const val POSTGRESQL_DRIVER = "org.postgresql:postgresql:${Versions.postgresql}"

    // ========================================================================
    // ---{ Testing }---
    // ========================================================================

    const val OPEN_TABLE_EMBEDED_POSTGRESS = "com.opentable.components:otj-pg-embedded:1.0.1"
    const val AWAITILITY = "org.awaitility:awaitility:4.2.0"
    const val AWAITILITY_KOTLIN = "org.awaitility:awaitility-kotlin:4.2.0"

    // testImplementation
}
