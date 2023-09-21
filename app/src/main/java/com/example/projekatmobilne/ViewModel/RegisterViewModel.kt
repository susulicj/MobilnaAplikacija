package com.example.projekatmobilne.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.projekatmobilne.DataClasses.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegisterViewModel : ViewModel() {
    private  var auth : FirebaseAuth = FirebaseAuth.getInstance()
    private  var database : DatabaseReference = FirebaseDatabase.getInstance().getReference("Users")


    private val _registrationStatus = MutableLiveData<Boolean>()
    val registrationStatus: LiveData<Boolean>
        get() = _registrationStatus

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String>
        get() = _errorMessage

    fun registerUser(
        email: String,
        password: String,
        korisnickoIme: String,
        imeIprezime: String,
        brojTelefona: String,
        profileImageUrl: String?
    ) {
        if (email.isEmpty() || korisnickoIme.isEmpty() || password.isEmpty() || imeIprezime.isEmpty() || brojTelefona.isEmpty()) {
            _errorMessage.value = "Molim vas popunite sva polja"
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        val newUser = User(email, korisnickoIme, imeIprezime, brojTelefona, 0, profileImageUrl)
                        database.child(korisnickoIme).setValue(newUser)
                                 .addOnSuccessListener {
                                      _registrationStatus.value = true

                                 }
                                 .addOnFailureListener {
                                      _errorMessage.value = "Neuspesno pvde"
                            }
                    }
                } else {
                    _errorMessage.value = "Neuspesno"
                }
            }
    }
}