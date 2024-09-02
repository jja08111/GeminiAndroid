plugins {
  id("jja08111.android.library")
  id("jja08111.android.library.compose")
}

android {
  namespace = "io.jja08111.gemini.core.ui"

  defaultConfig {
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }
}

dependencies {

  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.animation.android)
  androidTestImplementation(libs.androidx.ui.test.junit4.android)
  androidTestImplementation(libs.androidx.compose.material3)
  androidTestImplementation(libs.espresso.core)
}
