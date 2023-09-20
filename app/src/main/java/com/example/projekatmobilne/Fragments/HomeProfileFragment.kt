package com.example.projekatmobilne.Fragments

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.projekatmobilne.DataClasses.Apartman
import com.example.projekatmobilne.MyRecyclerViewAdapterApartman
import com.example.projekatmobilne.R
import com.example.projekatmobilne.ViewModel.AddApartmentViewModel
import com.example.projekatmobilne.ViewModel.SharedViewModel
import com.example.projekatmobilne.databinding.FragmentHomeProfileBinding
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.util.concurrent.TimeUnit

class HomeProfileFragment : Fragment() {
    private lateinit var binding: FragmentHomeProfileBinding
    private lateinit var apartmanViewModel : AddApartmentViewModel
    val sharedViewModel : SharedViewModel by activityViewModels()
    private lateinit var  fusedLocationClient : FusedLocationProviderClient
    private lateinit var currentLocation: Location
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback




    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1 // Zamijenite 123 sa željenim brojem
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeProfileBinding.inflate(inflater, container, false)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())




        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        apartmanViewModel = ViewModelProvider(this).get(AddApartmentViewModel::class.java)
        //sharedViewModel = ViewModelProvider(this).get(SharedViewModel::class.java)


        locationRequest = LocationRequest().apply {
            interval = TimeUnit.SECONDS.toMillis(60)
            fastestInterval = TimeUnit.SECONDS.toMillis(30)
            maxWaitTime = TimeUnit.MINUTES.toMillis(2)
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        // Inicijalizuj LocationCallback
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                val location = locationResult.lastLocation
                location?.let {
                    currentLocation = it
                    val latitude = currentLocation?.latitude
                    val longitude = currentLocation?.longitude
                    Log.d("Location", "Latitude: $latitude, Longitude: $longitude")
                }
            }
        }


        if (checkLocationPermission()) {
            requestLocationUpdates()
        } else {
            requestPermissions()
        }










        binding.btnListaKorsnika.setOnClickListener{
            it.findNavController().navigate(R.id.action_homeProfileFragment_to_userProfileFragment)
        }

        binding.btnDodajStan.setOnClickListener{
            it.findNavController().navigate(R.id.action_homeProfileFragment_to_addMarkerFragment)
        }

        filtriranje()


    }
    private fun checkLocationPermission(): Boolean {
        return (ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED)
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }


    private fun requestLocationUpdates() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Imate dozvolu za pristup lokaciji, sada možete pozvati requestLocationUpdates
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.myLooper()
            )
        } else {
            // Nemate dozvolu za pristup lokaciji, zatražite je od korisnika
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()
        )
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    requestLocationUpdates()
                } else {
                    // Ovde možete obavestiti korisnika da su dozvole potrebne za dobijanje lokacije
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Kada više nije potrebno praćenje lokacije, obavezno otkažite pretplatu na location updates
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
    private fun filtriranje(){

        var radioGroup = binding.radioGroup
        var radioGroupFilter = binding.radioGroupFiltriranje
        var filter = binding.ptFilter
        var lista = mutableListOf<Apartman>()


        radioGroupFilter.setOnCheckedChangeListener{ group, checkedID ->
            if(checkedID != R.id.IDbezFiltriranja && checkedID != R.id.IDPoDatumu){
                filter.visibility = View.VISIBLE
                binding.FilterDatum.visibility = View.GONE
            }else{
                filter.visibility = View.GONE
                binding.FilterDatum.visibility = View.GONE

            }

            if(checkedID == R.id.IDPoDatumu){
                binding.FilterDatum.visibility = View.VISIBLE
            }

        }
        apartmanViewModel.preuzmiSveApartmane { listaApartmana ->
            lista.addAll(listaApartmana)
        }


        binding.btnmapa.setOnClickListener{

            val selectedFilterID = radioGroupFilter.checkedRadioButtonId
            val selectedRadioButtonId = radioGroup.checkedRadioButtonId
            val korisnikovFilter = binding.ptFilter.text.toString()

            if(selectedFilterID != -1){
                when (selectedFilterID){
                    R.id.IDbezFiltriranja-> {
                        sharedViewModel.setListaApartmana(lista)

                    }
                    R.id.IDPoKorisniku-> {
                        val filtriranaLista = lista.filter {apartman ->
                            apartman.user!!.email == korisnikovFilter
                        }
                        sharedViewModel.setListaApartmana(filtriranaLista)

                    }
                    R.id.IDrbSprat->{
                        val filtriranaLista = lista.filter{apartman ->
                            apartman.sprat == korisnikovFilter.toLong()
                        }
                        sharedViewModel.setListaApartmana(filtriranaLista)
                    }
                    R.id.IDrbRadius->{
                        val filtriranaLista = mutableListOf<Apartman>()
                        val radiusInKm = korisnikovFilter.toDouble()
                        for(data in lista){
                            val distance = calculateDistance(currentLocation.latitude, currentLocation.longitude, data.latlng!!.latitude, data.latlng!!.longitude)
                            if(distance <= radiusInKm){
                                filtriranaLista.add(data)
                            }
                        }

                        sharedViewModel.setListaApartmana(filtriranaLista)
                    }
                    R.id.IDPoDatumu->{

                        val filtriranaLista = lista.filter {apartman ->
                            apartman.datumKreiranja == binding.FilterDatum.text.toString()
                        }
                        sharedViewModel.setListaApartmana(filtriranaLista)

                    }

                }
            }

            if(selectedRadioButtonId != -1){
                when (selectedRadioButtonId) {
                    R.id.radioButton1 -> {
                        it.findNavController().navigate(R.id.action_homeProfileFragment_to_mapsFragment)
                    }
                    R.id.radioButton2 -> {
                        it.findNavController().navigate(R.id.action_homeProfileFragment_to_apartmanListFragment)
                    }

                }
            }else{
                Toast.makeText(requireActivity(), "Morate izabrati opciju", Toast.LENGTH_SHORT).show()
            }



        }
    }


    fun calculateDistance(
        lat1: Double, lon1: Double, lat2: Double, lon2: Double
    ): Double {
        val radius = 6371 // Zemljina srednji radijus u kilometrima

        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)

        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return radius * c
    }


}