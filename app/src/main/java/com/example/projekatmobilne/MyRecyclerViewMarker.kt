package com.example.projekatmobilne

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
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
        holder.mySprat.text = marker.sprat.toString()
        holder.btnDetalji.setOnClickListener{
           viewModel.setclickedApartman(marker)
            navController.navigate(R.id.action_listaApartmanaMarkerFragment_to_commentsFragment)
        }


    }
}






class MyViewHolderMarker(val view: View) : RecyclerView.ViewHolder(view){
    var mySprat = view.findViewById<TextView>(R.id.IdDobijenaVrednost)
    var btnDetalji = itemView.findViewById<Button>(R.id.btnKomentariIOene)
}