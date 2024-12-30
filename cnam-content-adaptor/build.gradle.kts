plugins {
  id("cnam.kotlin")
  id("cnam.quarkus-resteasy")
}

dependencies {
  implementation(project(":cnam-content"))

  implementation("dev.dry:dry-core")
  implementation(Lib.MICROPROFILE_OPENAPI_API)
}
