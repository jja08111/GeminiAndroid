package io.jja08111.gemini.core.ui

import android.content.Context
import androidx.annotation.StringRes

sealed class StringValue {
  data class Dynamic(val value: String) : StringValue()

  data object Empty : StringValue()

  class Resource(
    @StringRes val resId: Int,
    vararg val args: Any,
  ) : StringValue()

  fun asString(context: Context): String {
    return when (this) {
      is Empty -> ""
      is Dynamic -> value
      is Resource -> context.getString(resId, *args)
    }
  }
}
