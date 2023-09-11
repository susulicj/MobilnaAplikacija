package com.example.projekatmobilne.Fragments

import android.os.Bundle
import android.util.Log
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.projekatmobilne.DataClasses.Apartman
import com.example.projekatmobilne.MyRecyclerViewAdapterApartman
import com.example.projekatmobilne.R
import com.example.projekatmobilne.ViewModel.AddApartmentViewModel
import com.example.projekatmobilne.databinding.FragmentHomeProfileBinding
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class HomeProfileFragment : Fragment() {
    private lateinit var binding: FragmentHomeProfileBinding
    private lateinit var apartmanViewModel : AddApartmentViewModel




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeProfileBinding.inflate(inflater, container, false)
        apartmanViewModel = ViewModelProvider(this).get(AddApartmentViewModel::class.java)

        var radioGroup = binding.radioGroup
        var radioGroupFilter = binding.radioGroupFiltriranje
        var filter = binding.ptFilter
        var lista = mutableListOf<Apartman>()


        /*radioGroupFilter.setOnCheckedChangeListener{ group, checkedID ->
            if(checkedID != -1){
                filter.visibility = View.VISIBLE
            }else{
                filter.visibility = View.GONE
            }

        }*/
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
                        //cela lista da se prosledi
                    }
                    R.id.IDPoKorisniku-> {
                        Log.d("filterrrr", "$lista")
                        val filtriranaLista = lista.filter {apartman ->
                              apartman.user!!.email == korisnikovFilter

                        }
                        Log.d("filterrrr", "$filtriranaLista")
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

        binding.btnListaKorsnika.setOnClickListener{
            it.findNavController().navigate(R.id.action_homeProfileFragment_to_usersListFragment)
        }

        binding.btnDodajStan.setOnClickListener{
            it.findNavController().navigate(R.id.action_homeProfileFragment_to_addMarkerFragment)
        }



        return binding.root
    }


}