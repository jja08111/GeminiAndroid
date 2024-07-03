package io.jja08111.gemini.feature.rooms.data.repository

import androidx.paging.PagingData
import io.jja08111.gemini.model.Room
import kotlinx.coroutines.flow.Flow

interface RoomRepository {
  fun getRoomStream(): Flow<PagingData<Room>>
}
