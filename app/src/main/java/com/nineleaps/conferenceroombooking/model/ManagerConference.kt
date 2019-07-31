package com.example.conferenceroomapp.model

import com.google.gson.annotations.SerializedName

class ManagerConference {
    @SerializedName("startTime")
    var fromTime = ArrayList<String>()

    @SerializedName("endTime")
    var toTime = ArrayList<String>()

    @SerializedName("buildingId")
    var buildingId : Int? = 0

    @SerializedName("capacity")
    var capacity: Int? = 0
}