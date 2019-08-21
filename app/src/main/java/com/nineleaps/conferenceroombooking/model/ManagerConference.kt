package com.example.conferenceroomapp.model

import com.google.gson.annotations.SerializedName

data class ManagerConference(
    @SerializedName("startTime")
    var fromTime: ArrayList<String>?= null,

    @SerializedName("endTime")
    var toTime: ArrayList<String>? = null,

    @SerializedName("capacity")
    var capacity: Int? =0
)
