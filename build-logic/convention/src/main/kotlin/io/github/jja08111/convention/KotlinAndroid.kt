package io.github.jja08111.convention

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions

/**
 * Configure base Kotlin with Android options
 */
internal fun Project.configureKotlinAndroid(commonExtension: CommonExtension<*, *, *, *, *>) {
  commonExtension.apply {
    compileSdk = 34

    defaultConfig {
      minSdk = 24
    }

    compileOptions {
      sourceCompatibility = JavaVersion.VERSION_17
      targetCompatibility = JavaVersion.VERSION_17

      isCoreLibraryDesugaringEnabled = true
    }

    lint {
      abortOnError = false
    }

    kotlinOptions {
      // Treat all Kotlin warnings as errors (disabled by default)
      allWarningsAsErrors = properties["warningsAsErrors"] as? Boolean ?: false

      freeCompilerArgs = freeCompilerArgs +
        listOf(
          "-Xcontext-receivers",
          "-opt-in=kotlin.RequiresOptIn",
          // Enable experimental coroutines APIs, including Flow
          "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
          // Enable experimental compose APIs
          "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
          "-opt-in=androidx.lifecycle.compose.ExperimentalLifecycleComposeApi",
        )

      jvmTarget = JavaVersion.VERSION_17.toString()
    }

    dependencies {
      val libs = project.extensions.getByType<VersionCatalogsExtension>().named("libs")
      add("coreLibraryDesugaring", libs.findLibrary("android-desugar-jdk-libs").get())
    }
  }
}

fun CommonExtension<*, *, *, *, *>.kotlinOptions(block: KotlinJvmOptions.() -> Unit) {
  (this as ExtensionAware).extensions.configure("kotlinOptions", block)
}
