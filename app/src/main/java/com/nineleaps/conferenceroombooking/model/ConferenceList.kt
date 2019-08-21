package com.nineleaps.conferenceroombooking.Models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ConferenceList(

        @SerializedName("roomName")
        var roomName : String? = null,

        @SerializedName("capacity")
        var capacity : Int? = 0,

        @SerializedName("buildingName")
        var buildingName : String? = null,

        @SerializedName("roomId")
        var roomId: Int? = null,

        @SerializedName("buildingId")
        var buildingId: Int? = null,

        @SerializedName("amenities")
        var amenities : HashMap<Int,String>? = null,

        @SerializedName("place")
        var place : String? = null,

        @SerializedName("permission")
        var permission: Boolean? = null,

        @SerializedName("status")
        var mStatus:String? = null
) :Serializable