package com.vks.locationtest

import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.vks.locationtest.databinding.FragmentLocationSourceBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class LocationSourceFragment : Fragment() {

    private var _binding: FragmentLocationSourceBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var viewModel: LocationViewModel
    private var mainList = ArrayList<LocationEntity>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[LocationViewModel::class.java]
        _binding = FragmentLocationSourceBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addPOI.setOnClickListener {
            findNavController().navigate(R.id.toSearchPlaces)
        }
        binding.addPOI2.setOnClickListener {
            findNavController().navigate(R.id.toSearchPlaces)
        }
        val adapter = LocationAdapter()
        binding.location.layoutManager = LinearLayoutManager(requireContext())
        viewModel.allLocations.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                binding.location.isVisible = false
                binding.addPOI2.isVisible = false
                binding.emptyLayout.isVisible = true
            } else {
                binding.location.isVisible = true
                binding.addPOI2.isVisible = true
                binding.emptyLayout.isVisible = false
            }
            mainList = it as ArrayList<LocationEntity>
            adapter.list = getLocations()
            binding.location.adapter = adapter
            binding.route.isVisible = it.size > 1

        }
        adapter.onDeleteClicked = {
            DeleteConfirmDialog().apply {
                onOkClicked = {
                    viewModel.delete(it)
                }
            }.showNow(childFragmentManager, null)
        }
        adapter.onEditClicked = {
            val bundle = Bundle()
            bundle.putInt("id", it.id)
            findNavController().navigate(R.id.toSearchPlaces, bundle)
        }
        binding.route.setOnClickListener {
            findNavController().navigate(R.id.toRoute)
        }
        binding.sort.setOnClickListener {
            SortByBottomSheetDialog().apply {
                onApplyClicked = { type ->
                    if (type == 0)
                        adapter.list = adapter.list.sortedBy { it.distance }
                    else adapter.list = adapter.list.sortedByDescending { it.distance }
                    binding.location.adapter = adapter
                }
                onClearClicked = {
                    adapter.list = getLocations()
                    binding.location.adapter = adapter
                }
            }.showNow(childFragmentManager, "")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getLocations(): List<LocationModal> {
        var lat = 0.0
        var lng = 0.0
        mainList.firstOrNull()?.let {
            lat = it.latitude
            lng = it.longitude
        }
        val list = mutableListOf<LocationModal>()
        mainList.forEach {
            list.add(
                LocationModal(
                    id = it.id,
                    placeId = it.placeId,
                    name = it.name,
                    address = it.address,
                    latitude = it.latitude,
                    longitude = it.longitude,
                    distance = getDistance(lat, lng, it.latitude, it.longitude)
                )
            )
        }
        return list
    }

    private fun getDistance(
        fromLatitude: Double,
        fromLongitude: Double,
        toLatitude: Double,
        toLongitude: Double
    ): Float {
        val results = floatArrayOf(0f, 0f, 0f)
        Location.distanceBetween(
            fromLatitude,
            fromLongitude,
            toLatitude,
            toLongitude,
            results
        )
        return (results[0] / 1000)
    }
}