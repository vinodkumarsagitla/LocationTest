package com.vks.locationtest.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.directions.route.AbstractRouting
import com.directions.route.Route
import com.directions.route.RouteException
import com.directions.route.Routing
import com.directions.route.RoutingListener
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.vks.locationtest.R
import com.vks.locationtest.databinding.FragmentRouteBinding
import com.vks.locationtest.modal.LocationModal

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
    private var googleMap: GoogleMap? = null
    private var latLngList = ArrayList<LatLng>()
    private var latLngBounds = LatLngBounds.Builder()

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
            googleMap = map
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(22.0, 75.0), 4F))
            list.forEach {
                val latLng = LatLng(it.latitude, it.longitude)
                googleMap?.addMarker(MarkerOptions().position(latLng))
                latLngList.add(latLng)
                latLngBounds.include(latLng)
            }
            val routing = Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(routingListener)
                .waypoints(latLngList)
                .key(resources.getString(R.string.google_api_key))
                .build()
            routing.execute()
            setMapCameraZoom()
        }
    }

    private fun setMapCameraZoom() {
        /*Set map center to view all points*/
        val bounds = latLngBounds.build()
        val width = binding.mapLayout.measuredWidth
        val height = binding.mapLayout.measuredHeight
        val padding = (height * 0.15).toInt() // offset from edges of the map in pixels
        val cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding)
        googleMap?.moveCamera(cu)
        val currentZoom = googleMap?.cameraPosition?.zoom
        if ((currentZoom ?: 0f) > 16f)
            googleMap?.moveCamera(CameraUpdateFactory.zoomTo(16f))
        /*Scroll up map view*/
//        val projection = googleMap.projection
//        val point = projection.toScreenLocation(googleMap.cameraPosition.target)
//        point.y += resources.getDimensionPixelSize(R.dimen.horizontal_margin)
//        googleMap?.moveCamera(CameraUpdateFactory.newLatLng(projection.fromScreenLocation(point)))
    }

    private var routingListener: RoutingListener = object : RoutingListener {
        override fun onRoutingCancelled() {
            binding.loadingLayout.isVisible = false
            Log.i(javaClass.name, "onRoutingCancelled ********************")
        }

        override fun onRoutingStart() {
            Log.i(javaClass.name, "onRoutingStart ***********************")
        }

        override fun onRoutingFailure(ex: RouteException) {
            binding.loadingLayout.isVisible = false
            Log.i(javaClass.name, "onRoutingFailure *********************** ${ex.message}")
            ex.printStackTrace()
        }

        override fun onRoutingSuccess(route: ArrayList<Route>, shortestRouteIndex: Int) {
            binding.loadingLayout.isVisible = false
            Log.i(javaClass.name, "onRoutingSuccess ********************* $shortestRouteIndex")
            if (route.size > shortestRouteIndex) {
                Log.i(javaClass.name, "Distance: " + route[shortestRouteIndex].distanceText)
                Log.i(javaClass.name, "Distance meter: " + route[shortestRouteIndex].distanceValue)
                Log.i(javaClass.name, "Duration: " + route[shortestRouteIndex].durationText)
                Log.i(javaClass.name, "Duration sec: " + route[shortestRouteIndex].durationValue)
                val polylineOptions = PolylineOptions()
                polylineOptions.color(ContextCompat.getColor(activity!!, R.color.color_primary))
                Log.i(javaClass.name, "Route List Size : " + route[shortestRouteIndex].points.size)
                polylineOptions.width(10f)
                polylineOptions.add(latLngList[0])
                polylineOptions.addAll(route[shortestRouteIndex].points)
                polylineOptions.add(latLngList[latLngList.size - 1])
                googleMap?.addPolyline(polylineOptions)
            } else {
                Log.i(javaClass.name, "No route found. $route")
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        var list: List<LocationModal> = emptyList()
    }
}