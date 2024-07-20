package io.jja08111.gemini.feature.chat.data.model

sealed class AttachedImage {
  data class Uri(val uri: android.net.Uri) : AttachedImage()

  data class Bitmap(val bitmap: android.graphics.Bitmap) : AttachedImage()

  override fun equals(other: Any?): Boolean {
    if (other !is AttachedImage) {
      return false
    }
    if (other is Uri && this is Uri) {
      return this.uri == other.uri
    }
    if (other is Bitmap && this is Bitmap) {
      return this.bitmap == other.bitmap
    }
    return false
  }

  override fun hashCode(): Int {
    return when (this) {
      is Uri -> this.uri.hashCode()
      is Bitmap -> this.bitmap.hashCode()
    }
  }

  companion object {
    fun create(uri: android.net.Uri): AttachedImage {
      return Uri(uri)
    }

    fun create(bitmap: android.graphics.Bitmap): AttachedImage {
      return Bitmap(bitmap)
    }
  }
}
