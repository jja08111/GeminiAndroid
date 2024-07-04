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
    maven {
      setUrl("https://jitpack.io")
    }
  }
}

rootProject.name = "Gemini Demo"
include(":app:mobile")
include(":app:wear")
include(":feature:chat:ui")
include(":feature:chat:data")
include(":feature:rooms:ui")
include(":feature:rooms:data")
include(":core:model")
include(":core:database")
include(":core:navigation")
include(":core:ui")
include(":core:common")
