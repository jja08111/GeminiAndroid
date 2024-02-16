@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
  `kotlin-dsl`
}

java {
  sourceCompatibility = JavaVersion.VERSION_17
  targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
  compileOnly(libs.android.gradlePlugin)
  compileOnly(libs.kotlin.gradlePlugin)
}

gradlePlugin {
  plugins {
    register("androidApplicationCompose") {
      id = "jja08111.android.application.compose"
      implementationClass = "AndroidApplicationComposeConventionPlugin"
    }
    register("AndroidApplicationPlugin") {
      id = "jja08111.android.application"
      implementationClass = "AndroidApplicationConventionPlugin"
    }
    register("AndroidLibraryPlugin") {
      id = "jja08111.android.library"
      implementationClass = "AndroidLibraryConventionPlugin"
    }
  }
}
