package com.example.projekatmobilne.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.projekatmobilne.DataClasses.User
import com.google.firebase.database.*

class UserViewModel: ViewModel() {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val usersRef: DatabaseReference = database.getReference("Users")

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
}