package com.example.projekatmobilne.Fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projekatmobilne.DataClasses.Apartman
import com.example.projekatmobilne.DataClasses.Comment
import com.example.projekatmobilne.DataClasses.User
import com.example.projekatmobilne.MyRecyclerViewAdapter
import com.example.projekatmobilne.R
import com.example.projekatmobilne.ViewModel.AddApartmentViewModel
import com.example.projekatmobilne.ViewModel.AddCommentViewModel
import com.example.projekatmobilne.ViewModel.CommentViewModel
import com.example.projekatmobilne.ViewModel.SharedViewModel
import com.example.projekatmobilne.databinding.FragmentCommentsBinding
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await

class CommentsFragment : Fragment() {
    private lateinit var binding: FragmentCommentsBinding
    private lateinit var viewModel: SharedViewModel
    private lateinit var currentFirebaseUser: FirebaseUser
    private lateinit var currentUser: User
    private lateinit var commentViewModel: AddCommentViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var db: FirebaseFirestore
    private lateinit var myAdapter: MyRecyclerViewAdapter
    private lateinit var commentList : ArrayList<Comment>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        commentViewModel = ViewModelProvider(this).get(AddCommentViewModel::class.java)
        viewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        binding = FragmentCommentsBinding.inflate(inflater, container, false)
        currentFirebaseUser = FirebaseAuth.getInstance().currentUser!!
        currentUser = User(currentFirebaseUser.email)


        recyclerView = binding.myRecyclerView
        recyclerView.setBackgroundColor(Color.WHITE)
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())

        commentList = arrayListOf()

        EventChangeListener()


        dodajKomentar()


        return binding.root
    }
    private fun EventChangeListener(){
        var kliknutiApartman: Apartman? = null
        viewModel.getclickedApartman().observe(viewLifecycleOwner, Observer { apartman ->
            if (apartman != null) {
                kliknutiApartman = apartman
            }

            commentViewModel.getComments(kliknutiApartman!!.verifikacioniKod)

            commentViewModel.getIsFetchingLiveData().observe(this, Observer { fetchingStatus ->
                if (!fetchingStatus) {
                    commentViewModel.getCommentListLiveData().observe(this, Observer { commentList ->
                        if (commentList != null) {
                            Log.d("apartman", "Komentari : $commentList")
                            val myAdapter = MyRecyclerViewAdapter(commentList)
                            recyclerView.adapter = myAdapter
                        }
                    })
                }
            })




            /* db = FirebaseFirestore.getInstance()
             db.collection("komentari")
                 .whereEqualTo("verifikacioniKodApartman", kliknutiApartman!!.verifikacioniKod)
                 .get()
                 .addOnSuccessListener {
                     Log.d("apartmanjes", "${it.documents}")
                     if (!it.isEmpty) {
                         for (document in it.documents) {
                             val commentData = document.data // Dohvatite podatke za dokument

                             if (commentData != null) {
                                 val tekst = commentData["tekst"] as String? // Dohvatite vrednost za "tekst" polje
                                 val userMap = commentData["user"] as Map<String, Any>? // Dohvatite mapu za "user" polje
                                 val apartmanMap = commentData["apartman"] as Map<String, Any>? // Dohvatite mapu za "apartman" polje
                                 val verifikacioniKodApartman = commentData["verifikacioniKodApartman"] as String? // Dohvatite vrednost za "verifikacioniKodApartman" polje

                                 // Ručno konstruišite Comment objekat koristeći podatke iz dokumenta
                                 val comment = Comment(
                                     tekst = tekst,
                                     user = userMap?.let { createUserFromMap(it) }, // Kreirajte User objekat iz mape
                                     apartman = apartmanMap?.let { createApartmanFromMap(it) }, // Kreirajte Apartman objekat iz mape
                                     verifikacioniKodApartman = verifikacioniKodApartman
                                 )

                                 commentList.add(comment)
                                 Log.d("apartmanjes", "$comment")
                                 myAdapter = MyRecyclerViewAdapter(commentList)
                                 recyclerView.adapter = myAdapter
                             }
                         }
                     }


                 }
                 .addOnFailureListener{
                     Log.d("apartman", "$it")
                 }


 */
        })



    }
    fun dodajKomentar(){

        var kliknutiApartman: Apartman? = null

        binding.btnDodajKomentar.setOnClickListener{
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
            EventChangeListener()

            Log.d("apartman kliknutiii", "$noviKomentar")
        }
    }










}