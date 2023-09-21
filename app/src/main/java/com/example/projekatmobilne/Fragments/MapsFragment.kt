package com.example.projekatmobilne.Fragments

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.os.bundleOf
import androidx.fragment.app.*
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.projekatmobilne.DataClasses.Apartman
import com.example.projekatmobilne.DataClasses.User
import com.example.projekatmobilne.MyRecyclerViewAdapterApartman
import com.example.projekatmobilne.R
import com.example.projekatmobilne.ViewModel.AddApartmentViewModel
import com.example.projekatmobilne.ViewModel.AddCommentViewModel
import com.example.projekatmobilne.ViewModel.CommentViewModel
import com.example.projekatmobilne.ViewModel.SharedViewModel
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore


class MapsFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var viewModel: AddApartmentViewModel
    private lateinit var viewModelshared: SharedViewModel
    private val proximityDistance = 1000000.0 // Minimalna udaljenost
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
            if(listaApartmana.isEmpty()){
                activity?.runOnUiThread {
                    Toast.makeText(requireContext(), "Ne postoje stnovi sa takvom osobinom", Toast.LENGTH_SHORT).show()
                }

            }else{


            for(apartman in listaApartmana) {
                val marker = mMap.addMarker(
                    MarkerOptions()
                        .position(apartman.latlng!!)
                        .title(apartman.adresa)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                )
                marker?.tag = apartman
                targetMarkers.add(marker!!)
             }
            }


            locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    locationResult.lastLocation?.let { location ->
                        val currentLatLng = LatLng(location.latitude, location.longitude)


                        val markeriZaBojenjeuZuto = mutableListOf<Marker>()


                        for (targetLocation in listaApartmana) {
                            Log.d("targetttt", "$targetLocation")
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


