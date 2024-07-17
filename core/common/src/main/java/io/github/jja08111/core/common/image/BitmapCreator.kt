package io.github.jja08111.core.common.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BitmapCreator @Inject constructor(
  @ApplicationContext private val context: Context,
) {
  fun create(imageUri: Uri): Bitmap {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
      ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, imageUri))
    } else {
      @Suppress("DEPRECATION")
      MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
    }
  }
}
