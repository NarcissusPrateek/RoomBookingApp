package com.nineleaps.conferenceroombooking.addConferenceRoom.repository

import com.nineleaps.conferenceroombooking.AddConferenceRoom
import com.nineleaps.conferenceroombooking.GetAllAmenities
import com.nineleaps.conferenceroombooking.services.ResponseListener
import com.nineleaps.conferenceroombooking.services.RestClient
import com.nineleaps.conferenceroombooking.utils.Constants
import com.nineleaps.conferenceroombooking.utils.ErrorException
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject


class AddConferenceRepository @Inject constructor() {
    //Passing the Context and model and call API, In return sends the status of LiveData
    fun addConferenceDetails(mConferenceRoom: AddConferenceRoom, listener: ResponseListener) {
        //Retrofit Call
        val addConferenceRequestCall: Call<ResponseBody> =
            RestClient.getWebServiceData()?.addConference(mConferenceRoom)!!

        addConferenceRequestCall.enqueue(object : Callback<ResponseBody> {
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

    // ------------------------------------------------update Room Details --------------------------------------

    //Passing the Context and model and call API, In return sends the status of LiveData
    fun updateConferenceDetails(mConferenceRoom: AddConferenceRoom, listener: ResponseListener) {
        //Retrofit Call
        val addConferenceRequestCall: Call<ResponseBody> =
            RestClient.getWebServiceData()?.updateConference(mConferenceRoom)!!
        addConferenceRequestCall.enqueue(object : Callback<ResponseBody> {
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


    // ------------------------------------------------Get Amenities Details --------------------------------------

    //Passing the Context and model and call API, In return sends the status of LiveData
    fun getAmenitiesDetails(listener: ResponseListener) {
        val getAmenitiesRequestCall: Call<List<GetAllAmenities>> = RestClient.getWebServiceData()?.getAllAmenities()!!
        getAmenitiesRequestCall.enqueue(object :Callback<List<GetAllAmenities>>{
            override fun onFailure(call: Call<List<GetAllAmenities>>, t: Throwable) {
                listener.onFailure(ErrorException.error(t))
            }

            override fun onResponse(call: Call<List<GetAllAmenities>>, response: Response<List<GetAllAmenities>>) {
                if ((response.code() == Constants.OK_RESPONSE) or (response.code() == Constants.NO_CONTENT_FOUND)){
                    listener.onSuccess(response.body()!!)
                }
                else{
                    listener.onFailure(response.code())
                }
            }

        })
    }
}