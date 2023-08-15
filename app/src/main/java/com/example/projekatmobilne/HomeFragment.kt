package com.example.projekatmobilne

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.projekatmobilne.Activity.LoginActivity
import com.example.projekatmobilne.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {

    private lateinit var binding :FragmentHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false )
        binding.btnLogin.setOnClickListener{
           // it.findNavController().navigate(R.id.action_homeFragment_to_logInFragment)
            val intent = Intent(getActivity(),  LoginActivity::class.java)
            startActivity(intent)
        }
        return binding.root
    }


}