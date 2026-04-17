plugins {
  id("teams.common-conventions")
}

dependencies {
  api(project(":${rootProject.name}-infrastructure"))

  compileOnly(libs.mariadb.driver)
  compileOnly(libs.hikaricp)
}