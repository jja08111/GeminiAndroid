package io.jja08111.gemini.model

enum class ContentType {
  Text,
}

sealed class Content(val type: ContentType) {
  companion object {
    fun of(type: ContentType, content: String): Content {
      return when (type) {
        ContentType.Text -> TextContent(text = content)
      }
    }
  }
}

data class TextContent(
  val text: String,
) : Content(ContentType.Text)
