package com.nineleaps.conferenceroombooking.model

data class RefreshToken(
    var jwtToken: String? = null,
    var refreshToken: String? = null
)