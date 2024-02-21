package io.jja08111.gemini.feature.rooms.ui

import androidx.paging.PagingData
import io.jja08111.gemini.model.Room
import kotlinx.coroutines.flow.Flow

data class RoomsUiState(val roomStream: Flow<PagingData<Room>>)
