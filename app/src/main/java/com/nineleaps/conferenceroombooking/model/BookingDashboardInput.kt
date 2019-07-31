package com.nineleaps.conferenceroombooking.model

import com.google.gson.annotations.SerializedName

data class BookingDashboardInput (
    @SerializedName("emailId")
    var email: String? = null,

    @SerializedName("status")
    var status: String? = null,

    @SerializedName("pageSize")
    var pageSize: Int? = null,

    @SerializedName("pageNumber")
    var pageNumber: Int? = null,

    @SerializedName("currentDateTime")
    var currentDatTime: String? = null


    )