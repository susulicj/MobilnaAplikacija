package com.example.projekatmobilne.Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.projekatmobilne.DataClasses.Apartman
import com.example.projekatmobilne.R
import com.example.projekatmobilne.ViewModel.SharedViewModel
import com.example.projekatmobilne.databinding.FragmentPodaciApartmanBinding


class PodaciApartmanFragment : Fragment() {

    private lateinit var binding: FragmentPodaciApartmanBinding
    private lateinit var viewModel: SharedViewModel
    private lateinit var currentApartman : Apartman


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPodaciApartmanBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)


        viewModel.getclickedApartman().observe(viewLifecycleOwner, Observer { apartman ->
            if (apartman != null) {
                currentApartman = apartman
                binding.IdAdresaApartmana.text = currentApartman.adresa
                binding.IdemailStana.text = currentApartman.email
                binding.idBrojSobaApatmana.text = currentApartman.brojSoba.toString()
                binding.IdPovrsinaStana.text = currentApartman.povrsina.toString()
                binding.IdSpratStana.text = currentApartman.sprat.toString()
                binding.IdBrojTelefonaStana.text = currentApartman.brojTelefona.toString()
            }
        })


        return binding.root
    }


}