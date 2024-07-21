package io.jja08111.gemini.feature.chat.data.source

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import dagger.hilt.android.qualifiers.ApplicationContext
import io.jja08111.gemini.model.createId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PromptImageLocalDataSource @Inject constructor(
  @ApplicationContext private val context: Context,
) {
  private fun createFileName(): String {
    return "prompt_${createId()}.jpg"
  }

  suspend fun saveImage(bitmap: Bitmap): String =
    withContext(Dispatchers.IO) {
      val filename = createFileName()
      val file = File(context.filesDir, filename)
      FileOutputStream(file).use { out ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
      }
      return@withContext file.absolutePath
    }

  suspend fun loadImage(imagePath: String): Bitmap =
    withContext(Dispatchers.IO) {
      return@withContext BitmapFactory.decodeFile(imagePath)
    }
}
