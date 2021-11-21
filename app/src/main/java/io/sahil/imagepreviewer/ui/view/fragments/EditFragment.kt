package io.sahil.imagepreviewer.ui.view.fragments

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.sahil.imagepreviewer.R
import io.sahil.imagepreviewer.databinding.FragmentEditBinding
import java.util.*
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.options
import io.sahil.imagepreviewer.utils.CropView
import java.io.ByteArrayOutputStream
import android.provider.MediaStore
import androidx.core.content.FileProvider
import io.sahil.imagepreviewer.BuildConfig
import java.io.File


class EditFragment(private val imageUri: Uri): Fragment(), View.OnClickListener {

    /*
    * 1. using imageUri as the default image we get from gallery or camera
    * 2. saving each bitmap while editing for undo history
    * 3. using java stack for undo
    * 4. For cropping, using a third party library. Creating own custom views was taking too much time.
    *       Can be done in free time
    * 5. Using deprecated methods to save temporary bitmap file. Deprecated after Build version 29. Can be changed after thorough research
    * */

    private lateinit var fragmentContext: Context
    private lateinit var fragmentEditBinding: FragmentEditBinding
    private var imageHistoryStack = Stack<Bitmap>()

    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            // use the returned uri
            val uriContent = result.uriContent
            val bitmap = MediaStore.Images.Media.getBitmap(fragmentContext.contentResolver, uriContent)
            imageHistoryStack.push(bitmap)
            setImageBitmap(bitmap)

        } else {
            // an error occurred
            val exception = result.error
            exception?.printStackTrace()
        }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentEditBinding = FragmentEditBinding.inflate(inflater, container, false)

        fragmentEditBinding.imageLayout.setImageURI(imageUri)
        registerOnClickListeners()

        return fragmentEditBinding.root
    }

    private fun setImageBitmap(bitmap: Bitmap){
        fragmentEditBinding.imageLayout.setImageBitmap(bitmap)
    }

    private fun registerOnClickListeners(){
        fragmentEditBinding.undo.setOnClickListener(this)
        fragmentEditBinding.rotateLeft.setOnClickListener(this)
        fragmentEditBinding.rotateRight.setOnClickListener(this)
        fragmentEditBinding.crop.setOnClickListener(this)
        fragmentEditBinding.save.setOnClickListener(this)
    }


    override fun onClick(view: View?) {

        when(view?.id){

            R.id.undo -> {
                    /*
                    * pop the last bitmap
                    * if stack empty set default image uri
                    * else peek top
                    * */
                    try {
                        imageHistoryStack.pop()
                        if (imageHistoryStack.empty()){
                            fragmentEditBinding.imageLayout.setImageURI(imageUri)
                        } else {
                            setImageBitmap(imageHistoryStack.peek())
                        }
                    }catch (e: EmptyStackException){
                        e.printStackTrace()
                        //nothing to do if no change is done
                    }
            }

            R.id.rotate_left -> {}

            R.id.rotate_right -> {}

            R.id.crop -> {
                if (imageHistoryStack.empty()){
                    cropImageResult(imageUri)
                } else {
                    val bitmap = imageHistoryStack.peek()
                    val bytes = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
                    val path: String = MediaStore.Images.Media.insertImage(
                        fragmentContext.contentResolver,
                        bitmap,
                        System.currentTimeMillis().toString(),
                        null
                    )
                    cropImageResult(Uri.parse(path))
                }
            }

            R.id.save -> {}

        }

    }


    private fun cropImageResult(picUri: Uri){
        cropImage.launch(
            options(uri = picUri)
        )
    }







}