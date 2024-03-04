plugins {
  id("jja08111.android.library")
  id("jja08111.android.hilt")
  alias(libs.plugins.google.secrets)
  alias(libs.plugins.ktlint)
}

android {
  namespace = "io.jja08111.gemini.feature.chat.data"
}

secrets {
  propertiesFileName = "secrets.properties"
  defaultPropertiesFileName = "secrets.defaults.properties"
}

dependencies {

  implementation(project(":core:common"))
  implementation(project(":core:model"))
  implementation(project(":core:database"))

  implementation(libs.androidx.room.paging)

  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.appcompat)
  implementation(libs.material)
  implementation(libs.google.generative.ai)
  testImplementation(libs.junit)
  androidTestImplementation(libs.androidx.test.ext.junit)
  androidTestImplementation(libs.espresso.core)
}
