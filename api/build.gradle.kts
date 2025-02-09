dependencies {
  compileOnlyApi(libs.paper)
  compileOnlyApi(libs.caffeine)
}

publishing {
  publications {
    create<MavenPublication>("maven") {
      from(components["java"])
    }
  }
}