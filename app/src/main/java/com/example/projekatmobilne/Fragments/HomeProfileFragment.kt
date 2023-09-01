package com.example.projekatmobilne.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.projekatmobilne.R
import com.example.projekatmobilne.databinding.FragmentHomeProfileBinding
class HomeProfileFragment : Fragment() {
    private lateinit var binding: FragmentHomeProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeProfileBinding.inflate(inflater, container, false)

        binding.btnmapa.setOnClickListener{
            it.findNavController().navigate(R.id.action_homeProfileFragment_to_mapsFragment)
        }

        return binding.root
    }


}