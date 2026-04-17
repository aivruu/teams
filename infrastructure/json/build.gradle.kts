plugins {
  id("teams.common-conventions")
}

dependencies {
  api(project(":${rootProject.name}-infrastructure"))

  compileOnly(libs.paper) // use shaded GSON version
}