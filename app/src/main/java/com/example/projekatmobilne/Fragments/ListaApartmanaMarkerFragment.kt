package com.example.projekatmobilne.Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projekatmobilne.MyRecyclerViewAdapterApartman
import com.example.projekatmobilne.MyRecyclerViewMarker
import com.example.projekatmobilne.R
import com.example.projekatmobilne.ViewModel.SharedViewModel
import com.example.projekatmobilne.databinding.FragmentApartmanListBinding
import com.example.projekatmobilne.databinding.FragmentListaApartmanaMarkerBinding


class ListaApartmanaMarkerFragment : Fragment() {

    private lateinit var viewModelshared: SharedViewModel
    private lateinit var binding: FragmentListaApartmanaMarkerBinding




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentListaApartmanaMarkerBinding.inflate(inflater, container, false)




        val recyclerView = binding.recyclerView2
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())

        binding.imageButton4.setOnClickListener{
            it.findNavController().navigate(R.id.action_listaApartmanaMarkerFragment_to_homeProfileFragment)

        }

        viewModelshared = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        val navController = findNavController()

        viewModelshared.getListaMakera().observe(viewLifecycleOwner, Observer{apartmani->
            Log.d("prikaz", "$apartmani")

            val myAdapter = MyRecyclerViewMarker(apartmani, viewModelshared, navController)
            recyclerView.adapter = myAdapter

        })




        return binding.root
    }


}