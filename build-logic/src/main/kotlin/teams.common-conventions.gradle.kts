import com.diffplug.gradle.spotless.FormatExtension
import org.gradle.accessors.dm.LibrariesForLibs
import java.util.Date

plugins {
  id("com.diffplug.spotless")
  id("net.kyori.indra")
}

indra {
  javaVersions {
    target(21)
    minimumToolchain(21)
    strictVersions(true)
  }
}

spotless {
  fun FormatExtension.applyCommon() {
    trimTrailingWhitespace()
    endWithNewline()
  }
  java {
    licenseHeaderFile(rootProject.file("header/header.txt"))
    importOrderFile(rootProject.file(".spotless/importorder.txt"))

    removeUnusedImports()
    applyCommon()
  }
  kotlin {
    applyCommon()
  }
}

val libs = extensions.getByType(LibrariesForLibs::class)

repositories {
  mavenLocal()
  mavenCentral()
  maven("https://repo.papermc.io/repository/maven-public/")
  maven("https://repo.helpch.at/releases/")
  maven("https://repo.codemc.io/repository/maven-releases/")
  maven("https://repo.codemc.io/repository/maven-snapshots/")
}

dependencies {
  compileOnlyApi(libs.annotations)
}

tasks {
  jar {
    manifest {
      attributes(
        "Specification-Version" to project.version,
        "Specification-Vendor" to "aivruu",
        "Implementation-Build-Date" to Date()
      )
    }
  }

  javadoc {
    options.encoding = Charsets.UTF_8.name()
  }

  compileJava {
    options.encoding = Charsets.UTF_8.name()
    dependsOn(spotlessApply)
    options.compilerArgs.add("-parameters")
  }
}