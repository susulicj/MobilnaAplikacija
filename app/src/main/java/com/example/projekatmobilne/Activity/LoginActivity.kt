package com.example.projekatmobilne.Activity

import com.example.projekatmobilne.ViewModel.LoginViewModel
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.projekatmobilne.ViewModel.UserViewModel
import com.example.projekatmobilne.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class LoginActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLoginBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var viewModel: LoginViewModel
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        binding.tvRegister.setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }


        binding.btnNazad1.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        binding.btnLogin1.setOnClickListener{
            performLogin()
        }


    }
    private fun performLogin() {
        val korisnickoIme = binding.ptEmail.text.toString()
        val password = binding.etPassword.text.toString()
        if(korisnickoIme.isEmpty() && password.isEmpty())
        {
            Toast.makeText(this@LoginActivity, "Morate popuniti polja", Toast.LENGTH_SHORT).show()
        }
        else{
        var rezultat : DataSnapshot?
        lifecycleScope.launch(Dispatchers.IO) {
            rezultat = userViewModel.vratiEmail(korisnickoIme)

            var email = rezultat!!.child(korisnickoIme).child("email").getValue(String::class.java)

            withContext(Dispatchers.Main) {
                if (rezultat != null) {
                    viewModel.loginUser(email!!, password, auth)

                    viewModel.loginSuccess.observe(this@LoginActivity) { success ->
                        if (success) {
                            Log.w("TAG", "Uspesna prijava")
                            val intent = Intent(this@LoginActivity, ProfileActivity::class.java)
                            startActivity(intent)
                        }
                    }

                    viewModel.errorMessage.observe(this@LoginActivity) { message ->
                        if (message.isNotEmpty()) {
                            Log.w("TAG", "Neuspesna prijava")
                            Toast.makeText(this@LoginActivity, message, Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        "Ne postoji korisnik sa tim korisnickim imenoom",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
        }

        }
    }
}