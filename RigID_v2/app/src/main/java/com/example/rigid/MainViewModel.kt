package com.example.rigid

import android.view.MenuItem
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.rigid.R

class MainViewModel : ViewModel() {

    val onFabButtonEvent: MutableLiveData<Unit?> = MutableLiveData()
    val onShutterButtonEvent: MutableLiveData<Unit?> = MutableLiveData()

    fun onBottomMenuClicked(item: MenuItem): Boolean {
        when (item.itemId) {
            //R.id.action_object_detect -> postVisionType(item)
        }
        //Object.isChecked = true
        return false
    }

    fun onClickFabButton(view: View) {
        onFabButtonEvent.postValue(Unit)
    }

    fun onClickShutter(view: View) {
        onShutterButtonEvent.postValue(Unit)
    }

    private fun postVisionType(){//type: VisionType) {
        //onItemSelectedEvent.postValue(type)
    }
}
