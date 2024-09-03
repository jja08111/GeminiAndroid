package io.jja08111.gemini.core.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test

class CrossFadeTest {
  @get:Rule
  val composeRule = createComposeRule()

  @Test
  fun shouldSwitchToSecondComponent() {
    var showFirst by mutableStateOf(true)
    composeRule.setContent {
      CrossFade(
        showFirst = showFirst,
        firstContent = { FirstComponent() },
        secondContent = { SecondComponent() },
      )
    }

    composeRule.onNodeWithText(FIRST_TITLE).assertIsDisplayed()
    composeRule.onNodeWithText(SECOND_TITLE).assertIsNotDisplayed()

    showFirst = false

    composeRule.onNodeWithText(FIRST_TITLE).assertIsNotDisplayed()
    composeRule.onNodeWithText(SECOND_TITLE).assertIsDisplayed()
  }

  @Test
  fun shouldSwitchToFirstComponent() {
    var showFirst by mutableStateOf(false)
    composeRule.setContent {
      CrossFade(
        showFirst = showFirst,
        firstContent = { FirstComponent() },
        secondContent = { SecondComponent() },
      )
    }

    composeRule.onNodeWithText(FIRST_TITLE).assertIsNotDisplayed()
    composeRule.onNodeWithText(SECOND_TITLE).assertIsDisplayed()

    showFirst = true

    composeRule.onNodeWithText(FIRST_TITLE).assertIsDisplayed()
    composeRule.onNodeWithText(SECOND_TITLE).assertIsNotDisplayed()
  }

  @Suppress("TestFunctionName")
  @Composable
  private fun FirstComponent() {
    Text(text = FIRST_TITLE)
  }

  @Suppress("TestFunctionName")
  @Composable
  private fun SecondComponent() {
    Text(text = SECOND_TITLE)
  }

  companion object {
    private const val FIRST_TITLE = "First"
    private const val SECOND_TITLE = "Second"
  }
}
