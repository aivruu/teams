dependencies {
  api(project(":${rootProject.name}-infrastructure"))

  compileOnlyApi(libs.annotations)
  compileOnlyApi(libs.mariadb.driver)
  compileOnlyApi(libs.hikaricp)
}