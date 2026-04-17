plugins {
  id("teams.common-conventions")
}

dependencies {
  api(project(":${rootProject.name}-api"))

  compileOnly(libs.packetevents)
}