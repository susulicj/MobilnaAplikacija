package com.example.projekatmobilne.Fragments

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.projekatmobilne.DataClasses.Apartman
import com.example.projekatmobilne.R
import com.example.projekatmobilne.ViewModel.AddApartmentViewModel
import com.example.projekatmobilne.ViewModel.CommentViewModel
import com.example.projekatmobilne.ViewModel.SharedViewModel
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore


class MapsFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var lastLocation: Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var currentMarker: Marker? = null
    private lateinit var viewModel: AddApartmentViewModel
    private lateinit var viewModelshared: SharedViewModel


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

        fusedLocationClient.lastLocation.addOnSuccessListener(requireActivity()) { location ->
            if (location != null) {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
                placeMarkerOnMap(currentLatLng)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
            }
        }

        //obrada promena
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    placeMarkerOnMap(currentLatLng)
                }
            }
        }
        //prikupljanje informacija za obbradu
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 100)
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(2000)
            .setMaxUpdateDelayMillis(100)
            .build()


        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)

        MapClickListener()

        viewModel.preuzmiSveApartmane{listaApartmana ->
            for(apartman in listaApartmana){
                Log.d("apartman kliknuti", "${apartman.latlng}")
                val marker = mMap.addMarker(MarkerOptions().position(apartman.latlng!!).title(apartman.adresa))
                marker?.tag = apartman

            }
        }

        mMap.setOnMarkerClickListener{marker->

            val clickedApartman = marker.tag as? Apartman
            Log.d("apartman kliknutii", "$clickedApartman")
            if (clickedApartman != null) {
                viewModelshared.setclickedApartman(clickedApartman)
            }

            val action = MapsFragmentDirections.actionMapsFragmentToCommentsFragment()
            view?.findNavController()?.navigate(action)

            true
        }


    }

    private fun MapClickListener(){

        mMap.setOnMapClickListener {latLng->

            val bundle = bundleOf("location" to latLng.toString())
            setFragmentResult("location", bundle)
            val action = MapsFragmentDirections.actionMapsFragmentToAddMarkerFragment()
            view?.findNavController()?.navigate(action)



        }
    }

    private fun placeMarkerOnMap(currentLatLng: LatLng) {
        if (currentMarker == null) {
            val markerOptions = MarkerOptions().position(currentLatLng)
            markerOptions.title("You are here")
            currentMarker = mMap.addMarker(markerOptions)
        } else {
            currentMarker?.position = currentLatLng
        }
    }
}


