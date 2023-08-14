package com.example.projekatmobilne

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.projekatmobilne.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var db: FirebaseFirestore

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
        val user= hashMapOf(
            "Email" to email,
            "Korisnicko ime" to korisnickoIme,
            "Broj telefona" to brojTelefona,
            "Ime i prezime" to imeIprezime,

        )

        if (email.isEmpty() || korisnickoIme.isEmpty() || password.isEmpty() || imeIprezime.isEmpty() || brojTelefona.isEmpty()) {
            Toast.makeText(this, "Please, fill all fields", Toast.LENGTH_LONG).show()
            return
        }

        val Users = db.collection("USERS")
        val query = Users.whereEqualTo("Email", email).get()
                         .addOnSuccessListener {
                             tasks->
                             if(tasks.isEmpty)
                             {
                                 auth.createUserWithEmailAndPassword(email, password)
                                     .addOnCompleteListener(this){
                                         task->
                                         if(task.isSuccessful)
                                         {
                                             Users.document(email).set(user)
                                         }
                                         else
                                         {
                                             Toast.makeText(this, "Failed", Toast.LENGTH_LONG).show()
                                         }
                                     }
                             }
                             else
                             {
                                 Toast.makeText(this, "Vec postoji korisnik", Toast.LENGTH_LONG).show()
                             }
                         }



        /*auth.createUserWithEmailAndPassword(korisnickoIme, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG", "createUserWithEmail:success")
                    val user = auth.currentUser

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("TAG", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()

                }

            }.addOnFailureListener{
                Toast.makeText(this, "Failer", Toast.LENGTH_LONG).show()
            }*/
    }


}