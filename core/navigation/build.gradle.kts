plugins {
  id("jja08111.android.library")
  id("jja08111.android.library.compose")
}

android {
  namespace = "io.github.jja08111.core.navigation"
}

dependencies {

  implementation(libs.androidx.hilt.navigation.compose)
}
