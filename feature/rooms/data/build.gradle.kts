plugins {
  id("jja08111.android.library")
  id("jja08111.android.hilt")
  alias(libs.plugins.ktlint)
}

android {
  namespace = "io.jja08111.gemini.feature.rooms.data"
}

dependencies {

  implementation(project(":core:model"))
  implementation(project(":core:database"))

  implementation(libs.androidx.room.paging)

  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.appcompat)
  implementation(libs.material)
  testImplementation(libs.junit)
  androidTestImplementation(libs.androidx.test.ext.junit)
  androidTestImplementation(libs.espresso.core)
}
