package com.nineleaps.conferenceroombooking.blockDashboard.repository

import com.nineleaps.conferenceroombooking.Blocked
import com.nineleaps.conferenceroombooking.services.ResponseListener
import com.nineleaps.conferenceroombooking.services.RestClient
import com.nineleaps.conferenceroombooking.utils.Constants
import com.nineleaps.conferenceroombooking.utils.ErrorException
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class BlockDashboardRepository @Inject constructor(){
    /**
     * Passing the Context and model and call API, In return sends the status of LiveData
     */
    fun getBlockedList(listener: ResponseListener) {
        val requestCall: Call<List<Blocked>> = RestClient.getWebServiceData()?.getBlockedConference()!!
        requestCall.enqueue(object : Callback<List<Blocked>> {
            override fun onFailure(call: Call<List<Blocked>>, t: Throwable) {
                listener.onFailure(ErrorException.error(t))
            }
            override fun onResponse(call: Call<List<Blocked>>, response: Response<List<Blocked>>) {
                if ((response.code() == Constants.OK_RESPONSE) or (response.code() == Constants.SUCCESSFULLY_CREATED)) {
                    listener.onSuccess(response.body()!!)
                } else {
                    listener.onFailure(response.code())
                }
            }

        })
    }

    /**
     * make request to server for unblock room
     */
    fun unblockRoom(bookingId: Int, listener: ResponseListener) {
        val requestCall: Call<ResponseBody> = RestClient.getWebServiceData()?.unBlockingConferenceRoom(bookingId)!!
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
}