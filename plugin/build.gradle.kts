plugins {
  id("teams.common-conventions")
  alias(libs.plugins.blossom)
  alias(libs.plugins.shadow)
}

tasks {
  compileJava {
    options.encoding = "UTF-8"
  }

  shadowJar {
    archiveExtension.set("")
    destinationDirectory.set(file("$rootDir/jars"))

    relocate("org.bstats", "${findProperty("group") as String}.bstats")
  }

  clean {
    delete(file("$rootDir/jars"))
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
  api(project(":${rootProject.name}-adapt-wrapper"))
  includeInfrastructureImplementations()

  compileOnlyApi(libs.paper)
  compileOnlyApi(libs.configurate)
  implementation(libs.bstats)

  compileOnlyApi(libs.placeholder.legacy)
  compileOnlyApi(libs.placeholder.modern)
}

fun DependencyHandlerScope.includeInfrastructureImplementations() {
  sequenceOf("json", "mongodb", "mariadb").forEach { api(project(":${rootProject.name}-infrastructure-$it")) }
}
