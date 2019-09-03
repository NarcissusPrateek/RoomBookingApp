package com.nineleaps.conferenceroombooking.bookingDashboard.repository

import com.nineleaps.conferenceroombooking.model.BookingDashboardInput
import com.nineleaps.conferenceroombooking.model.DashboardDetails
import com.nineleaps.conferenceroombooking.services.ResponseListener
import com.nineleaps.conferenceroombooking.services.RestClient
import com.nineleaps.conferenceroombooking.utils.Constants
import com.nineleaps.conferenceroombooking.utils.ErrorException
import com.nineleaps.conferenceroombooking.utils.GetCurrentTimeInUTC
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

class BookingDashboardRepository @Inject constructor() {
    /**
     * function will make api call for making a booking
     * and call the interface method with data from server
     */
    fun getBookingList(mBookingDashboardInput: BookingDashboardInput, listener: ResponseListener) {
        /**
         * API call using retrofit
         */
        mBookingDashboardInput.currentDatTime = GetCurrentTimeInUTC.getCurrentTimeInUTC()
        val requestCall: Call<DashboardDetails> =
            RestClient.getWebServiceData()?.getDashboard(mBookingDashboardInput)!!
        requestCall.enqueue(object : Callback<DashboardDetails> {
            override fun onFailure(call: Call<DashboardDetails>, t: Throwable) {
                listener.onFailure(ErrorException.error(t))
            }

            override fun onResponse(call: Call<DashboardDetails>, response: Response<DashboardDetails>) {
                if ((response.code() == Constants.OK_RESPONSE) or (response.code() == Constants.SUCCESSFULLY_CREATED)) {
                    listener.onSuccess(response.body()!!)
                } else {
                    listener.onFailure(response.code())
                }
            }
        })
    }

    /**
     * function will make the API Call and call the interface method with data from server
     */
    fun cancelBooking(meetingId: Int, listener: ResponseListener) {
        /**
         * api call using retrofit
         */
        val requestCall: Call<ResponseBody> = RestClient.getWebServiceData()?.cancelBookedRoom(meetingId)!!
        requestCall.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                listener.onFailure(ErrorException.error(t))
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if ((response.code() == Constants.OK_RESPONSE) or (response.code() == Constants.SUCCESSFULLY_CREATED)) {
                    listener.onSuccess(response.code())
                } else {
                    listener.onFailure(response.code())
                }
            }
        })
    }

    /**
     * function will make the API Call and call the interface method with data from srver
     */
    fun recurringCancelBooking(meetId: Int, recurringMeetingId: String, listener: ResponseListener) {
        /**
         * api call using rerofit
         */
        val requestCall: Call<ResponseBody> =
            RestClient.getWebServiceData()?.cancelRecurringBooking(meetId, recurringMeetingId)!!
        requestCall.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                listener.onFailure(ErrorException.error(t))
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if ((response.code() == Constants.OK_RESPONSE) or (response.code() == Constants.SUCCESSFULLY_CREATED)) {
                    listener.onSuccess(response.code())
                } else {
                    listener.onFailure(response.code())
                }
            }
        })
    }

    fun getPasscode(generateNewPasscode: Boolean, emailId: String, listener: ResponseListener) {
        val requestCall: Call<String> =
            RestClient.getWebServiceData()?.getPasscode(generateNewPasscode, emailId)!!
        requestCall.enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                listener.onFailure(Constants.INVALID_TOKEN)
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                if ((response.code() == Constants.OK_RESPONSE) or (response.code() == Constants.SUCCESSFULLY_CREATED)) {
                    listener.onSuccess(response.body()!!)
                } else {
                    listener.onFailure(response.code())
                }
            }

        })
    }
}