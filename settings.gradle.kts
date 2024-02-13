pluginManagement {
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
include(":ui:list")
include(":ui:chat")
include(":data:chat")
include(":data:list")
