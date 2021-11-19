package io.sahil.imagepreviewer.ui.view.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import io.sahil.imagepreviewer.R
import io.sahil.imagepreviewer.ui.view.fragments.HomeFragment
import io.sahil.imagepreviewer.utils.MyPreferences

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction()
            .add(R.id.main_frame, HomeFragment(), HomeFragment::class.java.simpleName)
            .commit()


    }

    override fun onDestroy() {
        MyPreferences(applicationContext).clearImageSaveLocation()
        super.onDestroy()
    }

}