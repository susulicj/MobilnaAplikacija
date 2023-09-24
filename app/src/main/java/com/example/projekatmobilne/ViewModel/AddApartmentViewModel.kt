package com.example.projekatmobilne.ViewModel


import androidx.lifecycle.ViewModel
import com.example.projekatmobilne.DataClasses.Apartman
import com.example.projekatmobilne.DataClasses.User
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.tasks.await
import java.time.LocalDate

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
            "sprat" to apartman.sprat,
            "datumKreiranja" to apartman.datumKreiranja,
            "brojStana" to apartman.brojStana,
            "brojZgrade" to apartman.brojZgrade,
            "user" to apartman.user,

        )

        apartmanRef.document(apartman.verifikacioniKod!!).set(apartmanData)
            .addOnSuccessListener { documentReference ->

            }
            .addOnFailureListener { e ->

            }
    }


  fun dodajOcenuApartmanu(apartmanID: String, novaOcena: Int){
      apartmanRef.document(apartmanID).update("listaOcena", FieldValue.arrayUnion(novaOcena))
          .addOnSuccessListener {


          }
          .addOnFailureListener{

          }

  }



    suspend fun preuzmiSveApartmane(): List<Apartman> {
        return try {
            val querySnapshot = apartmanRef.get().await()
            val listaApartmana = mutableListOf<Apartman>()
            for (document in querySnapshot.documents) {
                val podaciApartmana = document.data

                val latlngData = podaciApartmana?.get("latlng") as? HashMap<String, Double>

                if (latlngData != null && latlngData.containsKey("latitude") && latlngData.containsKey("longitude")) {
                    val latitude = latlngData["latitude"]!!
                    val longitude = latlngData["longitude"]!!
                    val latLng = LatLng(latitude, longitude)

                    val userData = podaciApartmana["user"] as HashMap<String, Any>?
                    val user: User? = if (userData != null) {
                        User(
                            email = userData["email"] as String?,
                            korisnickoIme = userData["korisnickoIme"] as String?,
                            imeiPrezime = userData["ImeiPrezime"] as String?,
                            brojTelefona = userData["brojTelefona"] as String?,
                            poeni = userData["poeni"] as Int?,
                            profileImageUrl = userData["profileImageUrl"] as String?
                        )
                    } else {
                        null
                    }

                    val apartman = Apartman(
                        podaciApartmana["adresa"] as String,
                        podaciApartmana["povrsina"] as Double,
                        podaciApartmana["brojSoba"] as Long,
                        podaciApartmana["brojTelefona"] as Long,
                        podaciApartmana["email"] as String,
                        latLng,
                        podaciApartmana["verifikacioniKod"] as String,
                        podaciApartmana["posecnaOcena"] as Double?,
                        podaciApartmana["listaOcena"] as MutableList<Int>?,
                        podaciApartmana["sprat"] as Long?,
                        podaciApartmana["datumKreiranja"] as String?,
                        podaciApartmana["brojStana"] as Long?,
                        podaciApartmana["brojZgrade"] as Long?,
                        user
                    )
                    listaApartmana.add(apartman)
                }
            }
            listaApartmana
        } catch (e: Exception) {
            println("Greska prilikom izvršavanja upita: $e")
            emptyList()
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
                val prosecnaOcena = it.get("prosecnaOcena") as Double?
                if(prosecnaOcena != null){
                    callback(prosecnaOcena)
                }
            }
    }

    suspend fun proveriDuplikatApartmana(apartman: Apartman): Boolean {
        database.collection("apartmani")

        val querySnapshot = apartmanRef
            .whereEqualTo("adresa", apartman.adresa)
            .whereEqualTo("sprat", apartman.sprat)
            .whereEqualTo("brojZgrade", apartman.brojZgrade)
            .whereEqualTo("brojStana", apartman.brojStana)
            .whereEqualTo("latlng", apartman.latlng)
            .get()
            .await()

        return !querySnapshot.isEmpty
    }


    suspend fun deleteApartment(apartmentId: String) {
        try {
            database.collection("apartmani")
                .document(apartmentId)
                .delete()
                .await()

        } catch (e: Exception) {

        }
    }




}

