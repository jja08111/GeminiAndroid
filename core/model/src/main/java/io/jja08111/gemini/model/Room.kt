package io.jja08111.gemini.model

import java.util.Date

data class Room(
  val id: String,
  val title: String?,
  val createdAt: Date,
  val activatedAt: Date,
)
