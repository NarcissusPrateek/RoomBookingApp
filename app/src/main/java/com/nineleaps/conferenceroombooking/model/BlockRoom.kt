package com.nineleaps.conferenceroombooking.model

import com.google.gson.annotations.SerializedName

data class BlockRoom(
    @SerializedName("roomId")
    var cId: Int? = 0,

    @SerializedName("emailId")
    var email: String? = null,

    @SerializedName("startTime")
    var fromTime: String? = null,

    @SerializedName("endTime")
    var toTime: String? = null,

    @SerializedName("buildingId")
    var bId: Int? = 0,

    @SerializedName("status")
    var status: String? = null,

    @SerializedName("purpose")
    var purpose: String? = null
)