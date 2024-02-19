plugins {
  id("jja08111.android.library")
  id("jja08111.android.hilt")
  alias(libs.plugins.ktlint)
}

android {
  namespace = "io.jja08111.gemini.core.database"

  defaultConfig {
    ksp {
      arg("room.schemaLocation", "$projectDir/schemas")
    }
  }

  sourceSets.getByName("test") {
    assets.srcDir(files("$projectDir/schemas"))
  }
}

dependencies {

  implementation(project(":core:model"))

  implementation(libs.androidx.room.paging)
  implementation(libs.androidx.room.ktx)
  ksp(libs.androidx.room.compiler)
  implementation(libs.androidx.room.runtime)
  testImplementation(libs.junit)
  androidTestImplementation(libs.androidx.test.ext.junit)
}
