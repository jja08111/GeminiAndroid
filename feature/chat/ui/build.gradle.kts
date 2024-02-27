plugins {
  id("jja08111.android.library")
  id("jja08111.android.library.compose")
  id("jja08111.android.hilt")
  id("dagger.hilt.android.plugin")
  alias(libs.plugins.ktlint)
}

android {
  namespace = "io.jja08111.gemini.feature.chat.ui"
}

dependencies {

  implementation(project(":core:navigation"))
  implementation(project(":core:model"))
  implementation(project(":core:ui"))
  implementation(project(":feature:chat:data"))

  implementation(libs.androidx.activity.compose)
  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.hilt.navigation.compose)
  implementation(libs.androidx.lifecycle.compose)

  implementation(libs.androidx.room.paging)
  implementation(libs.androidx.paging.compose)

  implementation(libs.orbit.compose)
  implementation(libs.orbit.viewmodel)
  testImplementation(libs.orbit.test)

  implementation(libs.google.generative.ai)

  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.appcompat)
  implementation(libs.material)
  testImplementation(libs.junit)
  androidTestImplementation(libs.androidx.test.ext.junit)
  androidTestImplementation(libs.espresso.core)
  debugImplementation(libs.androidx.compose.ui.tooling)
}
