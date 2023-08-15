package com.example.projekatmobilne

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.projekatmobilne.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var database : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
        db = FirebaseFirestore.getInstance()


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




        if (email.isEmpty() || korisnickoIme.isEmpty() || password.isEmpty() || imeIprezime.isEmpty() || brojTelefona.isEmpty()) {
            Toast.makeText(this, "Please, fill all fields", Toast.LENGTH_LONG).show()
            return
        }


        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this){
                    task->
                if(task.isSuccessful)
                {
                    database = FirebaseDatabase.getInstance().getReference("Users")
                    val User = User(email, korisnickoIme, imeIprezime, brojTelefona)
                    database.child(korisnickoIme).setValue(User).addOnSuccessListener {

                        Toast.makeText(this, "uspesno", Toast.LENGTH_LONG).show()

                    }.addOnFailureListener{

                        Toast.makeText(this, "neuspesno", Toast.LENGTH_LONG).show()

                    }
                }
                else
                {
                    Toast.makeText(this, "Failed", Toast.LENGTH_LONG).show()
                }
            }



}

}