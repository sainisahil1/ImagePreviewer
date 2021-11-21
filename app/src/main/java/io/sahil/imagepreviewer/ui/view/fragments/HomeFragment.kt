package io.sahil.imagepreviewer.ui.view.fragments

import android.app.Activity
import android.app.Instrumentation
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.hardware.camera2.CameraCharacteristics
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import io.sahil.imagepreviewer.BuildConfig
import io.sahil.imagepreviewer.R
import io.sahil.imagepreviewer.databinding.FragmentHomeBinding
import io.sahil.imagepreviewer.ui.viewmodel.HomeViewModel
import java.io.File

class HomeFragment: Fragment() {

    private lateinit var fragmentContext: Context
    private lateinit var fragmentHomeBinding: FragmentHomeBinding
    private var cameraImageUri: Uri? = null

    private val selectImageFromGalleryResult = registerForActivityResult(ActivityResultContracts.GetContent()){
            uri: Uri? -> uri?.let { goToEditFragment(it) }
    }

    private val captureImageFromCameraResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){

        result ->

            if (result.resultCode == Activity.RESULT_OK){
                val data = result.data
                Log.e(tag, "data: ${data.toString()}")
                cameraImageUri?.let { goToEditFragment(it) }
            }

    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        cameraImageUri?.let { outState.putString("cameraImageUri", cameraImageUri.toString()) }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        savedInstanceState?.let {
            if (savedInstanceState.containsKey("cameraImageUri")){
                cameraImageUri = Uri.parse(savedInstanceState.getString("cameraImageUri"))
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentHomeBinding = FragmentHomeBinding.inflate(inflater, container, false)


        registerOnClick()


        return fragmentHomeBinding.root
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.fragmentContext = context
    }


    private fun goToEditFragment(imageUri: Uri){

        val fragment = EditFragment(imageUri)

        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.main_frame, fragment, EditFragment::class.java.simpleName)
            ?.addToBackStack(null)
            ?.commit()

        /*activity?.supportFragmentManager?.setFragmentResultListener("savedImage"){
            key, bundle ->
            if (key.equals("savedImage")){

            }
        }*/

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



    private fun registerOnClick(){

        fragmentHomeBinding.galleryButton.setOnClickListener {

            selectImageFromGalleryResult.launch("image/*")

        }

        fragmentHomeBinding.cameraButton.setOnClickListener {

            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            cameraIntent.putExtra("com.google.assistant.extra.USE_FRONT_CAMERA", true)
            cameraIntent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true)
            cameraIntent.putExtra("android.intent.extras.LENS_FACING_FRONT", 1)
            cameraIntent.putExtra("android.intent.extras.CAMERA_FACING", 1)

            // Samsung
            cameraIntent.putExtra("camerafacing", "front")
            cameraIntent.putExtra("previous_mode", "front")


            cameraImageUri = getTmpFileUri()

            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri)

            captureImageFromCameraResult.launch(cameraIntent)

        }

    }

    private fun getTmpFileUri(): Uri {
        val tmpFile = File.createTempFile("tmp_image_file", ".png", fragmentContext.cacheDir).apply {
            createNewFile()
            deleteOnExit()
        }

        return FileProvider.getUriForFile(fragmentContext, "${BuildConfig.APPLICATION_ID}.provider", tmpFile)
    }







}