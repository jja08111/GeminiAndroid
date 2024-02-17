pluginManagement {
  includeBuild("build-logic")
  repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
  }
}
dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    google()
    mavenCentral()
  }
}

rootProject.name = "Gemini Demo"
include(":app:mobile")
include(":app:wear")
include(":feature:chat:ui")
include(":feature:chat:data")
include(":feature:rooms:ui")
include(":feature:rooms:data")
