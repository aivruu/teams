@file:Suppress("UnstableApiUsage")

pluginManagement {
  includeBuild("build-logic")
}

plugins {
  id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "teams"

sequenceOf("api", "plugin", "infrastructure").forEach {
  val kerbalProject = "${rootProject.name}-$it"
  include(kerbalProject)
  project(":$kerbalProject").projectDir = file(it)
}

sequenceOf("wrapper", "packet").forEach {
  val kerbalProject = "${rootProject.name}-adapt-$it"
  include(kerbalProject)
  project(":$kerbalProject").projectDir = file("adapt/$it")
}

sequenceOf("json", "mongodb", "mariadb").forEach {
  val kerbalProject = "${rootProject.name}-infrastructure-$it"
  include(kerbalProject)
  project(":$kerbalProject").projectDir = file("infrastructure/$it")
}