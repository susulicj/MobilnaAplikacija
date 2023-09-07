package com.example.projekatmobilne

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.projekatmobilne.DataClasses.Comment
import com.example.projekatmobilne.DataClasses.User

class MyRecyclerViewAdapterUser(private var userList: List<User>) : RecyclerView.Adapter<MyViewHolderUser>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolderUser {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listUser = layoutInflater.inflate(R.layout.list_users, parent, false)
        return MyViewHolderUser(listUser)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: MyViewHolderUser, position: Int) {
        val user: User = userList[position]
        holder.myTextKorisnickoIme.text = user.korisnickoIme
        holder.myTextPoeni.text = user.poeni.toString()
    }

}

class MyViewHolderUser(val view: View): RecyclerView.ViewHolder(view){
    val myTextPoeni = view.findViewById<TextView>(R.id.tvValuePoeni)
    val myTextKorisnickoIme = view.findViewById<TextView>(R.id.tvValueKI)

}