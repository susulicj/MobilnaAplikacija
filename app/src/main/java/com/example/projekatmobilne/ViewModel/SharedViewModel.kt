package com.example.projekatmobilne.ViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.projekatmobilne.DataClasses.Apartman

class SharedViewModel: ViewModel() {
    private val clickedApartmanLiveData =  MutableLiveData<Apartman>()
    private val listaApartmanaLiveData = MutableLiveData<List<Apartman>>()

    fun getclickedApartman(): LiveData<Apartman> {
        return clickedApartmanLiveData
    }

    fun setclickedApartman(apartman: Apartman){
        clickedApartmanLiveData.value = apartman
    }
    fun getListaApartmana(): LiveData<List<Apartman>>? {
        Log.d("filterrr y shared", "${listaApartmanaLiveData.value}" )
        return listaApartmanaLiveData
    }


    fun setListaApartmana(apartmani: List<Apartman>) {
        listaApartmanaLiveData.value = apartmani
        Log.d("filterrr shared", "${listaApartmanaLiveData.value}" )
    }
}