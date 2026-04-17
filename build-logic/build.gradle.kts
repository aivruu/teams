plugins {
  `kotlin-dsl`
}

dependencies {
  implementation(libs.build.indra)
  implementation(libs.build.spotless)

  compileOnly(files(libs::class.java.protectionDomain.codeSource.location))
}

java {
  sourceCompatibility = JavaVersion.VERSION_21
  targetCompatibility = JavaVersion.VERSION_21
}

repositories {
  gradlePluginPortal()
}

kotlin {
  jvmToolchain(21)
}