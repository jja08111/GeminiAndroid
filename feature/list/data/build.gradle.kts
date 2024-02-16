plugins {
  id("jja08111.android.library")
  alias(libs.plugins.ktlint)
}

android {
  namespace = "io.github.jja08111.feature.list.data"
}

dependencies {

  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.appcompat)
  implementation(libs.material)
  testImplementation(libs.junit)
  androidTestImplementation(libs.androidx.test.ext.junit)
  androidTestImplementation(libs.espresso.core)
}
