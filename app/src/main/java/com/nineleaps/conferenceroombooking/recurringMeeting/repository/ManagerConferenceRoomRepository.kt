package com.nineleaps.conferenceroombooking.recurringMeeting.repository

import android.util.Log
import com.example.conferenceroomapp.model.ManagerConference
import com.nineleaps.conferenceroombooking.model.RoomDetails
import com.nineleaps.conferenceroombooking.services.ResponseListener
import com.nineleaps.conferenceroombooking.services.RestClient
import com.nineleaps.conferenceroombooking.utils.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject
import android.R.string
import org.json.JSONObject
import java.lang.Exception


class ManagerConferenceRoomRepository @Inject constructor() {
    /**
     * function will initialize the MutableLivedata Object and than call a function for api call
     * Passing the Context and model and call API, In return sends the status of LiveData
     */
    fun getConferenceRoomListForRecurringMeeting(mRoom: ManagerConference, listener: ResponseListener) {
        /**
         * api call using retrofit
         */
        val requestCall: Call<List<RoomDetails>> =
            RestClient.getWebServiceData()?.getConferenceRoomListForRecurring(mRoom)!!
        requestCall.enqueue(object : Callback<List<RoomDetails>> {
            override fun onFailure(call: Call<List<RoomDetails>>, t: Throwable) {
                when (t) {
                    is SocketTimeoutException -> {
                        listener.onFailure(Constants.POOR_INTERNET_CONNECTION)
                    }
                    is UnknownHostException -> {
                        listener.onFailure(Constants.POOR_INTERNET_CONNECTION)
                    }
                    else -> {
                        listener.onFailure(Constants.INTERNAL_SERVER_ERROR)
                    }
                }
            }

            override fun onResponse(call: Call<List<RoomDetails>>, response: Response<List<RoomDetails>>) {
                if ((response.code() == Constants.OK_RESPONSE) or (response.code() == Constants.SUCCESSFULLY_CREATED)) {
                    listener.onSuccess(response.body()!!)
                } else
                    listener.onFailure(response.code())
            }
        })
    }
}


