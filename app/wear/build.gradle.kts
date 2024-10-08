plugins {
  id("jja08111.android.application")
  id("jja08111.android.application.compose")
  id("jja08111.android.hilt")
  id("dagger.hilt.android.plugin")
  alias(libs.plugins.ktlint)
}

android {
  namespace = "io.jja08111.gemini.wear"
  compileSdk = WearConfigurations.COMPILE_SDK

  defaultConfig {
    applicationId = "io.jja08111.gemini.wear"
    minSdk = WearConfigurations.MIN_SDK
    targetSdk = WearConfigurations.TARGET_SDK
    versionCode = WearConfigurations.VERSION_CODE
    versionName = WearConfigurations.VERSION_NAME
    vectorDrawables {
      useSupportLibrary = true
    }
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules.pro",
      )
    }
  }
  packaging {
    resources {
      excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
  }
}

dependencies {

  implementation(libs.play.services.wearable)
  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.wear.compose.material)
  implementation(libs.androidx.wear.compose.foundation)
  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.core.splashscreen)
  androidTestImplementation(platform(libs.androidx.compose.bom))
  debugImplementation(libs.androidx.compose.ui.tooling)
}
