package io.jja08111.gemini.database.extension

import io.jja08111.gemini.database.entity.RoomWithActivatedTime
import io.jja08111.gemini.model.Room

fun RoomWithActivatedTime.toDomain() =
  Room(
    id = room.id,
    title = room.title,
    createdAt = room.createdAt,
    activatedAt = activatedAt,
  )
