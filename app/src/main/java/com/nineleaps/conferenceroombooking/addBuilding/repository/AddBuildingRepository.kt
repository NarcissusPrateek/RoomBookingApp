package com.nineleaps.conferenceroombooking.addBuilding.repository

import com.nineleaps.conferenceroombooking.model.AddBuilding
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


class AddBuildingRepository @Inject constructor(){

    /**
     * make API call and calls the methods of interface
     */
    fun addBuildingDetails(mAddBuilding: AddBuilding, listener: ResponseListener) {
        val addBuildingRequestCall: Call<ResponseBody> = RestClient.getWebServiceData()?.addBuilding(mAddBuilding)!!
        addBuildingRequestCall.enqueue(object : Callback<ResponseBody> {
            // Negative response
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                when(t) {
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
            //positive response
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if ((response.code() == Constants.OK_RESPONSE) or (response.code() == Constants.SUCCESSFULLY_CREATED)) {
                    listener.onSuccess(response.code())
                } else {
                    listener.onFailure(response.code())
                }
            }
        })
    }
    //--------------------------------------------api call for update building details ----------------------------

    /**
     * make API call and calls the methods of interface
     */
    fun updateBuildingDetails(mAddBuilding: AddBuilding, listener: ResponseListener) {
        val addBuildingRequestCall: Call<ResponseBody> = RestClient.getWebServiceData()?.updateBuilding(mAddBuilding)!!
        addBuildingRequestCall.enqueue(object : Callback<ResponseBody> {
            // Negative response
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                when(t) {
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
            //positive response
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
