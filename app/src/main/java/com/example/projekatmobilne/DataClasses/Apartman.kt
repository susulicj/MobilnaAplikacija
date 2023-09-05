package com.example.projekatmobilne.DataClasses

import android.widget.EditText
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseUser

data class Apartman(
    val adresa: String? = null,
    val povrsina: Double? = null,
    val brojSoba: Long? = null,
    val brojTelefona: Long? = null,
    val email: String? = null,
    val latlng: LatLng? = null,
    val verifikacioniKod: String,
    val user: User? = null,
)