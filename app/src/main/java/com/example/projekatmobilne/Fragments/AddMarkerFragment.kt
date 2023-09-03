package com.example.projekatmobilne.Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import com.example.projekatmobilne.databinding.FragmentAddMarkerBinding
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import com.example.projekatmobilne.DataClasses.Apartman
import com.example.projekatmobilne.DataClasses.User
import com.example.projekatmobilne.ViewModel.AddApartmentViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class AddMarkerFragment : Fragment()  {

   private lateinit var viewModel : AddApartmentViewModel
   private lateinit var binding: FragmentAddMarkerBinding
   private lateinit var latLng : String
   private lateinit var currentUser: User
   private lateinit var currentFirebaseUser: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        currentFirebaseUser = FirebaseAuth.getInstance().currentUser!!
        //da mi vrati usera sa odredjenim emailom i onda da njega stavim u novi apartman
        currentUser = User(currentFirebaseUser.email)
        viewModel = ViewModelProvider(this).get(AddApartmentViewModel::class.java)
        binding = FragmentAddMarkerBinding.inflate(inflater, container, false)

       setFragmentResultListener("location") { location, bundle ->
            latLng = bundle.getString("location").toString()
            Log.d("SelectedLatLng", "Latitude: $latLng")
        }

        saveApartman()

        return binding.root



    }

   private fun saveApartman(){

        binding.btnDodaj.setOnClickListener{

            val noviApartman = Apartman(
                 adresa = binding.tvAdresa.text.toString(),
                 povrsina = binding.etPovrsina.text.toString().toDouble(),
                 brojSoba = binding.etBrojSoba.text.toString().toInt(),
                 brojTelefona = binding.tvtelefon.text.toString().toLong(),
                 email = binding.ptEmailKontakt.text.toString(),
                 latlng = parseLatLngFromString(latLng),
                 user = currentUser
            )

            viewModel.dodajApartman(noviApartman)
        }
    }


    private fun parseLatLngFromString(input: String?): LatLng? {
        try {

            val startIndex = input!!.indexOf("(")
            val endIndex = input.indexOf(")")

            if (startIndex != -1 && endIndex != -1) {
                val coordinates = input.substring(startIndex + 1, endIndex)
                val parts = coordinates.split(",")

                if (parts.size == 2) {
                    val latitude = parts[0].trim().toDouble()
                    val longitude = parts[1].trim().toDouble()

                    return LatLng(latitude, longitude)
                }
            }
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        }

        return null
    }
}