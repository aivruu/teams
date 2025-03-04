@file:Suppress("UnstableApiUsage")

rootProject.name = "teams"

sequenceOf(
  "api", "adapt", "plugin",
  "infrastructure", "infrastructure-mariadb", "infrastructure-mongodb", "infrastructure-json"
).forEach {
  val kerbalProject = ":${rootProject.name}-$it"
  include(kerbalProject)
  if (it.startsWith("infrastructure-")) {
    project(kerbalProject).projectDir = file("infrastructure/${it.substring(15)}")
  } else {
    project(kerbalProject).projectDir = file(it)
  }
}
