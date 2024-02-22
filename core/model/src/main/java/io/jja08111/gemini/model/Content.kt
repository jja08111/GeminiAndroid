package io.jja08111.gemini.model

enum class ContentType(val rawText: String) {
  Text("text"),
}

sealed class Content(val type: ContentType) {
  companion object {
    fun of(type: String, content: String): Content {
      return when (type) {
        ContentType.Text.rawText -> TextContent(text = content)
        else -> throw IllegalArgumentException()
      }
    }
  }
}

data class TextContent(
  val text: String,
) : Content(ContentType.Text)
