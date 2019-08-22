package com.nineleaps.conferenceroombooking

import com.google.gson.annotations.SerializedName
import java.io.Serializable

//Model Class Of the AddConference
data class AddConferenceRoom(
    @SerializedName("buildingId")
    var bId: Int? = 0,

    @SerializedName("roomId")
    var roomId: Int? = 0,

    @SerializedName("newRoomName")
    var newRoomName: String? = null,

    @SerializedName("roomName")
    var roomName: String? = null,

    @SerializedName("capacity")
    var capacity: Int? = 0,

    @SerializedName("amenities")
    var amenities:List<Int>? = null,

    @SerializedName("permission")
    var permission: Boolean? = false

) : Serializable

data class GetAllAmenities(
    @SerializedName("amenityId")
    var amenityId: Int? = 0,
    @SerializedName("amenityName")
    var amenityName: String? = null
) : Serializable

