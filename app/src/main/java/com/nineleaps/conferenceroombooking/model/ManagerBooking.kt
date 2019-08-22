package com.nineleaps.conferenceroombooking.model

import com.google.gson.annotations.SerializedName

data class ManagerBooking(

    @SerializedName("emailId")
    var email: String? = null,

    @SerializedName("roomId")
    var roomId: Int? = 0,

    @SerializedName("buildingId")
    var buildingId: Int? = 0,

    @SerializedName("startTime")
    var fromTime: ArrayList<String>? = null,

    @SerializedName("roomName")
    var roomName: String? = null,

    @SerializedName("endTime")
    var toTime: ArrayList<String>? = null,

    @SerializedName("purpose")
    var purpose: String? = null,

    @SerializedName("capacity")
    var capacity: Int? = 0,

    @SerializedName("attendee")
    var cCMail: List<String>? = null

)