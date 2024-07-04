package io.jja08111.gemini.model

import java.util.UUID

fun createId(): String {
  return UUID.randomUUID().toString()
}
