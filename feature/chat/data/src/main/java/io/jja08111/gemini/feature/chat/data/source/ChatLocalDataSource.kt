package io.jja08111.gemini.feature.chat.data.source

import io.jja08111.gemini.database.dao.MessageDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatLocalDataSource @Inject constructor(
  private val messageDao: MessageDao,
)
