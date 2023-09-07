package com.example.projekatmobilne.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projekatmobilne.MyRecyclerViewAdapter
import com.example.projekatmobilne.MyRecyclerViewAdapterUser
import com.example.projekatmobilne.R
import com.example.projekatmobilne.ViewModel.UserViewModel
import com.example.projekatmobilne.databinding.FragmentUsersListBinding


class UsersListFragment : Fragment() {


    private lateinit var binding: FragmentUsersListBinding
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        binding = FragmentUsersListBinding.inflate(inflater, container, false)

        val recyclerView = binding.recyclerView1
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())

        userViewModel.sortiranjeKorisnika()

        // Osigurajte da osluškujete LiveData za sortiranu listu korisnika
        userViewModel.sortedUsers.observe(viewLifecycleOwner, Observer { userList ->
            val myAdapter = MyRecyclerViewAdapterUser(userList)
            recyclerView.adapter = myAdapter
        })

        return binding.root
    }


}