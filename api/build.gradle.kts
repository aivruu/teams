dependencies {
  compileOnlyApi(libs.paper)
}

publishing {
  publications {
    create<MavenPublication>("maven") {
      from(components["java"])
    }
  }
}