package com.example.projekatmobilne.Activity

import com.example.projekatmobilne.ViewModel.LoginViewModel
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.projekatmobilne.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class LoginActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLoginBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        binding.tvRegister.setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }


        binding.btnLogin1.setOnClickListener{
            performLogin()
        }


    }
    private fun performLogin(){
        val email = binding.ptEmail.text.toString()
        val password = binding.etPassword.text.toString()

        viewModel.loginUser(email, password, auth)

        viewModel.loginSuccess.observe(this) { success ->
            if (success) {
                Log.w("TAG", "Uspesna prijava")
                //da se povezemo na sledeci aktiviti fragment koji zelimo da se pojavi
            }
        }

        viewModel.errorMessage.observe(this) { message ->
            if (message.isNotEmpty()) {
                Log.w("TAG", "Neuspesna prijava")
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }

    }
}