package com.example.projekatmobilne.Fragments

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController

import com.example.projekatmobilne.R
import com.example.projekatmobilne.ViewModel.UserViewModel
import com.example.projekatmobilne.databinding.FragmentHomeProfileBinding
import com.example.projekatmobilne.databinding.FragmentUserProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


class UserProfileFragment : Fragment() {

    private lateinit var binding: FragmentUserProfileBinding
    private lateinit var currentFirebaseUser: FirebaseUser



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {

        binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        currentFirebaseUser = FirebaseAuth.getInstance().currentUser!!
        val userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)



        binding.imageButton.setOnClickListener{
            it.findNavController().navigate(R.id.action_userProfileFragment_to_homeProfileFragment)
        }
        userViewModel.vratiTrenutngKorisnika(currentFirebaseUser.email!!) { user ->
            if (user != null) {
                Log.d("user", "$user")
                binding.IDdobijenoKorisnickoIme.text = user.korisnickoIme
                binding.IdDobijeniMail.text = user.email
                binding.IdDobijeniPoeni.text = user.poeni.toString()
                binding.IdDobijenBrojTelefona.text = user.brojTelefona.toString()
                binding.IdDobijenoImeiPrezime.text = user.imeiPrezime
                preuzmiFotografiju(user.profileImageUrl,
                    imageView = binding.iwProfilePhoto,
                    onFailure = { exception ->

                    }
                )


            }
        }

        binding.button.setOnClickListener{
            it.findNavController().navigate(R.id.action_userProfileFragment_to_usersListFragment)
        }
        return binding.root
    }

            /* CoroutineScope(Dispatchers.Main).launch {
            try {
                val user = userViewModel.vratiTrenutngKorisnika(currentFirebaseUser.email!!)

                if (user != null) {
                    Log.d("user", "$user")
                    binding.IDdobijenoKorisnickoIme.text = user.korisnickoIme
                    binding.IdDobijeniMail.text = user.email
                    binding.IdDobijeniPoeni.text = user.poeni.toString()
                    binding.IdDobijenBrojTelefona.text = user.brojTelefona.toString()
                    binding.IdDobijenoImeiPrezime.text = user.imeiPrezime
                    preuzmiFotografiju(user.profileImageUrl,
                        imageView = binding.iwProfilePhoto,
                        onFailure = { exception ->
                            // Tretirajte grešku pri preuzimanju slike
                        }
                    )
                }
            } catch (exception: Exception) {
                // Tretirajte grešku ili prikažite poruku o grešci
            }
        }

        binding.button.setOnClickListener{
            it.findNavController().navigate(R.id.action_userProfileFragment_to_usersListFragment)
        }

        return binding.root
    }*/


              fun preuzmiFotografiju(imeSlike: String?, imageView: ImageView, onFailure: (Exception) -> Unit) {
        val storage = FirebaseStorage.getInstance()
        val storageReference = storage.reference

        if(imeSlike != null) {

            val imagePath = "slike/$imeSlike"

            val imageRef = storageReference.child(imagePath)

            imageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener { bytes ->


                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                imageView.setImageBitmap(bitmap)

            }.addOnFailureListener { exception ->
                onFailure(exception)
            }
        }else{
            imageView.setImageResource(R.drawable.images)
        }
    }
            /*fun preuzmiFotografiju(
                imeSlike: String?,
                imageView: ImageView,
                onFailure: (Exception) -> Unit
            ) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val bitmap = if (imeSlike != null) {
                            preuzmiSliku(imeSlike)
                        } else {
                            null
                        }

                        withContext(Dispatchers.Main) {
                            if (bitmap != null) {
                                imageView.setImageBitmap(bitmap)
                            } else {
                                imageView.setImageResource(R.drawable.images)
                            }
                        }
                    } catch (exception: Exception) {
                        onFailure(exception)
                    }
                }
            }

            suspend fun preuzmiSliku(imeSlike: String): Bitmap? {
                val storage = FirebaseStorage.getInstance()
                val storageReference = storage.reference
                val imagePath = "slike/$imeSlike"

                val imageRef = storageReference.child(imagePath)

                return try {
                    val task = imageRef.getBytes(Long.MAX_VALUE)
                    val bytes = task.await() // Sačekajte da se Task završi i dohvatite rezultat

                    BitmapFactory.decodeByteArray(bytes, 0, bytes?.size ?: 0)
                } catch (exception: Exception) {
                    null
                }
            }*/


        }

