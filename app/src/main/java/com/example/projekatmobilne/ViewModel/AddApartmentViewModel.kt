package com.example.projekatmobilne.ViewModel

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.example.projekatmobilne.DataClasses.Apartman
import com.example.projekatmobilne.DataClasses.User
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class AddApartmentViewModel : ViewModel() {
    private val database  = FirebaseFirestore.getInstance()
    private val apartmanRef = database.collection("apartmani")
    private lateinit var currentFirebaseUser: FirebaseUser
    private lateinit var currentUser: User

    fun dodajApartman(apartman: Apartman) {
        val apartmanData = hashMapOf(
            "adresa" to apartman.adresa,
            "povrsina" to apartman.povrsina,
            "brojSoba" to apartman.brojSoba,
            "brojTelefona" to apartman.brojTelefona,
            "email" to apartman.email,
            "latlng" to apartman.latlng,
            "verifikacioniKod" to apartman.verifikacioniKod,
            "user" to apartman.user,

        )

        apartmanRef.document(apartman.verifikacioniKod!!).set(apartmanData)
            .addOnSuccessListener { documentReference ->

            }
            .addOnFailureListener { e ->

            }
    }

    fun preuzmiSveApartmane(callback: (List<Apartman>) -> Unit) {
        apartmanRef.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            if (firebaseFirestoreException != null) {

                println("Greska prilikom osluskivanja promena: $firebaseFirestoreException")
                callback(emptyList())
                return@addSnapshotListener
            }

            val listaApartmana = mutableListOf<Apartman>()
            for (document in querySnapshot!!.documents) {
                val podaciApartmana = document.data

                val latlngData = podaciApartmana!!["latlng"] as? HashMap<String, Double>

                if (latlngData != null && latlngData.containsKey("latitude") && latlngData.containsKey("longitude")) {
                    val latitude = latlngData["latitude"]!!
                    val longitude = latlngData["longitude"]!!
                    val latLng = LatLng(latitude, longitude)

                    val apartman = Apartman(
                        podaciApartmana!!["adresa"] as String,
                        podaciApartmana["povrsina"] as Double,
                        podaciApartmana["brojSoba"] as Long,
                        podaciApartmana["brojTelefona"] as Long,
                        podaciApartmana["email"] as String,
                        latLng,
                        podaciApartmana["verifikacioniKod"] as String
                    )
                    listaApartmana.add(apartman)
                }
            }
            callback(listaApartmana)
        }
    }



}