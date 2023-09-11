package com.example.projekatmobilne.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projekatmobilne.DataClasses.Apartman
import com.example.projekatmobilne.MyRecyclerViewAdapterApartman
import com.example.projekatmobilne.R
import com.example.projekatmobilne.ViewModel.AddApartmentViewModel
import com.example.projekatmobilne.ViewModel.UserViewModel
import com.example.projekatmobilne.databinding.FragmentApartmanListBinding
import com.example.projekatmobilne.databinding.FragmentUsersListBinding


class ApartmanListFragment : Fragment() {

    private lateinit var binding: FragmentApartmanListBinding
    private lateinit var apartmanViewModel: AddApartmentViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentApartmanListBinding.inflate(inflater, container, false)
        apartmanViewModel = ViewModelProvider(this).get(AddApartmentViewModel::class.java)

        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())

        apartmanViewModel.preuzmiSveApartmane { listaApartmana ->
            val myAdapter = MyRecyclerViewAdapterApartman(listaApartmana)
            recyclerView.adapter = myAdapter

        }


        return binding.root
    }


}