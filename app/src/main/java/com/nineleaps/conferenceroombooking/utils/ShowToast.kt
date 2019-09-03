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
                Constants.UNPROCESSABLE to mContext.getString(R.string.invalid_starttime_and_endtime),
                Constants.BUILDING_PRESENT to mContext.getString(R.string.building_present)
            )
            if (statusCodeMap[errorCode]==null)
                return mContext.getString(R.string.internal_server_error)
            return statusCodeMap[errorCode]!!
        }
    }

}

