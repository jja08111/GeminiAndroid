package io.jja08111.gemini.model

enum class Role(val text: String) {
  User("user"),
  Model("model"),
  ;

  companion object {
    fun of(role: String): Role {
      return entries.find { it.text == role } ?: throw IllegalArgumentException()
    }
  }
}
