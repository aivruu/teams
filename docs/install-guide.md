# How I can implement the plugin's API in my project

The plugin's API can be imported by using a dependency-manager and download the dependency from JitPack's repository (`jitpack.io`). Or use the plugin's jar to have
access to the API classes and events.

: [Latest Release](https://github.com/aivruu/teams/releases/latest)

```kotlin
repositories {
  maven("https://jitpack.io/")
}

dependencies {
  // Replace 'VERSION' with the latest-release.
  compileOnly("com.github.aivruu:teams:VERSION")
}
```

```xml
<repositories>
  <repository>
    <id>jitpack</id>
    <url>https://jitpack.io/</url>
  </repository>
</repositories>

<dependencies>
  <dependency>
    <groupId>com.github.aivruu</groupId>
    <artifactId>teams</artifactId>
    <version>VERSION</version>
    <scope>compile</scope>
  </dependency>
</dependencies>
```
