plugins {
  alias(libs.plugins.blossom)
}

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

sourceSets {
  main {
    blossom {
      javaSources {
        property("version", project.version.toString())
      }
    }
  }
}

dependencies {
  api(project(":${rootProject.name}-api"))
  api(project(":${rootProject.name}-adapt"))
  includeInfrastructureImplementations()

  compileOnlyApi(libs.paper)
  compileOnlyApi(libs.configurate)
  compileOnlyApi(libs.caffeine)

  compileOnlyApi(libs.placeholder.legacy)
  compileOnlyApi(libs.placeholder.modern)
}

fun includeInfrastructureImplementations() {
  val implementations = setOf("json", "mongodb", "mariadb")
  dependencies {
    for (implementation in implementations) {
      api(project(":${rootProject.name}-infrastructure-$implementation"))
    }
  }
}