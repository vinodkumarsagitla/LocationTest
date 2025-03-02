package com.vks.locationtest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.vks.locationtest.databinding.FragmentRouteBinding

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class RouteFragment : Fragment() {

    private var _binding: FragmentRouteBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var placesClient: PlacesClient
    private lateinit var viewModel: LocationViewModel
    private var googleMaps: GoogleMap? = null
    private lateinit var locationEntity: LocationEntity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize the SDK
        Places.initialize(requireContext(), getString(R.string.google_api_key))
        placesClient = Places.createClient(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(LocationViewModel::class.java)
        _binding = FragmentRouteBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.back.setOnClickListener {
            findNavController().navigateUp()
        }
        val supportMapFragment =
            binding.map.getFragment<SupportMapFragment>() as? SupportMapFragment?
        supportMapFragment?.getMapAsync { map ->
            googleMaps = map
           // map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(22.0, 75.0), 4F))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}