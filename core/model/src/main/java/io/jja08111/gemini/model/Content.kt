package io.jja08111.gemini.model

enum class ContentType(val rawText: String) {
  Text("text"),
}

sealed class Content(val type: ContentType) {
  companion object {
    fun of(rawText: String): Content {
      return when (rawText) {
        ContentType.Text.rawText -> TextContent(rawText)
        else -> throw IllegalArgumentException()
      }
    }
  }
}

data class TextContent(
  val text: String,
) : Content(ContentType.Text)
