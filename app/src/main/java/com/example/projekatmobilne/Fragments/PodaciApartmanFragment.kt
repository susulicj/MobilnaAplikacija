package com.example.projekatmobilne.Fragments

import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.example.projekatmobilne.DataClasses.Apartman
import com.example.projekatmobilne.DataClasses.User
import com.example.projekatmobilne.R
import com.example.projekatmobilne.ViewModel.AddApartmentViewModel
import com.example.projekatmobilne.ViewModel.AddCommentViewModel
import com.example.projekatmobilne.ViewModel.SharedViewModel
import com.example.projekatmobilne.databinding.FragmentPodaciApartmanBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class PodaciApartmanFragment : Fragment() {

    private lateinit var binding: FragmentPodaciApartmanBinding
    private lateinit var viewModel: SharedViewModel
    private lateinit var commentviewModel:AddCommentViewModel
    private lateinit var currentApartman : Apartman
    private lateinit var currentUser: User
    private lateinit var currentFirebaseUser: FirebaseUser
    private lateinit var email:String
    private lateinit var verKod: String
    private lateinit var viewModelApartman: AddApartmentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPodaciApartmanBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        commentviewModel = ViewModelProvider(requireActivity()).get(AddCommentViewModel::class.java)
        viewModelApartman = ViewModelProvider(requireActivity()).get(AddApartmentViewModel::class.java)
        currentFirebaseUser = FirebaseAuth.getInstance().currentUser!!
        currentUser = User(currentFirebaseUser.email)
        email = " "
        verKod = " "


        viewModel.getclickedApartman().observe(viewLifecycleOwner, Observer { apartman ->
            if (apartman != null) {
                currentApartman = apartman
                binding.IdAdresaApartmana.text = currentApartman.adresa
                binding.IdemailStana.text = currentApartman.email
                binding.idBrojSobaApatmana.text = currentApartman.brojSoba.toString()
                binding.IdPovrsinaStana.text = currentApartman.povrsina.toString()
                binding.IdSpratStana.text = currentApartman.sprat.toString()
                binding.IdBrojTelefonaStana.text = currentApartman.brojTelefona.toString()
                verKod = currentApartman.verifikacioniKod
                if(currentApartman.user!!.email.toString() == currentUser.email.toString()){
                    Log.d("vidljivost", "${currentApartman.user!!.email.toString()}")
                    Log.d("vidljivost", "${currentUser.email.toString()}")
                    binding.btnIzbrisi.visibility = View.VISIBLE
                }


            }
        })


        binding.btnIzbrisi.setOnClickListener{
            deleteApartmentAsync(verKod)
            Toast.makeText(requireContext(), "Uspešno ste izbrisali stan", Toast.LENGTH_SHORT).show()
            commentviewModel.azuriranjePoena(currentUser.email.toString(), -2)
            Toast.makeText(requireContext(), "Smanjeno Vam je 2 poena", Toast.LENGTH_SHORT).show()
            it.findNavController().navigate(R.id.action_podaciApartmanFragment_to_homeProfileFragment)

        }


        return binding.root


    }

    private fun deleteApartmentAsync(apartmentId: String) {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                viewModelApartman.deleteApartment(apartmentId)

            } catch (e: Exception) {

                withContext(Dispatchers.Main) {

                }
            }
        }
    }


}



