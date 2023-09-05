package com.example.projekatmobilne.ViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.projekatmobilne.DataClasses.Apartman
import com.example.projekatmobilne.DataClasses.Comment
import com.example.projekatmobilne.DataClasses.User
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await

class AddCommentViewModel : ViewModel() {
    private val database = FirebaseFirestore.getInstance()
    private val komentarRef = database.collection("komentari")
    private val commentListLiveData = MutableLiveData<List<Comment>>()
    private val commentFetchingStatusLiveData = MutableLiveData<Boolean>()

    fun getIsFetchingLiveData(): LiveData<Boolean> {
        return commentFetchingStatusLiveData
    }

    fun setCommentList(commentList: List<Comment>) {
        commentListLiveData.value = commentList
    }
    fun dodajKomentar(komentar: Comment) {
        val komentarData = hashMapOf(
            "tekst" to komentar.tekst,
            "user" to komentar.user,
            "apartman" to komentar.apartman,
            "verifikacioniKodApartman" to komentar.verifikacioniKodApartman
        )

        komentarRef.add(komentarData)
            .addOnSuccessListener { documentReferencfe ->

            }
            .addOnFailureListener { e ->

            }
    }

    fun getComments(verifikacioniKodApartman: String) {
        commentFetchingStatusLiveData.postValue(true)

        database.collection("komentari")
            .whereEqualTo("verifikacioniKodApartman", verifikacioniKodApartman)
            .get()
            .addOnSuccessListener { querySnapshot: QuerySnapshot? ->
                val commentList = mutableListOf<Comment>()
                querySnapshot?.forEach { document ->
                    val commentData = document.data
                    val tekst = commentData["tekst"] as String?
                    val userMap = commentData["user"] as Map<String, Any>?
                    val apartmanMap = commentData["apartman"] as Map<String, Any>?
                    val verifikacioniKod = commentData["verifikacioniKodApartman"] as String?

                    val comment = Comment(
                        tekst = tekst,
                        user = userMap?.let { createUserFromMap(it) },
                        apartman = apartmanMap?.let { createApartmanFromMap(it) },
                        verifikacioniKodApartman = verifikacioniKod
                    )

                    commentList.add(comment)
                }
                setCommentList(commentList)
                commentFetchingStatusLiveData.postValue(false)
                Log.d("apartman", "Komentari su dohvaćeni: $commentList")
            }
    }

    fun getCommentListLiveData(): LiveData<List<Comment>> {
        Log.d("apartman", "Komentari su : ${commentListLiveData.value}")
        return commentListLiveData
    }

    fun createUserFromMap(userMap: Map<String, Any>): User? {
        val email = userMap["email"] as String?
        val korisnickoIme = userMap["korisnickoIme"] as String?
        val ImeiPrezime = userMap["ImeiPrezime"] as String?
        val brojTelefona = userMap["brojTelefona"] as String?

        if (email != null) {
            return User(email, korisnickoIme, ImeiPrezime, brojTelefona)
        } else {
            return null
        }
    }

    fun createApartmanFromMap(apartmanMap: Map<String, Any>): Apartman? {
        val adresa = apartmanMap["adresa"] as String?
        val povrsina = apartmanMap["povrsina"] as Double?
        val brojSoba = apartmanMap["brojSoba"] as Long?
        val brojTelefona = apartmanMap["brojTelefona"] as Long?
        val email = apartmanMap["email"] as String?
        val verifikacioniKod = apartmanMap["verifikacioniKod"] as String?
        val userMap = apartmanMap["user"] as Map<String, Any>?


        val latlngData = apartmanMap["latlng"] as Map<String, Any>?
        val latitude = latlngData?.get("latitude") as Double?
        val longitude = latlngData?.get("longitude") as Double?
        val latlng = if (latitude != null && longitude != null) {
            LatLng(latitude, longitude)
        } else {
            null
        }

        val user = userMap?.let { createUserFromMap(it) }

        if (verifikacioniKod != null) {
            return Apartman(adresa, povrsina, brojSoba, brojTelefona, email, latlng, verifikacioniKod, user)
        } else {
            return null
        }
    }








}