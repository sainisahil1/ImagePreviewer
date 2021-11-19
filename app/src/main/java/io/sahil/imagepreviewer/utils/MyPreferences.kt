package io.sahil.imagepreviewer.utils

import android.content.Context
import android.content.SharedPreferences

class MyPreferences(context: Context) {

    private val myPreferences = "MY_PREFERENCES"
    private val imageLocation = "IMAGE_LOCATION"
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(myPreferences, Context.MODE_PRIVATE)

    fun getImageSaveLocation(): String?{
        return sharedPreferences.getString(imageLocation, null)
    }

    fun setImageSaveLocation(location: String){
        val editor = sharedPreferences.edit()
        editor.putString(imageLocation, location)
        editor.apply()
    }

    fun clearImageSaveLocation(){
        val editor = sharedPreferences.edit()
        editor.remove(imageLocation)
        editor.apply()
    }


}