package com.example.projekatmobilne.DataClasses

data class Comment(
      val tekst: String? = null,
      val user: User? = null,
      val apartman: Apartman? = null,
      val verifikacioniKodApartman: String? = null
)
