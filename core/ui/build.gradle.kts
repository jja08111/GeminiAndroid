plugins {
  id("jja08111.android.library")
  id("jja08111.android.library.compose")
}

android {
  namespace = "io.jja08111.gemini.core.ui"
}

dependencies {

  implementation(libs.androidx.compose.ui)
}
