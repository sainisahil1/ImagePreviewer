package io.sahil.imagepreviewer.ui.view.fragments

import android.app.Activity
import android.app.Instrumentation
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import io.sahil.imagepreviewer.databinding.FragmentHomeBinding
import io.sahil.imagepreviewer.ui.viewmodel.HomeViewModel
import java.io.File

class HomeFragment: Fragment() {

    private lateinit var fragmentContext: Context
    private lateinit var fragmentHomeBinding: FragmentHomeBinding
    private lateinit var homeViewModel: HomeViewModel

    private val selectImageFromGalleryResult = registerForActivityResult(ActivityResultContracts.GetContent()){
            uri: Uri? -> uri?.let { showImage(it) }
    }

    //remove the viewmodel, repository and util

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentHomeBinding = FragmentHomeBinding.inflate(inflater, container, false)

        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        homeViewModel.setContext(fragmentContext)

        //registerResultCallback()
        registerOnClick()


        return fragmentHomeBinding.root
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.fragmentContext = context
    }


    override fun onResume() {
        super.onResume()
       /* homeViewModel.imageLiveData.observe(viewLifecycleOwner, Observer {
            it?.also { showImage(it) } ?: showPlaceholder()
        })
        homeViewModel.fetchImage()*/
    }


    private fun showImage(imageUri: Uri){
        try {

            fragmentHomeBinding.image.visibility = View.VISIBLE
            fragmentHomeBinding.placeholder.visibility = View.GONE
            fragmentHomeBinding.image.setImageURI(imageUri)

        }catch (e: Exception){
            Toast.makeText(fragmentContext, "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }

    }

  /*  private fun showPlaceholder(){
        fragmentHomeBinding.image.visibility = View.GONE
        fragmentHomeBinding.placeholder.visibility = View.VISIBLE
    }*/

    private fun registerOnClick(){

        fragmentHomeBinding.galleryButton.setOnClickListener {

            selectImageFromGalleryResult.launch("image/*")

        }

        fragmentHomeBinding.cameraButton.setOnClickListener {



        }

    }

   /* private fun registerResultCallback(){
        resultCallback = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result ->
            if (result.resultCode == Activity.RESULT_OK){
                val data = result.data
            }

        }
    }*/






}