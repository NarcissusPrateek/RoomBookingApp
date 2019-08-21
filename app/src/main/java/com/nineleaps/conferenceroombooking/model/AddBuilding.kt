package com.nineleaps.conferenceroombooking.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

//Model Class Of the AddBuilding
data class AddBuilding(
        @SerializedName("buildingName")
        var buildingName: String? = null,

        @SerializedName("placeId")
        var place:Int? = null,

        @SerializedName("buildingId")
        var buildingId: Int? = null
):Serializable

data class Location(
        @SerializedName("location")
        var locaionName:String? = null,

        @SerializedName("locationId")
        var locationId:Int? = null
):Serializable