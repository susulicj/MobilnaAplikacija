package com.example.projekatmobilne

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.projekatmobilne.DataClasses.Comment

class MyRecyclerViewAdapter(private var commentList: ArrayList<Comment>): RecyclerView.Adapter<MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val leyoutInflater = LayoutInflater.from(parent.context)
        val listItem = leyoutInflater.inflate(R.layout.list_item, parent, false)
        return MyViewHolder(listItem)
    }

    override fun getItemCount(): Int {
       return commentList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val comment : Comment = commentList[position]
        holder.myTextName.text = comment.user!!.email
        holder.myText.text = comment.tekst
   }

}



class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view){
    //ovde cu da stavim 3 texta i onda gore da ga napunim s podacima
    var myTextName = view.findViewById<TextView>(R.id.tvname)
    var myText = view.findViewById<TextView>(R.id.tvTekst)
}