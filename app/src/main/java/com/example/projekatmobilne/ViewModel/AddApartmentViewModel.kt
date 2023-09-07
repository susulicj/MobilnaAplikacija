package com.example.projekatmobilne.ViewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.projekatmobilne.DataClasses.Apartman
import com.example.projekatmobilne.DataClasses.Comment
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class AddApartmentViewModel : ViewModel() {
    private val database  = FirebaseFirestore.getInstance()
    private val apartmanRef = database.collection("apartmani")
    private var listenerRegistration: ListenerRegistration? = null

    fun dodajApartman(apartman: Apartman) {
        val apartmanData = hashMapOf(
            "adresa" to apartman.adresa,
            "povrsina" to apartman.povrsina,
            "brojSoba" to apartman.brojSoba,
            "brojTelefona" to apartman.brojTelefona,
            "email" to apartman.email,
            "latlng" to apartman.latlng,
            "verifikacioniKod" to apartman.verifikacioniKod,
            "prosecnaOcena" to apartman.prosecnaOcena,
            "listaOcena" to apartman.listaOcena,
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

  fun dodajOcenuApartmanu(apartmanID: String, novaOcena: Int){
      apartmanRef.document(apartmanID).update("listaOcena", FieldValue.arrayUnion(novaOcena))
          .addOnSuccessListener {


          }
          .addOnFailureListener{

          }

  }


    fun azuriranjeProsecneOcene(apartmanID: String, callback: (Double) -> Unit) {
        listenerRegistration = apartmanRef.document(apartmanID).addSnapshotListener { documentSnapshot, e ->
            if (e != null) {
                return@addSnapshotListener
            }

            if (documentSnapshot != null && documentSnapshot.exists()) {
                val listaOcena = documentSnapshot.get("listaOcena") as? List<Int>
                if (listaOcena != null) {

                    val prosecnaOcena = listaOcena.average()
                    apartmanRef.document(apartmanID).update("prosecnaOcena", prosecnaOcena)
                        .addOnSuccessListener {
                             callback(prosecnaOcena)
                        }
                        .addOnFailureListener { exception ->

                        }
                }
            }
        }
    }

    fun vratiProsecnuOcenu(apartmanID: String, callback: (Double) -> Unit){
        apartmanRef.document(apartmanID).get()
            .addOnSuccessListener {
                val prosecnaOcena = it.get("prosecnaOcena") as Double
                if(prosecnaOcena != null){
                    callback(prosecnaOcena)
                }
            }
    }

}

