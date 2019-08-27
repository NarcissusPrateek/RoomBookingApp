package com.nineleaps.conferenceroombooking.utils

import android.content.Context
import android.widget.Toast
import com.nineleaps.conferenceroombooking.R
import es.dmoral.toasty.Toasty

/**
 * show different toast for different kind of error and response messages
 */
class ShowToast {
    companion object {
        fun show(mContext: Context, errorCode: Int) {
            Toasty.info(
                mContext,
                showMessageAccordingToCode(mContext, errorCode), Toast.LENGTH_SHORT, true
            ).show()
        }

        fun showMessageAccordingToCode(mContext: Context, errorCode: Int): String {

            val statusCodeMap = mutableMapOf(
                Constants.NOT_ACCEPTABLE to mContext.getString(R.string.parameter_missing),
                Constants.NOT_MODIFIED to mContext.getString(R.string.not_modified_message),
                Constants.NO_CONTENT_FOUND to mContext.getString(R.string.no_booking_available),
                Constants.NOT_FOUND to mContext.getString(R.string.not_found),
                Constants.INTERNAL_SERVER_ERROR to mContext.getString(R.string.internal_server_error),
                Constants.UNAVAILABLE_SLOT to mContext.getString(R.string.slot_unavailable),
                Constants.POOR_INTERNET_CONNECTION to mContext.getString(R.string.poor_internet_connection),
                Constants.UNPROCESSABLE to "Invalid Start Time and End Time"
            )
            if (statusCodeMap[errorCode]==null)
                return mContext.getString(R.string.internal_server_error)
            return statusCodeMap[errorCode]!!
        }
    }

}

/*
var message = mContext.getString(R.string.something_went_wrong)
            when (errorCode) {
                Constants.NOT_ACCEPTABLE -> {
                    message = mContext.getString(R.string.parameter_missing)
                }
                Constants.NOT_MODIFIED -> {
                    message = mContext.getString(R.string.not_modified_message)
                }
                Constants.NO_CONTENT_FOUND -> {
                    message = mContext.getString(R.string.no_booking_available)
                }
                Constants.NOT_FOUND -> {
                    message = mContext.getString(R.string.not_found)
                }
                Constants.INTERNAL_SERVER_ERROR -> {
                    message = mContext.getString(R.string.internal_server_error)
                }
                Constants.UNAVAILABLE_SLOT -> {
                    message = mContext.getString(R.string.slot_unavailable)
                }
                Constants.POOR_INTERNET_CONNECTION -> {
                    message = mContext.getString(R.string.poor_internet_connection)
                }
            }
        */