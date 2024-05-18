package com.example.rigid

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.example.rigid.databinding.FragmentSecondBinding

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null
    private lateinit var croppedImage: ImageView
    private lateinit var imageView: ImageView
    //private val cardEntries = mutableListOf<CardAdapter.CardEntry>()
    val image = MutableLiveData<Bitmap>()
    val guitarClass = MutableLiveData<String>()
    val pickupConfig = MutableLiveData<String>()

    // This property is only valid between onCreateView and
    // onDestroyView
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        imageView = (view?.findViewById(R.id.imageView) ?: null)!!

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSecond.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }

        val bundle = arguments
        val message = bundle?.getString("message")

        // In FragmentTwo:
        image.observe(viewLifecycleOwner) { bitmap ->
            // Update the UI with the latest image
            //binding.imageView.setImageBitmap(bitmap)
        }

        guitarClass.observe(viewLifecycleOwner) { string ->
            // Update the UI with the latest string1
            //binding.Description.text = string
        }

        pickupConfig.observe(viewLifecycleOwner) { string ->
            // Update the UI with the latest string2
            //binding.PickupConfiguration.text = string
        }

        /*

        val args = arguments
        val croppedBitmap = args?.getParcelable<Bitmap>("cropped_bitmap")


        val imageView = binding.textviewSecond
        imageView.setImageBitmap(croppedBitmap)
 */
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}