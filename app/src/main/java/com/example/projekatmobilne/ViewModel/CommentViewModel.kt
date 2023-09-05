package com.example.projekatmobilne.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.projekatmobilne.DataClasses.Apartman
import com.example.projekatmobilne.DataClasses.Comment
import com.google.firebase.firestore.FirebaseFirestore

class CommentViewModel(sharedViewModel: SharedViewModel) : ViewModel() {
    private val apartmanLiveData: LiveData<Apartman> = sharedViewModel.getclickedApartman()

    fun getApartmanLiveData(): LiveData<Apartman> {
        return apartmanLiveData
    }


}