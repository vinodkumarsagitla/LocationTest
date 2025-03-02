package com.vks.locationtest.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.material.textview.MaterialTextView
import com.vks.locationtest.R

class PlacesResultAdapter(
    private val mContext: Context,
    val onClick: (prediction: AutocompletePrediction) -> Unit,
    val onResult: (Int) -> Unit,
) : RecyclerView.Adapter<PlacesResultAdapter.ViewHolder>(), Filterable {
    private var mResultList: ArrayList<AutocompletePrediction>? = arrayListOf()
    private val placesClient: PlacesClient = Places.createClient(mContext)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_places_result_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mResultList!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(position)
        holder.itemView.setOnClickListener {
            onClick(mResultList?.get(position)!!)
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: MaterialTextView = itemView.findViewById(R.id.name)
        private val address: MaterialTextView = itemView.findViewById(R.id.address)
        fun onBind(position: Int) {
            val res = mResultList?.get(position)
            name.text = res?.getPrimaryText(null)
            address.text = res?.getSecondaryText(null)
            address.isVisible = !address.text.isNullOrEmpty()
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence): FilterResults {
                val results = FilterResults()
                mResultList = getPredictions(constraint)
                results.values = mResultList
                results.count = mResultList!!.size
                return results
            }

            override fun publishResults(constraint: CharSequence, results: FilterResults) {

            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getPredictions(constraint: CharSequence): ArrayList<AutocompletePrediction> {
        val result: ArrayList<AutocompletePrediction> = arrayListOf()
        val token = AutocompleteSessionToken.newInstance()
        val request =
            FindAutocompletePredictionsRequest.builder() // Call either setLocationBias() OR setLocationRestriction().
                .setSessionToken(token)
                .setQuery(constraint.toString())
                .build()

        placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener { response: FindAutocompletePredictionsResponse ->
                result.addAll(response.autocompletePredictions)
                onResult.invoke(result.size)
                notifyDataSetChanged()
            }.addOnFailureListener { exception: Exception? ->
                Toast.makeText(
                    mContext,
                    exception?.message ?: " Place not found",
                    Toast.LENGTH_SHORT
                ).show()
                if (exception is ApiException) {
                    Log.e("TAG", "Place not found: " + exception.statusCode)
                }
            }
        return result
    }
}