plugins {
  id("jja08111.android.library")
  alias(libs.plugins.ktlint)
}

android {
  namespace = "io.github.jja08111.core.testing"
}

dependencies {

  implementation(libs.kotlin.coroutine.test)
  implementation(libs.junit)
}
