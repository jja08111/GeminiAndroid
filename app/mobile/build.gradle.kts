plugins {
  id("jja08111.android.application")
  id("jja08111.android.application.compose")
  alias(libs.plugins.ktlint)
}

android {
  namespace = "io.github.jja08111.mobile"
  compileSdk = MobileConfigurations.COMPILE_SDK

  defaultConfig {
    applicationId = "io.github.jja08111.mobile"
    minSdk = MobileConfigurations.MIN_SDK
    targetSdk = MobileConfigurations.TARGET_SDK
    versionCode = MobileConfigurations.VERSION_CODE
    versionName = MobileConfigurations.VERSION_NAME

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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

  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.activity.compose)
  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.compose.material3)
  testImplementation(libs.junit)
  androidTestImplementation(libs.androidx.test.ext.junit)
  androidTestImplementation(libs.espresso.core)
  androidTestImplementation(platform(libs.androidx.compose.bom))
  debugImplementation(libs.androidx.compose.ui.tooling)
}
