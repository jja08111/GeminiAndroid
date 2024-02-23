package io.jja08111.gemini.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import io.jja08111.gemini.mobile.ui.navigation.MobileNavHost
import io.jja08111.gemini.mobile.ui.theme.GeminiDemoTheme

@AndroidEntryPoint
class MobileMainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContent {
      GeminiDemoTheme {
        MobileNavHost()
      }
    }
  }
}
