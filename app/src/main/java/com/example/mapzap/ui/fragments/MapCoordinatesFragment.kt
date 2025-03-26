package com.example.mapzap.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.mapzap.databinding.FragmentMapCoordinatesBinding
import com.example.mapzap.domain.isValidLatLng
import com.example.mapzap.ui.MapViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class MapCoordinatesFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentMapCoordinatesBinding

    private lateinit var mapViewModel: MapViewModel


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = requireActivity()
        mapViewModel = ViewModelProvider(activity)[MapViewModel::class.java]
        binding.goBtn.setOnClickListener {
            passCoordinates()
        }
    }

    private fun passCoordinates() {
        val latitude = binding.latitudeInput.text.toString().toDoubleOrNull()
        val longitude = binding.longitudeInput.text.toString().toDoubleOrNull()

        if (latitude != null && longitude != null && isValidLatLng(latitude, longitude)) {
            mapViewModel._latLng.value = LatLng(latitude, longitude)

        } else {
            val activity = requireActivity()
            Toast.makeText(
                activity, "please enter the valid latitude and longitude", Toast.LENGTH_LONG
            ).show()
        }
        binding.latitudeInput.setText("")
        binding.longitudeInput.setText("")
        dismiss()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapCoordinatesBinding.inflate(inflater, container, false)
        return binding.root

    }


}