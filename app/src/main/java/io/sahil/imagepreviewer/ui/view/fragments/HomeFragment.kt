package io.sahil.imagepreviewer.ui.view.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentHomeBinding = FragmentHomeBinding.inflate(inflater, container, false)

        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        homeViewModel.setContext(fragmentContext)

        return fragmentHomeBinding.root
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.fragmentContext = context
    }


    override fun onResume() {
        super.onResume()
        homeViewModel.imageLiveData.observe(viewLifecycleOwner, Observer {
            it?.also { showImage(it) } ?: showPlaceholder()
        })
        homeViewModel.fetchImage()
    }


    private fun showImage(location: String){
        try {

            val file: File = File(location)
            fragmentHomeBinding.image.visibility = View.VISIBLE
            fragmentHomeBinding.placeholder.visibility = View.GONE
            Glide.with(fragmentHomeBinding.image)
                .load(file)
                .into(fragmentHomeBinding.image)

        }catch (e: Exception){
            Toast.makeText(fragmentContext, "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }

    }

    private fun showPlaceholder(){
        fragmentHomeBinding.image.visibility = View.GONE
        fragmentHomeBinding.placeholder.visibility = View.VISIBLE
    }



}