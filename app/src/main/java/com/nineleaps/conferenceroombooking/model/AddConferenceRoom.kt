package com.nineleaps.conferenceroombooking

import com.google.gson.annotations.SerializedName
import java.io.Serializable

//Model Class Of the AddConference
data class AddConferenceRoom(
   @SerializedName("buildingId")
   var  bId:Int?= 0,

   @SerializedName("roomId")
   var roomId: Int? = 0,

   @SerializedName("newRoomName")
   var newRoomName: String? = null,

   @SerializedName("roomName")
   var roomName :String?=null,

   @SerializedName("capacity")
   var capacity :Int? = 0,

   @SerializedName("projector")
   var projector: Boolean? = null,

   @SerializedName("monitor")
   var monitor: Boolean? = null,

   @SerializedName("speaker")
   var speaker: Boolean? = null,

   @SerializedName("extensionBoard")
   var extensionBoard: Boolean? = null,

   @SerializedName("whiteBoardMarker")
   var whiteBoardMarker: Boolean? = null,

   @SerializedName("permission")
   var permission: Boolean? = false

): Serializable