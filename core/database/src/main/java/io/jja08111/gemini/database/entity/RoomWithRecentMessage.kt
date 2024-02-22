package io.jja08111.gemini.database.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded

data class RoomWithRecentMessage(
  @Embedded val roomEntity: RoomEntity,
  // TODO: 아래보다 더 나은 방법 찾아보기
  @ColumnInfo(name = "message_id") val messageId: String?,
  @ColumnInfo(name = "message_content") val messageContent: String?,
  @ColumnInfo(name = "message_role") val messageRole: String?,
  @ColumnInfo(name = "message_type") val messageType: String?,
  @ColumnInfo(name = "message_created_at") val messageCreatedAt: Long?,
)
