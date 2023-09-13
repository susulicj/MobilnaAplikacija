package com.example.projekatmobilne.DataClasses

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng


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
    val user: User? = null,
)