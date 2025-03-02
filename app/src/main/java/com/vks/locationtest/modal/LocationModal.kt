package com.vks.locationtest.modal

class LocationModal(
    val id: Int = 0,
    val placeId: String,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val distance: Float = 0f
)