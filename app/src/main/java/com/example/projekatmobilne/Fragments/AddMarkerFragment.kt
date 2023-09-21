package com.example.projekatmobilne.Fragments


import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import com.example.projekatmobilne.databinding.FragmentAddMarkerBinding
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.example.projekatmobilne.DataClasses.Apartman
import com.example.projekatmobilne.DataClasses.User
import com.example.projekatmobilne.R
import com.example.projekatmobilne.ViewModel.AddApartmentViewModel
import com.example.projekatmobilne.ViewModel.AddCommentViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate


class AddMarkerFragment : Fragment()  {

   private lateinit var viewModel : AddApartmentViewModel
   private lateinit var commentViewModel : AddCommentViewModel
   private lateinit var binding: FragmentAddMarkerBinding
   private lateinit var latLng : String
   private lateinit var lnglat: LatLng
   private lateinit var currentUser: User
   private lateinit var currentFirebaseUser: FirebaseUser
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener

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
        locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        commentViewModel = ViewModelProvider(this).get(AddCommentViewModel::class.java)


        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                val latitude = location.latitude
                val longitude = location.longitude

                lnglat = LatLng(latitude, longitude)
            }
        }

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, locationListener)
        } else {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }


      binding.btnNazad2.setOnClickListener{
          it.findNavController().navigate(R.id.action_addMarkerFragment_to_homeProfileFragment)
      }

      /* setFragmentResultListener("location") { location, bundle ->
            latLng = bundle.getString("location").toString()
            Log.d("SelectedLatLng", "Latitude: $latLng")
        }*/

        saveApartman()

        return binding.root



    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    // Ako je dozvola dodeljena, postavite LocationListener-a
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, locationListener)
                }
            }
        }
    }

   private fun saveApartman(){




       binding.btnDodaj.setOnClickListener{



            val noviApartman = Apartman(
                 adresa = binding.tvAdresa.text.toString(),
                 povrsina = binding.etPovrsina.text.toString().toDouble(),
                 brojSoba = binding.etBrojSoba.text.toString().toLong(),
                 brojTelefona = binding.tvtelefon.text.toString().toLong(),
                 email = binding.ptEmailKontakt.text.toString(),
                // latlng = parseLatLngFromString(latLng),
                 latlng = lnglat,
                 verifikacioniKod = binding.idVerKod.text.toString(),
                 prosecnaOcena = 0.0,
                 listaOcena = mutableListOf(),
                 sprat = binding.ptSprat.text.toString().toLong(),
                 datumKreiranja = LocalDate.now().toString(),
                 user = currentUser
            )
           lifecycleScope.launch(Dispatchers.IO) {

               val result = viewModel.proveriDuplikatApartmana(noviApartman)

               if (result) {
                   activity?.runOnUiThread {
                       Toast.makeText(requireContext(), "Vec postoji", Toast.LENGTH_SHORT).show()
                   }

               } else {
                   viewModel.dodajApartman(noviApartman)
                   activity?.runOnUiThread {
                       Toast.makeText(requireContext(), "Uspešno ste dodali stan", Toast.LENGTH_SHORT).show()
                   }
                   commentViewModel.azuriranjePoena(currentUser.email.toString(), 6)
                   activity?.runOnUiThread {
                       Toast.makeText(requireContext(), "Dobili ste 6 poena", Toast.LENGTH_SHORT).show()
                       binding.tvAdresa.text.clear()
                       binding.etPovrsina.text.clear()
                       binding.etBrojSoba.text.clear()
                       binding.tvtelefon.text.clear()
                       binding.ptEmailKontakt.text.clear()
                       binding.idVerKod.text.clear()
                       binding.ptSprat.text.clear()
                   }

               }
           }


       }
    }

}