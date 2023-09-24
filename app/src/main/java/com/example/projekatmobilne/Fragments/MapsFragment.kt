package com.example.projekatmobilne.Fragments

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.projekatmobilne.DataClasses.Apartman
import com.example.projekatmobilne.DataClasses.User
import com.example.projekatmobilne.R
import com.example.projekatmobilne.ViewModel.AddApartmentViewModel
import com.example.projekatmobilne.ViewModel.AddCommentViewModel
import com.example.projekatmobilne.ViewModel.SharedViewModel
import com.google.android.gms.location.*
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MapsFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var viewModel: AddApartmentViewModel
    private lateinit var viewModelshared: SharedViewModel
    private val proximityDistance = 50// Minimalna udaljenost
    val sharedViewModel : SharedViewModel by activityViewModels()
    private val requestingLocationUpdates = true
    private lateinit var locationRequest: LocationRequest
    private lateinit var commentViewModel: AddCommentViewModel
    private lateinit var currentFirebaseUser: FirebaseUser
    private lateinit var currentUser: User




    companion object{
        private const val LOCATION_REQUEST_CODE = 1
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {




        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        commentViewModel = ViewModelProvider(this).get(AddCommentViewModel::class.java)
        currentFirebaseUser = FirebaseAuth.getInstance().currentUser!!
        currentUser = User(currentFirebaseUser.email)

        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 100)
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(2000)
            .setMaxUpdateDelayMillis(100)
            .build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                }
            }
        }



    }

    override fun onMapReady(googleMap: GoogleMap) {

        mMap = googleMap
        viewModelshared = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        viewModel = ViewModelProvider(this).get(AddApartmentViewModel::class.java)
        mMap.uiSettings.isZoomControlsEnabled = true



        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_REQUEST_CODE)
            return
        }



        mMap.isMyLocationEnabled = true


        sharedViewModel.getListaApartmana()?.observe(viewLifecycleOwner, Observer { listaApartmana ->

            val targetMarkers = mutableListOf<Marker>()
            val latLngApartmaniMap = mutableMapOf<LatLng, MutableList<Apartman>>()

            for (apartman in listaApartmana) {
                val latLng = apartman.latlng
                if (latLng != null) {
                    if (latLngApartmaniMap.containsKey(latLng)) {
                        latLngApartmaniMap[latLng]!!.add(apartman)
                    } else {
                        val novaListaApartmana = mutableListOf(apartman)
                        latLngApartmaniMap[latLng] = novaListaApartmana
                    }
                }
            }

            for ((latLng, apartmani) in latLngApartmaniMap) {
                val marker = mMap.addMarker(
                    MarkerOptions()
                        .position(latLng)
                        .title(apartmani[0].brojZgrade.toString())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                )
                marker!!.tag = apartmani
                Log.d("prikaz", "${marker.tag}")


                targetMarkers.add(marker)

            }
            Log.d("prikaz", "$targetMarkers")

            locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    locationResult.lastLocation?.let { location ->
                        val currentLatLng = LatLng(location.latitude, location.longitude)


                        val markeriZaBojenjeuZuto = mutableListOf<Marker>()


                        for (targetLocation in listaApartmana) {
                            val distance = calculateDistance(currentLatLng, targetLocation.latlng!!)
                            if (distance <= proximityDistance) {

                                for (marker in targetMarkers) {
                                    val markerLatLng = marker.position
                                    if (markerLatLng == targetLocation.latlng) {
                                        markeriZaBojenjeuZuto.add(marker)



                                    }else{


                                    }
                                }
                            }
                        }
                        for (marker in targetMarkers) {
                            if (marker in markeriZaBojenjeuZuto) {
                                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                            } else {
                                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                            }
                        }




                    }
                }
            }

            startLocationUpdates()

        })

           mMap.setOnMarkerClickListener{marker->
               val kliknutiMarker = marker.tag as? List<Apartman>
               if(kliknutiMarker != null){
                   viewModelshared.setListaMarkera(kliknutiMarker)
               }
               view?.findNavController()!!.navigate(R.id.action_mapsFragment_to_listaApartmanaMarkerFragment)

            true
        }

    }

    private fun calculateDistance(latLng1: LatLng, latLng2: LatLng): Float {
        val location1 = Location("Location1")
        location1.latitude = latLng1.latitude
        location1.longitude = latLng1.longitude

        val location2 = Location("Location2")
        location2.latitude = latLng2.latitude
        location2.longitude = latLng2.longitude

        return location1.distanceTo(location2)
    }

    override fun onResume() {
        super.onResume()
        if (requestingLocationUpdates) startLocationUpdates()
    }
    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_REQUEST_CODE)
            return
        }
        fusedLocationClient.requestLocationUpdates(locationRequest,
            locationCallback,
            Looper.getMainLooper())
    }
    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

}


