plugins {
    id("cnam.kotlin")
}

val quarkusPlatformVersion: String by project

dependencies {
    implementation(platform("io.quarkus.platform:quarkus-bom:${quarkusPlatformVersion}"))
}
