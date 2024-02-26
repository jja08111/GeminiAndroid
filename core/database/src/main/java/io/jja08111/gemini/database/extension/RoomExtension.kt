package io.jja08111.gemini.database.extension

import io.jja08111.gemini.database.entity.RoomWithActivatedTime
import io.jja08111.gemini.model.Room
import java.util.Date

fun RoomWithActivatedTime.toDomain() =
  Room(
    id = room.id,
    title = room.title,
    createdAt = Date(room.createdAt),
    activatedAt = Date(activatedAt),
  )
