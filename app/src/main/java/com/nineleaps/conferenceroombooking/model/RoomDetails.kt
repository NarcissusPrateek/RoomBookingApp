package com.nineleaps.conferenceroombooking.model

import com.google.gson.annotations.SerializedName

data class RoomDetails(
    @SerializedName("roomId")
    var roomId: Int? = null,

    @SerializedName("buildingId")
    var buildingId: Int? = null,


    @SerializedName("capacity")
    var capacity: Int? = null,

    @SerializedName("roomName")
    var roomName: String? = null,

    @SerializedName("status")
    var status: String? = null,

    @SerializedName("buildingName")
    var buildingName: String? = null,

    @SerializedName("place")
    var place: String? = null,

    @SerializedName("amenities")
    var amenities: List<String>? = null,

    @SerializedName("permission")
    var permission: Boolean? = false
)