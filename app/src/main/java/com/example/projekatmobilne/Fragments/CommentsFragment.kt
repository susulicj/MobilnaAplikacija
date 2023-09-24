package com.example.projekatmobilne.Fragments

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projekatmobilne.DataClasses.Apartman
import com.example.projekatmobilne.DataClasses.Comment
import com.example.projekatmobilne.DataClasses.User
import com.example.projekatmobilne.MyRecyclerViewAdapter
import com.example.projekatmobilne.R
import com.example.projekatmobilne.ViewModel.AddApartmentViewModel
import com.example.projekatmobilne.ViewModel.AddCommentViewModel
import com.example.projekatmobilne.ViewModel.SharedViewModel
import com.example.projekatmobilne.databinding.FragmentCommentsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class CommentsFragment : Fragment() {
    private lateinit var binding: FragmentCommentsBinding
    private lateinit var viewModel: SharedViewModel
    private lateinit var apartmanViewModel: AddApartmentViewModel
    private lateinit var currentFirebaseUser: FirebaseUser
    private lateinit var currentUser: User
    private lateinit var commentViewModel: AddCommentViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var commentList : ArrayList<Comment>
    private var ocena: Int = 0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        apartmanViewModel = ViewModelProvider(this).get(AddApartmentViewModel::class.java)
        commentViewModel = ViewModelProvider(this).get(AddCommentViewModel::class.java)
        viewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        binding = FragmentCommentsBinding.inflate(inflater, container, false)
        currentFirebaseUser = FirebaseAuth.getInstance().currentUser!!
        currentUser = User(currentFirebaseUser.email)


        recyclerView = binding.myRecyclerView
        recyclerView.setBackgroundColor(Color.WHITE)
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())

        commentList = arrayListOf()

        binding.imageButton3.setOnClickListener{
            it.findNavController().navigate(R.id.action_commentsFragment_to_listaApartmanaMarkerFragment)
        }
        binding.detalji.setOnClickListener{
            it.findNavController().navigate(R.id.action_commentsFragment_to_podaciApartmanFragment)
        }

        binding.ptKomentar.setOnClickListener{

            binding.IdNaslov.visibility = View.INVISIBLE
            binding.ratingBar.visibility = View.INVISIBLE
            binding.btnSubmit.visibility = View.INVISIBLE
            binding.Idnatpis.visibility = View.INVISIBLE
            binding.myRecyclerView.visibility = View.INVISIBLE
            binding.detalji.visibility = View.INVISIBLE
            binding.tvProsecnaOcena.visibility = View.INVISIBLE
            binding.vratiSeNazad.visibility = View.VISIBLE
            binding.twOcena.visibility = View.INVISIBLE
            binding.imageButton3.visibility = View.INVISIBLE
        }

        binding.vratiSeNazad.setOnClickListener{
            binding.IdNaslov.visibility = View.VISIBLE
            binding.ratingBar.visibility = View.VISIBLE
            binding.btnSubmit.visibility = View.VISIBLE
            binding.Idnatpis.visibility = View.VISIBLE
            binding.myRecyclerView.visibility = View.VISIBLE
            binding.detalji.visibility = View.VISIBLE
            binding.tvProsecnaOcena.visibility = View.VISIBLE
            binding.twOcena.visibility = View.VISIBLE
            binding.imageButton3.visibility = View.VISIBLE

            binding.ptKomentar.text.clear()
            binding.vratiSeNazad.visibility = View.INVISIBLE
            hideKeyboard(requireActivity())
        }

        Ocenjivanje()
        EventChangeListener()
        dodajKomentar()


        return binding.root
    }

    private fun Ocenjivanje(){
        var kliknutiApartman: Apartman? = null
        viewModel.getclickedApartman().observe(viewLifecycleOwner, Observer { apartman ->
            if (apartman != null) {
                kliknutiApartman = apartman
            }
            apartmanViewModel.vratiProsecnuOcenu(kliknutiApartman!!.verifikacioniKod) { prosecnaOcena ->
                binding.tvProsecnaOcena.text = prosecnaOcena.toString()

            }
        })

        val ratingBar = binding.ratingBar
        val btnSubmit = binding.btnSubmit
        val ratingScale = binding.twOcena

        ratingBar.setOnRatingBarChangeListener{ratingBar, new, personOrComputer->
            ratingScale.text = new.toString()
            ocena = new.toInt()
            when (ratingBar.rating.toInt()){
                1-> ratingScale.text = "Vary bad"
                2-> ratingScale.text = "Bad"
                3-> ratingScale.text = "Good"
                4-> ratingScale.text = "Great"
                5-> ratingScale.text = "Awsome"
                else -> ratingScale.text = " "

            }
        }

        btnSubmit.setOnClickListener{
            var kliknutiApartman: Apartman? = null
            viewModel.getclickedApartman().observe(viewLifecycleOwner, Observer { apartman ->
                if (apartman != null) {
                    kliknutiApartman = apartman
                }

                apartmanViewModel.dodajOcenuApartmanu(kliknutiApartman!!.verifikacioniKod, ratingBar.rating.toInt())
                apartmanViewModel.azuriranjeProsecneOcene(kliknutiApartman!!.verifikacioniKod){prosecnaOcena ->
                    binding.tvProsecnaOcena.text = prosecnaOcena.toString()


                }
            })

            commentViewModel.azuriranjePoena(currentUser.email.toString(),3)
            Toast.makeText(requireContext(), "Dobili ste 3 poena", Toast.LENGTH_SHORT).show()


        }
    }
    private fun EventChangeListener(){
        var kliknutiApartman: Apartman? = null
        viewModel.getclickedApartman().observe(viewLifecycleOwner, Observer { apartman ->
            if (apartman != null) {
                kliknutiApartman = apartman
            }

            commentViewModel.getComments(kliknutiApartman!!.verifikacioniKod)

            commentViewModel.getIsFetchingLiveData().observe(viewLifecycleOwner, Observer { fetchingStatus ->
                if (!fetchingStatus) {
                    commentViewModel.getCommentListLiveData().observe(viewLifecycleOwner, Observer { commentList ->
                        if (commentList != null) {
                            Log.d("apartman", "Komentari : $commentList")
                            val myAdapter = MyRecyclerViewAdapter(commentList)
                            recyclerView.adapter = myAdapter
                        }
                    })
                }
            })

        })



    }
    fun dodajKomentar(){

        var kliknutiApartman: Apartman? = null
        binding.btnDodajKomentar.setOnClickListener{
            if(binding.ptKomentar.text.isEmpty()){
                    Toast.makeText(requireContext(), "Unesie tekst", Toast.LENGTH_SHORT).show()
            }else {
                 viewModel.getclickedApartman().observe(viewLifecycleOwner, Observer { apartman ->
                    if (apartman != null) {
                        kliknutiApartman = apartman
                        Log.d("apartman kliknutiii", "$apartman")
                    }
                 })

                 val noviKomentar = Comment(
                    tekst = binding.ptKomentar.text.toString(),
                    user = currentUser,
                    apartman = kliknutiApartman,
                    verifikacioniKodApartman = kliknutiApartman!!.verifikacioniKod
                 )

                 commentViewModel.dodajKomentar(noviKomentar)
                 commentViewModel.azuriranjePoena(currentUser.email.toString(), 5)
                 Toast.makeText(requireContext(), "Dobili ste 5 poena", Toast.LENGTH_SHORT).show()

                 EventChangeListener()
                 binding.IdNaslov.visibility = View.VISIBLE
                 binding.ratingBar.visibility = View.VISIBLE
                 binding.btnSubmit.visibility = View.VISIBLE
                 binding.Idnatpis.visibility = View.VISIBLE
                 binding.myRecyclerView.visibility = View.VISIBLE
                 binding.detalji.visibility = View.VISIBLE
                 binding.tvProsecnaOcena.visibility = View.VISIBLE
                 binding.ptKomentar.text.clear()
                 binding.vratiSeNazad.visibility = View.INVISIBLE
                 hideKeyboard(requireActivity())

                 Log.d("apartman kliknutiii", "$noviKomentar")
            }
        }



    }

    fun hideKeyboard(activity: Activity) {
        val inputMethodManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val currentFocusView = activity.currentFocus
        if (currentFocusView != null) {
            inputMethodManager.hideSoftInputFromWindow(currentFocusView.windowToken, 0)
        }
    }










}