package com.example.projekatmobilne.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.projekatmobilne.DataClasses.Apartman

class SharedViewModel: ViewModel() {
    private val clickedApartmanLiveData =  MutableLiveData<Apartman>()

    fun getclickedApartman(): LiveData<Apartman> {
        return clickedApartmanLiveData
    }

    fun setclickedApartman(apartman: Apartman){
        clickedApartmanLiveData.value = apartman
    }
}