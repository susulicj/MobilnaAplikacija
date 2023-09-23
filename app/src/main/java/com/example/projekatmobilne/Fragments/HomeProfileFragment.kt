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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

import java.util.concurrent.TimeUnit

class HomeProfileFragment : Fragment() {
    private lateinit var binding: FragmentHomeProfileBinding
    private lateinit var apartmanViewModel : AddApartmentViewModel
    val sharedViewModel : SharedViewModel by activityViewModels()
    private lateinit var  fusedLocationClient : FusedLocationProviderClient
    private lateinit var currentLocation: Location
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private val requestingLocationUpdates = true
    private lateinit var lista : MutableList<Apartman>




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
        apartmanViewModel = ViewModelProvider(this).get(AddApartmentViewModel::class.java)
         lista = mutableListOf<Apartman>()





        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        lifecycleScope.launch(Dispatchers.IO) {
            lista = apartmanViewModel.preuzmiSveApartmane() as MutableList<Apartman>
        }


        locationRequest =   LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 60).apply {
            setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
            setWaitForAccurateLocation(true)
            }.build()


       locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                val location = locationResult.lastLocation
                location?.let {
                    currentLocation = it
                    val latitude = currentLocation?.latitude
                    val longitude = currentLocation?.longitude
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

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.myLooper()
            )
        } else {

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

                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Kada više nije potrebno praćenje lokacije, obavezno otkažite pretplatu na location updates
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onResume() {
        super.onResume()
        if (requestingLocationUpdates) {
            requestLocationUpdates()
        }
    }

    private fun filtriranje(){

        var radioGroup = binding.radioGroup
        var radioGroupFilter = binding.radioGroupFiltriranje
        var filter = binding.ptFilter



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
     /*  apartmanViewModel.preuzmiSveApartmane { listaApartmana ->
            lista.addAll(listaApartmana)
        }*/
      /*  lifecycleScope.launch(Dispatchers.IO){
            lista = apartmanViewModel.preuzmiSveApartmane() as MutableList<Apartman>
            // Ovde možete raditi sa listom apartmana
        }*/




        binding.btnmapa.setOnClickListener{

            val selectedFilterID = radioGroupFilter.checkedRadioButtonId
            val selectedRadioButtonId = radioGroup.checkedRadioButtonId
            val korisnikovFilter = binding.ptFilter.text.toString()


                if (selectedFilterID != -1) {
                    when (selectedFilterID) {
                        R.id.IDbezFiltriranja -> {
                            sharedViewModel.setListaApartmana(lista)
                            if (selectedRadioButtonId != -1) {
                                when (selectedRadioButtonId) {
                                    R.id.radioButton1 -> {
                                        it.findNavController()
                                            .navigate(R.id.action_homeProfileFragment_to_mapsFragment)
                                    }
                                    R.id.radioButton2 -> {
                                        it.findNavController()
                                            .navigate(R.id.action_homeProfileFragment_to_apartmanListFragment)
                                    }

                                }
                            } else {
                                Toast.makeText(requireActivity(), "Morate izabrati opciju", Toast.LENGTH_SHORT)
                                    .show()
                            }

                        }
                        R.id.IDPoKorisniku -> {
                            if(korisnikovFilter.isEmpty())
                            {
                                Toast.makeText(requireActivity(), "Morate uneti parametar za filtriranje", Toast.LENGTH_SHORT).show()

                            }else {
                                val filtriranaLista = lista.filter { apartman ->
                                    apartman.user!!.email == korisnikovFilter
                                }
                                sharedViewModel.setListaApartmana(filtriranaLista)

                                if (selectedRadioButtonId != -1) {
                                    when (selectedRadioButtonId) {
                                        R.id.radioButton1 -> {
                                            it.findNavController()
                                                .navigate(R.id.action_homeProfileFragment_to_mapsFragment)
                                        }
                                        R.id.radioButton2 -> {
                                            it.findNavController()
                                                .navigate(R.id.action_homeProfileFragment_to_apartmanListFragment)
                                        }

                                    }
                                } else {
                                    Toast.makeText(requireActivity(), "Morate izabrati opciju", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            }
                        }
                        R.id.IDrbSprat -> {
                            if(korisnikovFilter.isEmpty())
                            {
                                Toast.makeText(requireActivity(), "Morate uneti parametar za filtriranje", Toast.LENGTH_SHORT).show()

                            }else {
                                val filtriranaLista = lista.filter { apartman ->
                                    apartman.sprat == korisnikovFilter.toLong()
                                }
                                sharedViewModel.setListaApartmana(filtriranaLista)

                                if (selectedRadioButtonId != -1) {
                                    when (selectedRadioButtonId) {
                                        R.id.radioButton1 -> {
                                            it.findNavController()
                                                .navigate(R.id.action_homeProfileFragment_to_mapsFragment)
                                        }
                                        R.id.radioButton2 -> {
                                            it.findNavController()
                                                .navigate(R.id.action_homeProfileFragment_to_apartmanListFragment)
                                        }

                                    }
                                } else {
                                    Toast.makeText(requireActivity(), "Morate izabrati opciju", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            }
                        }
                        R.id.IDrbRadius -> {
                            if(korisnikovFilter.isEmpty())
                            {
                                Toast.makeText(requireActivity(), "Morate uneti parametar za filtriranje", Toast.LENGTH_SHORT).show()

                            }else {

                                val filtriranaLista = mutableListOf<Apartman>()
                                val radiusInKm = korisnikovFilter.toDouble()
                                for (data in lista) {
                                    val distance = calculateDistance(
                                        currentLocation.latitude,
                                        currentLocation.longitude,
                                        data.latlng!!.latitude,
                                        data.latlng!!.longitude
                                    )
                                    if (distance <= radiusInKm) {
                                        filtriranaLista.add(data)
                                    }
                                }

                                sharedViewModel.setListaApartmana(filtriranaLista)

                                if (selectedRadioButtonId != -1) {
                                    when (selectedRadioButtonId) {
                                        R.id.radioButton1 -> {
                                            it.findNavController()
                                                .navigate(R.id.action_homeProfileFragment_to_mapsFragment)
                                        }
                                        R.id.radioButton2 -> {
                                            it.findNavController()
                                                .navigate(R.id.action_homeProfileFragment_to_apartmanListFragment)
                                        }

                                    }
                                } else {
                                    Toast.makeText(requireActivity(), "Morate izabrati opciju", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            }
                        }
                        R.id.IDPoDatumu -> {
                            val korisnikovfilter = binding.FilterDatum.text.toString()
                            if(korisnikovfilter.isEmpty())
                            {
                                Toast.makeText(requireActivity(), "Morate uneti parametar za filtriranje", Toast.LENGTH_SHORT).show()

                            }else {

                                val filtriranaLista = lista.filter { apartman ->
                                    apartman.datumKreiranja == korisnikovfilter
                                }
                                sharedViewModel.setListaApartmana(filtriranaLista)

                                if (selectedRadioButtonId != -1) {
                                    when (selectedRadioButtonId) {
                                        R.id.radioButton1 -> {
                                            it.findNavController()
                                                .navigate(R.id.action_homeProfileFragment_to_mapsFragment)
                                        }
                                        R.id.radioButton2 -> {
                                            it.findNavController()
                                                .navigate(R.id.action_homeProfileFragment_to_apartmanListFragment)
                                        }

                                    }
                                } else {
                                    Toast.makeText(requireActivity(), "Morate izabrati opciju", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            }
                        }

                    }
                }




            }



    }


    fun calculateDistance(
        lat1: Double, lon1: Double, lat2: Double, lon2: Double
    ): Double {
        val radius = 6371000 // Zemljina srednji radijus u metrima

        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)

        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return radius * c
    }



}
