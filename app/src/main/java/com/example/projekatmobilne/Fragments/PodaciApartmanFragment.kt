package com.example.projekatmobilne.Fragments

import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.example.projekatmobilne.DataClasses.Apartman
import com.example.projekatmobilne.DataClasses.User
import com.example.projekatmobilne.MyRecyclerViewMarker
import com.example.projekatmobilne.R
import com.example.projekatmobilne.ViewModel.AddApartmentViewModel
import com.example.projekatmobilne.ViewModel.AddCommentViewModel
import com.example.projekatmobilne.ViewModel.SharedViewModel
import com.example.projekatmobilne.databinding.FragmentPodaciApartmanBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class PodaciApartmanFragment : Fragment() {

    private lateinit var binding: FragmentPodaciApartmanBinding
    private lateinit var viewModel: SharedViewModel
    private lateinit var commentviewModel:AddCommentViewModel
    private lateinit var currentApartman : Apartman
    private lateinit var currentUser: User
    private lateinit var currentFirebaseUser: FirebaseUser
    private lateinit var email:String
    private lateinit var verKod: String
    private lateinit var viewModelApartman: AddApartmentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPodaciApartmanBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        commentviewModel = ViewModelProvider(requireActivity()).get(AddCommentViewModel::class.java)
        viewModelApartman = ViewModelProvider(requireActivity()).get(AddApartmentViewModel::class.java)
        currentFirebaseUser = FirebaseAuth.getInstance().currentUser!!
        currentUser = User(currentFirebaseUser.email)
        email = " "
        verKod = " "

        binding.btnNazad6.setOnClickListener{
            it.findNavController().navigate(R.id.action_podaciApartmanFragment_to_commentsFragment)
        }


        viewModel.getclickedApartman().observe(viewLifecycleOwner, Observer { apartman ->
            if (apartman != null) {
                currentApartman = apartman
                val apartman = currentApartman // Pretpostavimo da je ovo vaš trenutni apartman

                binding.IdAdresaApartmana.text = "Ulica: ${apartman.adresa}"
                binding.IdemailStana.text = "Email: ${apartman.email}"
                binding.idBrojSobaApatmana.text = "Broj soba:  ${apartman.brojSoba}"
                binding.IdPovrsinaStana.text = "Površina: ${apartman.povrsina} m²"
                binding.IdSpratStana.text = "Broj: ${apartman.brojZgrade}/${apartman.sprat}/${apartman.brojStana}"
                binding.IdBrojTelefonaStana.text = "Broj telefona: ${apartman.brojTelefona}"


                verKod = currentApartman.verifikacioniKod
                if(currentApartman.user!!.email.toString() == currentUser.email.toString()){
                    Log.d("vidljivost", "${currentApartman.user!!.email.toString()}")
                    Log.d("vidljivost", "${currentUser.email.toString()}")
                    binding.btnIzbrisi.visibility = View.VISIBLE
                }


            }
        })


        binding.btnIzbrisi.setOnClickListener{
            deleteApartmentAsync(verKod)
            Toast.makeText(requireContext(), "Uspešno ste izbrisali stan", Toast.LENGTH_SHORT).show()
            commentviewModel.azuriranjePoena(currentUser.email.toString(), -2)
            Toast.makeText(requireContext(), "Smanjeno Vam je 2 poena", Toast.LENGTH_SHORT).show()


            var filtriranaLista : List<Apartman>? = null
            viewModel.getListaMakera().observe(viewLifecycleOwner, Observer{apartmani->
                Log.d("prikaz", "$apartmani")

                  filtriranaLista = apartmani.filter {apartman->
                    apartman.verifikacioniKod != verKod }




            })

            viewModel.setListaMarkera(filtriranaLista!!)
            it.findNavController().navigate(R.id.action_podaciApartmanFragment_to_listaApartmanaMarkerFragment)

        }


        return binding.root


    }

    private fun deleteApartmentAsync(apartmentId: String) {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                viewModelApartman.deleteApartment(apartmentId)

            } catch (e: Exception) {

                withContext(Dispatchers.Main) {

                }
            }
        }
    }


}



