package com.example.projekatmobilne.DataClasses

import android.widget.EditText
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseUser

data class Apartman(
    val adresa: String? = null,
    val povrsina: Number? = null,
    val brojSoba: Number? = null,
    val brojTelefona: Number? = null,
    val email: String? = null,
    val latlng: LatLng? = null,
    val user: User? = null
)