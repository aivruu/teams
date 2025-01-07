plugins {
  alias(libs.plugins.paperweight)
}

dependencies {
  paperweight.paperDevBundle(libs.versions.paper.get())

  api(project(":${rootProject.name}-api"))
}