package com.example.projekatmobilne

import android.provider.Settings.Global.getString
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.projekatmobilne.DataClasses.Apartman
import com.example.projekatmobilne.ViewModel.SharedViewModel

class MyRecyclerViewMarker (private var markerList: List<Apartman>,  private val viewModel: SharedViewModel,private val navController: NavController): RecyclerView.Adapter<MyViewHolderMarker>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolderMarker {
        val leyoutInflater = LayoutInflater.from(parent.context)
        val listItem = leyoutInflater.inflate(R.layout.list_marker, parent, false)
        return MyViewHolderMarker(listItem)
    }

    override fun getItemCount(): Int {
      return markerList.size
    }

    override fun onBindViewHolder(holder: MyViewHolderMarker, position: Int) {
       val marker: Apartman = markerList[position]

        val broj = "Stan" + " " + "broj" + " " + marker.brojStana.toString()
        Log.d("prikaz", "$broj")
        holder.mySprat.text = broj

        holder.btnDetalji.setOnClickListener{
           viewModel.setclickedApartman(marker)
            navController.navigate(R.id.action_listaApartmanaMarkerFragment_to_commentsFragment)
        }


    }
}






class MyViewHolderMarker(val view: View) : RecyclerView.ViewHolder(view){
    var mySprat = view.findViewById<TextView>(R.id.IdDobijenaVrednost)
    var btnDetalji = itemView.findViewById<ImageButton>(R.id.btnKomentariIOene)
}