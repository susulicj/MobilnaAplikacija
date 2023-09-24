package com.example.projekatmobilne.Fragments

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.example.projekatmobilne.Activity.LoginActivity
import com.example.projekatmobilne.R
import com.example.projekatmobilne.ViewModel.UserViewModel
import com.example.projekatmobilne.databinding.FragmentUserProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch



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



        binding.ibLogout.setOnClickListener{
            Toast.makeText(requireContext(), "Odjavili ste se", Toast.LENGTH_SHORT).show()

            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
        }
        binding.imageButton.setOnClickListener{
            it.findNavController().navigate(R.id.action_userProfileFragment_to_homeProfileFragment)
        }

        lifecycleScope.launch(Dispatchers.IO) {
            val user = userViewModel.vratiTrenutngKorisnika(currentFirebaseUser.email!!)


            if (user != null) {
                activity?.runOnUiThread {
                    Log.d("user", "$user")
                    binding.IDdobijenoKorisnickoIme.text = user.korisnickoIme
                    binding.IdDobijeniMail.text = user.email
                    binding.IdDobijeniPoeni.text = user.poeni.toString()
                    binding.IdDobijenBrojTelefona.text = user.brojTelefona.toString()
                    binding.IdDobijenoImeiPrezime.text = user.imeiPrezime
                }


                preuzmiFotografiju(
                    user.profileImageUrl,
                    onFailure = { exception ->

                    },
                    onSuccess = { bitmap ->
                        if (bitmap != null) {

                            activity?.runOnUiThread {
                                binding.iwProfilePhoto.setImageBitmap(bitmap)

                            }
                        } else {
                            activity?.runOnUiThread {
                                binding.iwProfilePhoto.setImageResource(R.drawable.images)

                            }
                        }
                    }
                )

            }
        }


        binding.button.setOnClickListener{
            it.findNavController().navigate(R.id.action_userProfileFragment_to_usersListFragment)
        }
        return binding.root
    }



    fun preuzmiFotografiju(imeSlike: String?, onFailure: (Exception) -> Unit, onSuccess: (Bitmap?) -> Unit) {
        val storage = FirebaseStorage.getInstance()
        val storageReference = storage.reference

        if (imeSlike != null) {
            val imagePath = "slike/$imeSlike"
            val imageRef = storageReference.child(imagePath)

            imageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener { bytes ->
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                onSuccess(bitmap)
            }.addOnFailureListener { exception ->
                onFailure(exception)
            }
        } else {

            onSuccess(null)
        }
    }

        }

