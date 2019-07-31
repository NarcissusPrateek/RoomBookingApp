package com.nineleaps.conferenceroombooking.model

data class RefreshToken(
    var accessToken: String? = null,
    var refreshToken: String? = null
)