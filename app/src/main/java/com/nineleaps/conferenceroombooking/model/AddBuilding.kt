package com.nineleaps.conferenceroombooking.model

import com.google.gson.annotations.SerializedName

//Model Class Of the AddBuilding
data class AddBuilding(
        @SerializedName("buildingName")
        var buildingName: String? = null,

        @SerializedName("place")
        var place:String? = null,

        @SerializedName("buildingId")
        var buildingId: Int? = null
)