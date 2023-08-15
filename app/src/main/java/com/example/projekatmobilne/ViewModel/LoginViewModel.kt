package com.example.projekatmobilne.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class LoginViewModel : ViewModel() {

    private val _loginSuccess = MutableLiveData<Boolean>()
    val loginSuccess: LiveData<Boolean>
        get() = _loginSuccess

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String>
        get() = _errorMessage

    fun loginUser(email: String, password: String, auth: FirebaseAuth) {
        if (email.isEmpty() || password.isEmpty()) {
            _errorMessage.value = "Molim Vas unesite podatke"
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                    task ->
                    if (task.isSuccessful) {
                        _loginSuccess.value = true
                    } else {
                        _errorMessage.value = "Authentication failed"
                    }
            }
    }
}
