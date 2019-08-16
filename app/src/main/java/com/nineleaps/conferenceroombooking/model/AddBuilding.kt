package com.nineleaps.conferenceroombooking.model

import com.google.gson.annotations.SerializedName

//Model Class Of the AddBuilding
data class AddBuilding(
        @SerializedName("buildingName")
        var buildingName: String? = null,

        @SerializedName("place")
        var place:Int? = null,

        @SerializedName("buildingId")
        var buildingId: Int? = null
)

data class Location(
        @SerializedName("location")
        var locaionName:String? = null,

        @SerializedName("locationId")
        var locationId:Int? = null
)