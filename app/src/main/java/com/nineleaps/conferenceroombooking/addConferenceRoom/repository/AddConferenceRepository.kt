package com.nineleaps.conferenceroombooking.addConferenceRoom.repository

import com.nineleaps.conferenceroombooking.AddConferenceRoom
import com.nineleaps.conferenceroombooking.services.ResponseListener
import com.nineleaps.conferenceroombooking.services.RestClient
import com.nineleaps.conferenceroombooking.utils.Constants
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject


class AddConferenceRepository @Inject constructor() {
    //Passing the Context and model and call API, In return sends the status of LiveData
    fun addConferenceDetails(mConferenceRoom: AddConferenceRoom, token: String, listener: ResponseListener) {
        //Retrofit Call
        val addConferenceRequestCall: Call<ResponseBody> =
            RestClient.getWebServiceData()?.addConference(token, mConferenceRoom)!!

        addConferenceRequestCall.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
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

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if ((response.code() == Constants.OK_RESPONSE) or (response.code() == Constants.SUCCESSFULLY_CREATED)) {
                    listener.onSuccess(response.code())
                } else {
                    listener.onFailure(response.code())
                }
            }
        })
    }

    // ------------------------------------------------update Room Details --------------------------------------
    //Passing the Context and model and call API, In return sends the status of LiveData
    fun updateConferenceDetails(mConferenceRoom: AddConferenceRoom, token: String, listener: ResponseListener) {
        //Retrofit Call
        val addConferenceRequestCall: Call<ResponseBody> =
            RestClient.getWebServiceData()?.updateConference(token, mConferenceRoom)!!
        addConferenceRequestCall.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
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