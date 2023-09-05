package com.example.projekatmobilne.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.projekatmobilne.DataClasses.Apartman

class MapsViewModel (sharedViewModel: SharedViewModel) : ViewModel() {
    private var apartmanLiveData: LiveData<Apartman>? = null

    init {
        apartmanLiveData = sharedViewModel.getclickedApartman()
    }

    fun getApartmanLiveData(): LiveData<Apartman>? {
        return apartmanLiveData
    }
}