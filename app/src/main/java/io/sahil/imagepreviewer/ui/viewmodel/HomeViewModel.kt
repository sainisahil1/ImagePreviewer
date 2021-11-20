package io.sahil.imagepreviewer.ui.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.sahil.imagepreviewer.utils.MyPreferences

class HomeViewModel: ViewModel() {

    /*
    * using context in viewmodel because the repository calls shared preferences to get data
    * */

    private lateinit var fragmentContext: Context

    var imageLiveData: MutableLiveData<Uri> = MutableLiveData()

    private val tag = HomeViewModel::class.java.simpleName

    fun fetchImage(){
        val imageUriString = MyPreferences(fragmentContext).getImageSaveLocation()
        Log.e(tag, "Image path: $imageUriString")
        imageLiveData.postValue(Uri.parse(imageUriString))
    }

    fun saveImage(imageLocation: String){
        MyPreferences(fragmentContext).setImageSaveLocation(imageLocation)
    }

    fun setContext(context: Context){
        fragmentContext = context
    }

}