package io.jja08111.gemini.feature.rooms.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.jja08111.gemini.feature.rooms.data.repository.RoomRepository
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class RoomsViewModel @Inject constructor(
  roomRepository: RoomRepository,
) : ViewModel(), ContainerHost<RoomsUiState, RoomsSideEffect> {
  override val container = container<RoomsUiState, RoomsSideEffect>(
    RoomsUiState(roomStream = roomRepository.getRoomStream()),
  )
}
