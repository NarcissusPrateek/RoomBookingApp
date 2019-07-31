package com.nineleaps.conferenceroombooking.model

import com.google.gson.annotations.SerializedName

data class RoomDetails(

    @SerializedName("roomName")
    var roomName: String? = null,

    @SerializedName("buildingName")
    var buildingName: String? = null,

    @SerializedName("place")
    var place: String? = null,

    @SerializedName("roomId")
    var roomId: Int? = null,

    @SerializedName("buildingId")
    var buildingId: Int? = null,

    @SerializedName("status")
    var status: String? = null,

    @SerializedName("capacity")
    var capacity: Int? = null,

    @SerializedName("amenities")
    var amenities: List<String>? = null,

    @SerializedName("permission")
    var permission: Int? = 0
)