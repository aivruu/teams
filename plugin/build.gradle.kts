import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
  id("teams.common-conventions")
  alias(libs.plugins.blossom)
  alias(libs.plugins.shadow)
  alias(libs.plugins.paper.plugin)
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
    delete(shadowJar.get().destinationDirectory)
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

  compileOnly(libs.paper)
  compileOnly(libs.configurate)
  implementation(libs.bstats)

  compileOnly(libs.placeholder.legacy)
  compileOnly(libs.placeholder.modern)

  compileOnly(libs.mongodb)
}

paper {
  name = "AldrTeams"
  version = project.version.toString()
  author = "aivruu"

  serverDependencies {
    register("PlaceholderAPI") {
      required = false
    }
    register("MiniPlaceholders") {
      required = false
    }
    register("PacketEvents") {
      required = true
    }
  }

  val route = findProperty("group") as String
  main = "$route.${name}Plugin"
  loader = "$route.library.application.TeamsPluginLoader"
  apiVersion = "1.21"

  permissions {
    register("teams.*") {
      children = listOf(
        "teams.updates",
        "teams.command.help",
        "teams.command.reload",
        "teams.command.create",
        "teams.command.delete",
        "teams.command.select",
        "teams.command.modify",
        "teams.command.fetch"
      )
    }
    register("teams.updates") {
      description = "Allows to receive notifications about newer updates for the plugin."
      default = BukkitPluginDescription.Permission.Default.OP
    }
    register("teams.command.help") {
      description = "Shows the available commands for the plugin"
      default = BukkitPluginDescription.Permission.Default.OP
    }
    register("teams.command.reload") {
      description = "Reloads the plugin"
      default = BukkitPluginDescription.Permission.Default.OP
    }
    register("teams.command.create") {
      description = "Allows to create a new tag."
      default = BukkitPluginDescription.Permission.Default.OP
    }
    register("teams.command.delete") {
      description = "Deletes a given tag."
      default = BukkitPluginDescription.Permission.Default.OP
    }
    register("teams.command.select") {
      description = "Opens the tags-selector menu."
      default = BukkitPluginDescription.Permission.Default.OP
    }
    register("teams.command.fetch") {
      description = "Allows to fetch information about the given tag."
      default = BukkitPluginDescription.Permission.Default.OP
    }
  }
}

fun DependencyHandlerScope.includeInfrastructureImplementations() {
  sequenceOf("json", "mongodb", "mariadb").forEach { api(project(":${rootProject.name}-infrastructure-$it")) }
}
