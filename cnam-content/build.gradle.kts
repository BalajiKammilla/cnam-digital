plugins {
  id("cnam.kotlin")
  id("cnam.quarkus-bom")
}

dependencies {
  implementation("dev.dry:dry-core")

  implementation(Lib.JAKARTA_INJECT_API)
  implementation(Lib.QUARKUS_ARC)
}
