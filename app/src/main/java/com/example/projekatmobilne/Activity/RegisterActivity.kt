package com.example.projekatmobilne.Activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.projekatmobilne.ViewModel.RegisterViewModel
import com.example.projekatmobilne.ViewModel.UserViewModel
import com.example.projekatmobilne.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel: RegisterViewModel
    private lateinit var userViewModel: UserViewModel
    private lateinit var myImage: ImageView
    private val cameraRequestId = 1222
    private var imageURL: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(RegisterViewModel::class.java)
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        myImage = binding.myImage


        if(ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED )
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA),cameraRequestId)
        binding.btnDodajFoografiju.setOnClickListener{
            val cameraInt = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraInt, cameraRequestId)
        }

        binding.btnReg.setOnClickListener(){
            performSignUp()
        }
        binding.textView3.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        binding.btnNazad.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        setContentView(binding.root)
    }
    /*automatski se poziva kad se zavrsi aktivnost koja je prekinula putam
    startactivityforResult
    slika je smestena u data.extras delu intenta
    izvlaci se kao bitmapa
     */

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == cameraRequestId && resultCode == Activity.RESULT_OK) {
            val image: Bitmap? = data?.extras?.get("data") as Bitmap
            myImage.setImageBitmap(image)

            val storageReference = FirebaseStorage.getInstance().reference
            val imageName = "korisnicko_ime_${System.currentTimeMillis()}.jpg"

            val baos = ByteArrayOutputStream()
            image?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val imageData = baos.toByteArray()

            val imageRef = storageReference.child("slike/$imageName")
            imageURL = imageName
            val uploadTask = imageRef.putBytes(imageData)

            uploadTask.addOnSuccessListener { taskSnapshot ->
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()
                    imageURL = imageName


                }
            }.addOnFailureListener { exception ->

            }
        }
    }


    private fun performSignUp(){
        var email = binding.etEmail.text.toString()
        var korisnickoIme  = binding.ptKorisnickoIme.text.toString()
        var password = binding.passwordReg.text.toString()
        var imeIprezime = binding.etImeiPrezime.text.toString()
        var brojTelefona = binding.editTextNumber.text.toString()

        var rezultat: Boolean = true
        lifecycleScope.launch(Dispatchers.IO) {
            rezultat = userViewModel.proveriDuplikatKorisnika(korisnickoIme)


            withContext(Dispatchers.Main) {
                if (rezultat) {
                    runOnUiThread {
                        Toast.makeText(this@RegisterActivity, "Vec postoji korisnik sa tim korisnickim imenom", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    viewModel.registerUser(email, password, korisnickoIme, imeIprezime, brojTelefona, imageURL)

                    viewModel.registrationStatus.observe(this@RegisterActivity) { success ->
                        if (success) {

                            runOnUiThread {
                                Toast.makeText(this@RegisterActivity, "Uspesno ste se registrovali", Toast.LENGTH_LONG).show()
                            }
                            binding.etEmail.text.clear()
                            binding.ptKorisnickoIme.text.clear()
                            binding.passwordReg.text.clear()
                            binding.etImeiPrezime.text.clear()
                            binding.editTextNumber.text.clear()
                            binding.myImage.setImageDrawable(null)


                        }
                    }

                    viewModel.errorMessage.observe(this@RegisterActivity) { message ->
                        if (message.isNotEmpty()) {
                            runOnUiThread {
                                Toast.makeText(this@RegisterActivity, message, Toast.LENGTH_LONG)
                                    .show()
                            }
                        }

                    }


                }
            }

    }






    }

}