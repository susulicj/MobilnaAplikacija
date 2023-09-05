package com.example.projekatmobilne.ViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.projekatmobilne.DataClasses.Comment
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AddCommentViewModel : ViewModel() {
    private val database = FirebaseFirestore.getInstance()
    private val komentarRef = database.collection("komentari")
    private val commentsLiveData = MutableLiveData<List<Comment>>()


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

    fun getCommentsLiveData(apartmanId: String, callback: (List<Comment>) -> Unit) {
        osveziKomentare(apartmanId, callback)
    }

    private fun osveziKomentare(apartmanId: String, callback: (List<Comment>) -> Unit) {
        komentarRef.whereEqualTo("verifikacioniKodApartman", apartmanId)
            .get()
            .addOnSuccessListener { documents ->
                val komentari = mutableListOf<Comment>()
                for (document in documents) {
                    val komentar = document.toObject(Comment::class.java)
                    komentari.add(komentar)
                }
                callback(komentari) // Pozovite callback sa listom komentara
            }
            .addOnFailureListener { exception ->

                callback(emptyList()) // Pozovite callback sa praznom listom u slučaju greške
            }
    }









}