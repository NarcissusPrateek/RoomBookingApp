package com.nineleaps.conferenceroombooking

import com.google.gson.annotations.SerializedName

data class Blocked(

    @SerializedName("roomId")
    var roomId : Int? =0,

    @SerializedName("buildingName")
    var buildingName : String? = null,

    @SerializedName("roomName")
    var roomName : String? = null,

    @SerializedName("startTime")
    var fromTime: String? = null,

    @SerializedName("endTime")
    var toTime: String? = null,

    @SerializedName("purpose")
    var purpose: String? = null,

    @SerializedName("meetId")
    var bookingId: Int? = null
)