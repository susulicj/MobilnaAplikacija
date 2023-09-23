package com.example.projekatmobilne.ViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.projekatmobilne.DataClasses.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class UserViewModel: ViewModel() {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val usersRef: DatabaseReference = database.getReference("Users")
    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().reference


    private val _sortedUsers = MutableLiveData<List<User>>()
    val sortedUsers: LiveData<List<User>> get() = _sortedUsers

    fun sortiranjeKorisnika() {
        val query = usersRef.orderByChild("poeni")

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val userList = mutableListOf<User>()

                for (userSnapshot in dataSnapshot.children) {
                    val user = userSnapshot.getValue(User::class.java)
                    if (user != null) {
                        userList.add(user)
                    }
                }
                val sortedList = userList.sortedByDescending { it.poeni ?: 0 }

                _sortedUsers.value = sortedList
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

 /*   fun vratiTrenutngKorisnika(email: String, callback: (User?) -> Unit)
    {
        val query = databaseReference.child("Users").orderByChild("email").equalTo(email)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (userSnapshot in dataSnapshot.children) {
                        val user = userSnapshot.getValue(User::class.java)
                        callback(user)
                        return
                    }
                }
                callback(null) //
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Došlo je do greške
                callback(null)
            }
        })
    }*/
 suspend fun vratiTrenutngKorisnika(email: String): User? = withContext(Dispatchers.IO) {
     val query = databaseReference.child("Users").orderByChild("email").equalTo(email)

     return@withContext try {
         val dataSnapshot = query.get().await()

         if (dataSnapshot.exists()) {
             for (userSnapshot in dataSnapshot.children) {
                 val user = userSnapshot.getValue(User::class.java)
                 return@withContext user
             }
         }
         null
     } catch (e: Exception) {
        Log.d("prikai", "$e")
         null
     }
 }


    suspend fun vratiEmail(ime:String) : DataSnapshot?{
        return try{
            val query= usersRef.orderByChild("korisnickoIme").equalTo(ime).limitToFirst(1)
            val dataSnapshot = query.get().await()


            dataSnapshot

        }catch (e:Exception){
            Log.d("rezultatttt", "$e")
            null
        }
    }

    suspend fun proveriDuplikatKorisnika(ime: String): Boolean {
        return try {
            val query = usersRef.orderByChild("korisnickoIme").equalTo(ime).limitToFirst(1)
            val dataSnapshot = query.get().await()

            dataSnapshot.exists()
        } catch (e: Exception) {
           Log.d("rezultat", "$e")
            false
        }
    }








    /* suspend fun vratiTrenutngKorisnika(email: String): User? {
         return withContext(Dispatchers.IO) {
             val query = databaseReference.child("Users").orderByChild("email").equalTo(email)
             val dataSnapshot = query.get().await()

             if (dataSnapshot.exists()) {
                 for (userSnapshot in dataSnapshot.children) {
                     val user = userSnapshot.getValue(User::class.java)
                     return@withContext user
                 }
             }
             return@withContext null
         }
     }*/

}