package com.example.projekatmobilne.DataClasses

data class User(
    var email : String? = null,
    val korisnickoIme : String? = null,
    val imeiPrezime : String? = null,
    var brojTelefona : String? = null,
    val poeni: Int? = null,
    val profileImageUrl: String? = null)
