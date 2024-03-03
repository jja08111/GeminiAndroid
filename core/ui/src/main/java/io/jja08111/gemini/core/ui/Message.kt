package io.jja08111.gemini.core.ui

import android.content.Context
import androidx.annotation.StringRes

sealed class Message {
  data class Primitive(val value: String) : Message()

  data object Empty : Message()

  class Resource(
    @StringRes val resId: Int,
    vararg val args: Any,
  ) : Message()

  fun asString(context: Context): String {
    return when (this) {
      is Empty -> ""
      is Primitive -> value
      is Resource -> context.getString(resId, *args)
    }
  }
}
