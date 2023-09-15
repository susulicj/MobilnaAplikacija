package com.example.projekatmobilne.Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController

import com.example.projekatmobilne.R
import com.example.projekatmobilne.ViewModel.UserViewModel
import com.example.projekatmobilne.databinding.FragmentHomeProfileBinding
import com.example.projekatmobilne.databinding.FragmentUserProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class UserProfileFragment : Fragment() {

    private lateinit var binding: FragmentUserProfileBinding
    private lateinit var currentFirebaseUser: FirebaseUser
    private lateinit var userViewModel: UserViewModel


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

        userViewModel.vratiTrenutngKorisnika(currentFirebaseUser.email!!){user->
            if(user != null){
                Log.d("user", "$user")
                binding.IDdobijenoKorisnickoIme.text = user.korisnickoIme
                binding.IdDobijeniMail.text = user.email
                binding.IdDobijeniPoeni.text = user.poeni.toString()
                binding.IdDobijenBrojTelefona.text = user.brojTelefona.toString()
                binding.IdDobijenoImeiPrezime.text = user.imeiPrezime
            }

        }





        binding.button.setOnClickListener{
            it.findNavController().navigate(R.id.action_userProfileFragment_to_usersListFragment)
        }

        return binding.root
    }

}
