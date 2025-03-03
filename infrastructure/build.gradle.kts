dependencies {
  api(project(":${rootProject.name}-api"))

  compileOnlyApi(libs.annotations)
  compileOnlyApi(libs.gson)
}