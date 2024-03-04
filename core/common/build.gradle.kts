plugins {
  id("jja08111.android.library")
  id("jja08111.android.hilt")
  alias(libs.plugins.ktlint)
}

android {
  namespace = "io.github.jja08111.core.common"
}

dependencies {

  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.appcompat)
  implementation(libs.material)
  testImplementation(libs.junit)
  androidTestImplementation(libs.androidx.test.ext.junit)
  androidTestImplementation(libs.espresso.core)
}
