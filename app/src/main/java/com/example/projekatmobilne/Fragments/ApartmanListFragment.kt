package com.example.projekatmobilne.Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projekatmobilne.DataClasses.Apartman
import com.example.projekatmobilne.MyRecyclerViewAdapterApartman
import com.example.projekatmobilne.R
import com.example.projekatmobilne.ViewModel.AddApartmentViewModel
import com.example.projekatmobilne.ViewModel.SharedViewModel
import com.example.projekatmobilne.ViewModel.UserViewModel
import com.example.projekatmobilne.databinding.FragmentApartmanListBinding
import com.example.projekatmobilne.databinding.FragmentUsersListBinding


class ApartmanListFragment : Fragment() {

    private lateinit var binding: FragmentApartmanListBinding
    private lateinit var apartmanViewModel: AddApartmentViewModel
    private  val sharedViewModel : SharedViewModel by activityViewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentApartmanListBinding.inflate(inflater, container, false)
        apartmanViewModel = ViewModelProvider(this).get(AddApartmentViewModel::class.java)


        preuzimanje()


        return binding.root
    }

    fun preuzimanje(){

        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())

        sharedViewModel.getListaApartmana()?.observe(viewLifecycleOwner, Observer { listaApartmana ->
            if(listaApartmana.isEmpty()) {
                activity?.runOnUiThread {
                    Toast.makeText(requireContext(), "Ne postoje stnovi sa takvom osobinom", Toast.LENGTH_SHORT).show()
                }
            }
            else{
                val myAdapter = MyRecyclerViewAdapterApartman(listaApartmana)
                recyclerView.adapter = myAdapter
            }
        })


    }


}