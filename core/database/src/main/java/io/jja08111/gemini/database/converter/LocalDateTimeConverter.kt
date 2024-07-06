package io.jja08111.gemini.database.converter

import androidx.room.TypeConverter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class LocalDateTimeConverter {
  private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

  @TypeConverter
  fun fromTimestamp(value: String?): LocalDateTime? {
    return value?.let { LocalDateTime.parse(it, formatter) }
  }

  @TypeConverter
  fun dateToTimestamp(date: LocalDateTime?): String? {
    return date?.format(formatter)
  }
}
