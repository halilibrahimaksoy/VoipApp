package com.haksoy.voipapp.ui.discover

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.Marker

class MapsViewModel : ViewModel() {

    lateinit var markers: MutableLiveData<List<Marker>>


}