package io.jja08111.gemini.feature.chat.data.exception

class EmptyMessageGroupsOnRegenerationException : Exception(
  "Message group list is empty when regenerating response",
)
