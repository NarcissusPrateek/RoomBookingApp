package com.nineleaps.conferenceroombooking.model

import com.google.gson.annotations.SerializedName

data class Dashboard (
    @SerializedName("roomId")
    var roomId : Int? = null,

    @SerializedName("emailId")
    var email: String? = null,

    @SerializedName("startTime")
    var fromTime : String? = null,

    @SerializedName("endTime")
    var toTime : String? = null,

    @SerializedName("buildingName")
    var buildingName : String? = null,

    @SerializedName("roomName")
    var roomName : String? = null,

    @SerializedName("purpose")
    var purpose: String? = null,

    @SerializedName("status")
    var status: String? = null,

    @SerializedName("meetId")
    var bookingId: Int? = null,

    @SerializedName("nameOfAttendees")
    var name: List<String>? = null,

    @SerializedName("attendeesMail")
    var cCMail: List<String>? = null,

    @SerializedName("amenities")
    var amenities: List<String>? = null,

    @SerializedName("nameOfOrganizer")
    var organizer: String? = null,

    @SerializedName("isTagged")
    var isTagged: Boolean? = false,

    @SerializedName("recurringMeetId")
    var recurringmeetingId:String?= null


)

data class PaginationMetaData(
    @SerializedName("totalCount")
    var pageCount: Int? = null,

    @SerializedName("pageSize")
    var pageSize: Int? = null,

    @SerializedName("currentPage")
    var currentPage: Int? = null,

    @SerializedName("totalPages")
    var totalPages: Int? = null,

    @SerializedName("previousPage")
    var previousPage: Boolean? = null,

    @SerializedName("nextPage")
    var nextPage: Boolean? = null
)

data class DashboardDetails(
    @SerializedName("userDashboard")
    var dashboard: List<Dashboard>? = null,

    @SerializedName("pager")
    var paginationMetaData: PaginationMetaData? = null
)

