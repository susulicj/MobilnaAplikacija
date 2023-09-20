package com.example.projekatmobilne

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.projekatmobilne.DataClasses.Comment
import com.example.projekatmobilne.DataClasses.User
import com.google.firebase.storage.FirebaseStorage

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
        preuzmiFotografiju(user.profileImageUrl, holder.myImage,onFailure = { exception ->

        } )

    }

    fun preuzmiFotografiju(imeSlike: String?, imageView: ImageView, onFailure: (Exception) -> Unit) {
        val storage = FirebaseStorage.getInstance()
        val storageReference = storage.reference

        if(imeSlike != null) {

            val imagePath = "slike/$imeSlike"

            val imageRef = storageReference.child(imagePath)

            imageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener { bytes ->


                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                imageView.setImageBitmap(bitmap)

            }.addOnFailureListener { exception ->
                onFailure(exception)
            }
        }else{
            imageView.setImageResource(R.drawable.images)
        }
    }

}

class MyViewHolderUser(val view: View): RecyclerView.ViewHolder(view){
    val myTextPoeni = view.findViewById<TextView>(R.id.tvValuePoeni)
    val myTextKorisnickoIme = view.findViewById<TextView>(R.id.tvValueKI)
    val myImage = view.findViewById<ImageView>(R.id.IdSlikaKorisnika)

}