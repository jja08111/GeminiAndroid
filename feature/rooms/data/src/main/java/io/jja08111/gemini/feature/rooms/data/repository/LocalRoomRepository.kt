package io.jja08111.gemini.feature.rooms.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import io.jja08111.gemini.database.dao.RoomDao
import io.jja08111.gemini.database.entity.RoomEntity
import io.jja08111.gemini.database.extension.toDomain
import io.jja08111.gemini.model.Room
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date
import java.util.UUID
import javax.inject.Inject

class LocalRoomRepository @Inject constructor(
  private val roomDao: RoomDao,
) : RoomRepository {
  override fun getRoomStream(): Flow<PagingData<Room>> {
    return Pager(config = PagingConfig(pageSize = ROOM_PAGE_SIZE)) {
      roomDao.getRooms()
    }.flow.map { pagingData -> pagingData.map { it.toDomain() } }
  }

  override suspend fun createRoom(): Result<String> {
    return runCatching {
      val id = UUID.randomUUID().toString()
      roomDao.insert(RoomEntity(id = id, createdAt = Date().time, title = null))
      id
    }
  }

  companion object {
    private const val ROOM_PAGE_SIZE = 30
  }
}
