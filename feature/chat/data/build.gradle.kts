plugins {
  id("jja08111.android.library")
  id("jja08111.android.hilt")
  alias(libs.plugins.google.secrets)
  alias(libs.plugins.ktlint)
}

android {
  namespace = "io.github.jja08111.feature.chat.data"
}

secrets {
  propertiesFileName = "secrets.properties"
  defaultPropertiesFileName = "secrets.defaults.properties"
}

dependencies {

  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.appcompat)
  implementation(libs.material)
  implementation(libs.google.generative.ai)
  testImplementation(libs.junit)
  androidTestImplementation(libs.androidx.test.ext.junit)
  androidTestImplementation(libs.espresso.core)
}
