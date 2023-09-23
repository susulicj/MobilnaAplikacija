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
        holder.adresa.text = "Ulica: ${apartman.adresa}"
        holder.povrsina.text = "Povrsina: ${apartman.povrsina.toString()} m²"
        holder.brojSoba.text = "Broj soba: ${apartman.brojSoba.toString()}"
        holder.brojTelefona.text = "Broj telefona: ${apartman.brojTelefona.toString()}"
        holder.emaill.text = "Email: ${apartman.email}"
        holder.brojapa.text = (position + 1).toString()
        holder.broj.text = "Broj: ${apartman.brojZgrade}/${apartman.sprat}/${apartman.brojStana} "


    }

}



class MyViewHolderApartman(val view: View): RecyclerView.ViewHolder(view){
    val adresa = view.findViewById<TextView>(R.id.IdDobijenaAdresa)
    val povrsina = view.findViewById<TextView>(R.id.IdDobijenaPovrsina)
    val brojSoba = view.findViewById<TextView>(R.id.IdDobijeniBrojSoba)
    val brojTelefona = view.findViewById<TextView>(R.id.IdDobijeniBrojTelefona)
    val emaill = view.findViewById<TextView>(R.id.IdDobijeniEmailKontakt)
    val brojapa = view.findViewById<TextView>(R.id.broj)
    val broj = view.findViewById<TextView>(R.id.IddobijeniBrojStana)




}