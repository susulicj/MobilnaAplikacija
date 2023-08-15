package com.example.projekatmobilne.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.projekatmobilne.ViewModel.RegisterViewModel
import com.example.projekatmobilne.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(RegisterViewModel::class.java)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnReg.setOnClickListener(){
            performSignUp()
        }

    }

    private fun performSignUp(){
        var email = binding.etEmail.text.toString()
        var korisnickoIme  = binding.ptKorisnickoIme.text.toString()
        var password = binding.passwordReg.text.toString()
        var imeIprezime = binding.etImeiPrezime.text.toString()
        var brojTelefona = binding.editTextNumber.text.toString()

        viewModel.registerUser(email, korisnickoIme, password, imeIprezime, brojTelefona)

        viewModel.registrationStatus.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "Uspesno", Toast.LENGTH_LONG).show()

            }
        }

        viewModel.errorMessage.observe(this) { message ->
            if (message.isNotEmpty()) {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }
        }






    }

}