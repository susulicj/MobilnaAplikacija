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
    private lateinit var lastLocation: Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var currentMarker: Marker? = null
    private lateinit var viewModel: AddApartmentViewModel
    private lateinit var viewModelshared: SharedViewModel
    private val proximityDistance = 1000000.0 // Minimalna udaljenost za detekciju približavanja (u metrima)
    private var locationListener: LocationListener? = null
    private val targetLocations = mutableListOf<LatLng>()
    val sharedViewModel : SharedViewModel by activityViewModels()
    private val requestingLocationUpdates = true // Set this to true if you want to start location updates initially
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

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    val currentLatLng = LatLng(location.latitude, location.longitude)

                }
            }
        }

        locationRequest = LocationRequest.create().apply {
            interval = 2000 // Set the update interval (in milliseconds) as per your requirements
            fastestInterval = 1000 // Set the fastest update interval
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }



    }

    override fun onMapReady(googleMap: GoogleMap) {



        mMap = googleMap
        viewModelshared = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        viewModel = ViewModelProvider(this).get(AddApartmentViewModel::class.java)
        mMap.uiSettings.isZoomControlsEnabled = true




        viewModel.preuzmiSveApartmane{listaApartmana ->
            for(apartman in listaApartmana){
              targetLocations.add(apartman.latlng!!)

            }
        }



        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_REQUEST_CODE)
            return
        }




        mMap.isMyLocationEnabled = true

       /* fusedLocationClient.lastLocation.addOnSuccessListener(requireActivity()) { location ->
            if (location != null) {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
                //placeMarkerOnMap(currentLatLng)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
            }
        }*/


        //obrada promena
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    //placeMarkerOnMap(currentLatLng)
                }
            }
        }
        //prikupljanje informacija za obbradu
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 100)
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(2000)
            .setMaxUpdateDelayMillis(100)
            .build()






        /*viewModel.preuzmiSveApartmane{listaApartmana ->
            for(apartman in listaApartmana){
                Log.d("apartman kliknuti", "${apartman.latlng}")
                val marker = mMap.addMarker(MarkerOptions().position(apartman.latlng!!).title(apartman.adresa))
                marker?.tag = apartman

            }
        }*/

        sharedViewModel.getListaApartmana()?.observe(viewLifecycleOwner, Observer { listaApartmana ->

            val targetMarkers = mutableListOf<Marker>()

            for(apartman in listaApartmana){
                Log.d("filterrr", "${apartman.latlng}")
                val marker = mMap.addMarker( MarkerOptions()
                    .position(apartman.latlng!!)
                    .title(apartman.adresa)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                )
                marker?.tag = apartman
                targetMarkers.add(marker!!)

            }

           /* fusedLocationClient.lastLocation.addOnSuccessListener(requireActivity()) { location ->
                if (location != null) {
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    // Postavi trenutnu lokaciju na mapu i animiraj kameru
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))

                    // Proveri približavanje ciljanim lokacijama
                    for (targetLocation in listaApartmana) {
                        Log.d("targetttt", "$targetLocation")
                        val distance = calculateDistance(currentLatLng, targetLocation.latlng!!)
                        if (distance <= proximityDistance) {
                            // Korisnik se približio ciljanoj lokaciji, prikažite Toast poruku
                            Toast.makeText(requireContext(), "Približavate se ${targetLocation}!", Toast.LENGTH_SHORT).show()
                            for (marker in targetMarkers) {
                                val markerLatLng = marker.position
                                if (markerLatLng == targetLocation.latlng) {
                                    // Promenite boju markera na žutu ako se LatLng poklapaju
                                    marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                                }
                            }

                        }
                    }
                } else {
                    // Trenutna lokacija nije dostupna, možete obavestiti korisnika o tome
                    Toast.makeText(requireContext(), "Trenutna lokacija nije dostupna.", Toast.LENGTH_SHORT).show()
                }
            }*/


            //kad se korisniku poklopi lokacija sa zadatom lokacijom da dobije 1 poen
            // Obrada promena
            locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    locationResult.lastLocation?.let { location ->
                        val currentLatLng = LatLng(location.latitude, location.longitude)
                        val thresholdInMeters = 10.0

                        val markeriZaBojenjeuZuto = mutableListOf<Marker>()


                        for (targetLocation in listaApartmana) {
                            Log.d("targetttt", "$targetLocation")
                            val distance = calculateDistance(currentLatLng, targetLocation.latlng!!)
                            if (distance <= proximityDistance) {

                               /* if(distance <= thresholdInMeters) {
                                    commentViewModel.azuriranjePoena(currentUser.email.toString(), 2)
                                    Toast.makeText(requireContext(), "Dobili ste 2 poena", Toast.LENGTH_SHORT).show()
                                }*/
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
                            marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                        }


                        for (marker in markeriZaBojenjeuZuto) {
                            marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
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


        // Nakon inicijalizacije mape
       /* if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Provjerite lokaciju korisnika i udaljenost do ciljanih lokacija
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    for (targetLocation in targetLocations) {
                        val distance = calculateDistance(currentLatLng, targetLocation)
                        if (distance <= proximityDistance) {
                            // Korisnik se približio ciljanoj lokaciji, prikažite Toast poruku
                            Toast.makeText(requireContext(), "Približavate se ${targetLocation}!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }*/


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


