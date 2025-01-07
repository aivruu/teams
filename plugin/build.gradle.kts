tasks {
  compileJava {
    options.encoding = "UTF-8"
  }

  processResources {
    filesMatching("paper-plugin.yml") {
      expand("version" to project.version)
    }
  }
}

dependencies {
  api(project(":${rootProject.name}-api"))
  api(project(":${rootProject.name}-adapt"))
  api(project(":${rootProject.name}-json"))
  api(project(":${rootProject.name}-mongodb"))

  compileOnlyApi(libs.paper)
  compileOnlyApi(libs.configurate)
  compileOnlyApi(libs.caffeine)

  api(libs.gui)
}