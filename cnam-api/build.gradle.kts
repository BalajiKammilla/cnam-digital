plugins {
    id("cnam.kotlin")
    id("cnam.kotlin-jpa")
    id("cnam.quarkus")
    id("cnam.e2e-test")
    //id("org.liquibase.gradle") version "2.1.0"
}

dependencies {
    implementation("dev.dry:dry-common")
    implementation("dev.dry:dry-core")
    implementation("dev.dry:dry-core-adaptor")
    implementation("dev.dry:dry-core-geok")
    implementation("dev.dry:dry-core-jackson")
    implementation("dev.dry:dry-core-libphonenumber")
    implementation("dev.dry:dry-core-minio")
    implementation("dev.dry:dry-core-password4j")
    implementation("dev.dry:dry-core-qrcode-kotlin")
    implementation("dev.dry:dry-core-quarkus")

    implementation("dev.dry:dry-configuration")
    implementation("dev.dry:dry-configuration-adaptor")
    implementation("dev.dry:dry-alert")
    implementation("dev.dry:dry-alert-adaptor")
    implementation("dev.dry:dry-audit")
    implementation("dev.dry:dry-audit-adaptor")
    implementation("dev.dry:dry-user")
    implementation("dev.dry:dry-user-adaptor")

    implementation(project(":cnam-content"))
    implementation(project(":cnam-content-adaptor"))
    implementation(project(":cnam-enrolment"))
    implementation(project(":cnam-enrolment-adaptor"))
    implementation(project(":cnam-zetes-integration"))

    implementation("io.quarkus:quarkus-flyway")

    // https://mvnrepository.com/artifact/org.postgresql/postgresql
    runtimeOnly(Lib.POSTGRESQL_DRIVER)

    // ---{ Jib container image }---

    implementation("io.quarkus:quarkus-container-image-jib")

    // ---{ RESTEasy }---
    implementation("io.quarkus:quarkus-resteasy-reactive")
    implementation("io.quarkus:quarkus-resteasy-reactive-jackson")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // ---{ Validation }---
    implementation("io.quarkus:quarkus-hibernate-validator")

    // ---{ OpenAPI }---
    implementation("io.quarkus:quarkus-smallrye-openapi")

    // ---{ JWT }---
    implementation("io.quarkus:quarkus-smallrye-jwt")
    implementation("io.quarkus:quarkus-smallrye-jwt-build")

    // ---{ K8s }---
    //implementation("io.quarkus:quarkus-kubernetes")
    //implementation("io.quarkiverse.helm:quarkus-helm:1.0.6")

    // ---{ Testing }---

    testImplementation("io.rest-assured:rest-assured")
    testImplementation("io.rest-assured:kotlin-extensions")
    testImplementation("dev.dry:dry-test-rest-assured")
    testImplementation(Lib.OPEN_TABLE_EMBEDED_POSTGRESS)
    testImplementation("io.quarkiverse.cucumber:quarkus-cucumber:1.0.0")

    testImplementation("io.cucumber:cucumber-java:7.14.0")
    testImplementation("io.cucumber:cucumber-junit:7.14.0")
    // https://www.datafaker.net/
    testImplementation("net.datafaker:datafaker:1.9.0")
    testImplementation(Lib.AWAITILITY)
    testImplementation(Lib.AWAITILITY_KOTLIN)
/*
    ext {
        slf4jVersion  = '2.0.9'
        logbackVersion  = '1.4.11'

        junitJupiterVersion = '5.10.1'
        junitPlatformSuiteVersion = '1.10.1'
        cucumberVersion = '7.14.0'
    }
    */
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.1")
    testImplementation("org.junit.platform:junit-platform-suite:1.10.1")
    testImplementation("io.cucumber:cucumber-junit-platform-engine:7.14.0")
    /*
        liquibaseRuntime("org.liquibase:liquibase-core:4.16.1")
        liquibaseRuntime("org.liquibase:liquibase-groovy-dsl:3.0.2")
        liquibaseRuntime("info.picocli:picocli:4.6.1")
        liquibaseRuntime("org.liquibase.ext:liquibase-hibernate5:3.6")
        liquibaseRuntime("org.hsqldb:hsqldb:2.7.2")
        liquibaseRuntime("org.postgresql:postgresql:${Versions.postgresql}")
        liquibaseRuntime(sourceSets.main.get().output)
    */
}

tasks.e2eTest {
    useJUnitPlatform()
    include("civ/cnam/**")
}
/*
tasks["diffChangeLog"].dependsOn("classes")
liquibase {
    // https://github.com/liquibase/liquibase-gradle-plugin
    activities.register("main") {
        this.arguments = mapOf(
            "logLevel" to "info",
            "driver" to "org.hsqldb.jdbcDriver",
            "changeLogFile" to "src/main/resources/db/changelog/db.changelog-master.yaml",
            "url" to "jdbc:hsqldb:file:.liquibase/hsqldb",
            "referenceDriver" to "liquibase.ext.hibernate.database.connection.HibernateDriver",
            //"referenceUrl" to "hibernate:ejb3:civ.cnam?dialect=liquibase.ext.hibernate.database.HibernateGenericDialect&hibernate.physical_naming_strategy=org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy"
            "referenceUrl" to "hibernate:ss:civ.cnam?dialect=liquibase.ext.hibernate.database.HibernateGenericDialect&hibernate.physical_naming_strategy=org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy"
            //"referenceUrl" to "jpa:persistence:META-INF/persistence.xml"
            // hibernate.connection.driver_class=org.hsqldb.jdbcDriver&
        )
    }
}
*/