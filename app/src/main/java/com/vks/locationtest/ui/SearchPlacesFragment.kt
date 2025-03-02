package com.vks.locationtest.ui

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.libraries.places.api.net.PlacesClient
import com.vks.locationtest.R
import com.vks.locationtest.database.LocationEntity
import com.vks.locationtest.databinding.FragmentSearchPlacesBinding
import com.vks.locationtest.ui.adapter.PlacesResultAdapter

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SearchPlacesFragment : Fragment() {

    private var _binding: FragmentSearchPlacesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var placesClient: PlacesClient
    private lateinit var viewModel: LocationViewModel
    private var googleMap: GoogleMap? = null
    private lateinit var locationEntity: LocationEntity
    private var id: Int = 0

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
        viewModel = ViewModelProvider(this)[LocationViewModel::class.java]
        _binding = FragmentSearchPlacesBinding.inflate(inflater, container, false)
        return binding.root

    }

    @Suppress("DEPRECATION")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        id = arguments?.getInt("id") ?: 0
        binding.back.setOnClickListener {
            findNavController().navigateUp()
        }
        val supportMapFragment =
            binding.map.getFragment<SupportMapFragment>() as? SupportMapFragment?
        supportMapFragment?.getMapAsync { map ->
            googleMap = map
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(22.0, 75.0), 4F))
        }
        if (id > 0)
            viewModel.getLocation(id) {
                locationEntity = it
                Handler(Looper.getMainLooper()).postDelayed({
                    val latLng = LatLng(it.latitude, it.longitude)
                    googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14F))
                    val marker = MarkerOptions().position(latLng).title(it.address)
                    googleMap?.addMarker(marker)?.showInfoWindow()
                }, 2000)
                binding.locationConfirm.isVisible = true
                binding.save.text = getString(R.string.update)
            }
        val placeAdapter = PlacesResultAdapter(requireContext(), onClick = { prediction ->
            val name = prediction.getPrimaryText(null)
            val address = prediction.getSecondaryText(null)
            val placeFields = listOf(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.LAT_LNG,
                Place.Field.ADDRESS
            )
            val request = FetchPlaceRequest.newInstance(prediction.placeId, placeFields)
            placesClient.fetchPlace(request)
                .addOnSuccessListener { response: FetchPlaceResponse ->
                    val place = response.place
                    val latLng = place.latLng
                    googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14F))
                    val marker = MarkerOptions().position(latLng).title(address.toString())
                    googleMap?.addMarker(marker)?.showInfoWindow()
                    locationEntity =
                        LocationEntity(
                            id,
                            prediction.placeId,
                            name.toString(),
                            address.toString(),
                            latLng.latitude,
                            latLng.longitude
                        )
                    hideKeyboard()
                    binding.locationConfirm.isVisible = true
                    binding.result.isVisible = false
                    binding.placeResults.isVisible = false
                    binding.search.setText("")
                }.addOnFailureListener { exception: Exception ->
                    Toast.makeText(requireContext(), exception.message, Toast.LENGTH_SHORT)
                        .show()
                }
        }, onResult = {
            binding.result.isVisible = it > 0
            binding.placeResults.isVisible = it > 0
            binding.progressBar.isVisible = false
        })
        binding.placeResults.layoutManager = LinearLayoutManager(requireContext())
        binding.placeResults.adapter = placeAdapter
        binding.search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                if (s.length > 2) {
                    binding.progressBar.isVisible = true
                    binding.locationConfirm.isVisible = false
                    placeAdapter.filter.filter(s.toString())
                } else if (placeAdapter.itemCount == 0) {
                    binding.result.isVisible = false
                    binding.placeResults.isVisible = false
                }
            }
        })
        binding.save.setOnClickListener {
            viewModel.insertOrUpdate(locationEntity) {
                Handler(Looper.getMainLooper()).postDelayed({
                    findNavController().popBackStack()
                }, 1000)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

/**
 * Hide keyboard if showing
 */
fun Fragment.hideKeyboard() {
    if (activity?.currentFocus != null) {
        (activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(activity?.currentFocus!!.windowToken, 0)
    }
}