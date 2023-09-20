package com.example.projekatmobilne.DataClasses

import android.os.Parcelable
import android.provider.ContactsContract
import com.google.android.gms.maps.model.LatLng
import java.time.LocalDate
import java.util.Date


data class Apartman(
    val adresa: String? = null,
    val povrsina: Double? = null,
    val brojSoba: Long? = null,
    val brojTelefona: Long? = null,
    val email: String? = null,
    val latlng: LatLng? = null,
    val verifikacioniKod: String,
    val prosecnaOcena: Double? = null,
    var listaOcena: MutableList<Int>? = mutableListOf(),
    val sprat: Long? = null,
    var datumKreiranja: String? = null,
    val user: User? = null
)