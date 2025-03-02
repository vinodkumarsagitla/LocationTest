package com.vks.locationtest.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.vks.locationtest.database.LocationEntity
import com.vks.locationtest.databinding.LayoutLocationItemBinding
import com.vks.locationtest.modal.LocationModal

internal typealias OnDeleteClicked = (LocationEntity) -> Unit
internal typealias OnEditClicked = (LocationEntity) -> Unit

class LocationAdapter : RecyclerView.Adapter<LocationAdapter.ItemViewHolder>() {
    var list: List<LocationModal> = emptyList()
    var onDeleteClicked: OnDeleteClicked? = null
    var onEditClicked: OnEditClicked? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            LayoutLocationItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(list[position], onDeleteClicked, onEditClicked)
        val params = holder.itemView.layoutParams as MarginLayoutParams
        params.topMargin = if (position == 0) holder.itemView.dip(12)
        else holder.itemView.dip(6)
        params.bottomMargin = if (position == list.size - 1) holder.itemView.dip(60)
        else holder.itemView.dip(6)
        holder.itemView.layoutParams = params
    }

    class ItemViewHolder(private val binding: LayoutLocationItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("DefaultLocale")
        fun bind(
            item: LocationModal,
            onDeleteClicked: OnDeleteClicked?,
            onEditClicked: OnEditClicked?
        ) {
            binding.name.text = item.name
            binding.address.text = item.address
            binding.badge.isVisible = item.distance == 0f
            binding.distance.isVisible = item.distance != 0f
            binding.distance.text = String.format("Distance: %.2f KM", item.distance)
            binding.delete.setOnClickListener {
                onDeleteClicked?.invoke(item.toEntity())
            }
            binding.edit.setOnClickListener {
                onEditClicked?.invoke(item.toEntity())
            }
        }
    }
}

fun LocationModal.toEntity() = LocationEntity(id, placeId, name, address, latitude, longitude)

fun View.dip(value: Int): Int = context.dip(value)

fun Context.dip(value: Int): Int = (value * resources.displayMetrics.density).toInt()


