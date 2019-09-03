package com.nineleaps.conferenceroombooking.utils

import java.net.SocketTimeoutException
import java.net.UnknownHostException

class ErrorException {
    companion object {
        fun error(t: Throwable):Any {
            when (t) {
                is SocketTimeoutException -> {
                   return Constants.POOR_INTERNET_CONNECTION
                }
                is UnknownHostException -> {
                    return Constants.POOR_INTERNET_CONNECTION
                }
                else -> {
                    return Constants.INTERNAL_SERVER_ERROR
                }
            }
        }
    }
}