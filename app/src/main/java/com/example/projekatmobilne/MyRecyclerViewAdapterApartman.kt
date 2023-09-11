package com.example.projekatmobilne

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.projekatmobilne.DataClasses.Apartman

class MyRecyclerViewAdapterApartman(private var apartmanList: List<Apartman>) : RecyclerView.Adapter<MyViewHolderApartman>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolderApartman {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listApartman = layoutInflater.inflate(R.layout.list_apartmans, parent, false)
        return MyViewHolderApartman(listApartman)
    }

    override fun getItemCount(): Int {
        return apartmanList.size
    }

    override fun onBindViewHolder(holder: MyViewHolderApartman, position: Int) {
        val apartman: Apartman = apartmanList[position]
        holder.myText.text = apartman.verifikacioniKod
    }

}



class MyViewHolderApartman(val view: View): RecyclerView.ViewHolder(view){
    val myText = view.findViewById<TextView>(R.id.apartman)


}