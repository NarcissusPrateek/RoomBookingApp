package com.example.conferenceroomapp.model

import com.google.gson.annotations.SerializedName

data class InputDetailsForRoom (
    @SerializedName("startTime")
    var fromTime : String? = null,

    @SerializedName("endTime")
    var toTime : String? = null,

    @SerializedName("capacity")
    var capacity : Int? = 0,

    @SerializedName("emailId")
    var email : String? = null
)