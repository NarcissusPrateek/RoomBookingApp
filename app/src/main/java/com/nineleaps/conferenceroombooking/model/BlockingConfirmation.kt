package com.nineleaps.conferenceroombooking.model

import com.google.gson.annotations.SerializedName

data class BlockingConfirmation (
    @SerializedName("name")
    var name: String? = null,
    @SerializedName("purpose")
    var purpose: String? = null,
    var mStatus: Int? = 0
)