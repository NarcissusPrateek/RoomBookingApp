package com.nineleaps.conferenceroombooking.addBuilding.repository

import com.nineleaps.conferenceroombooking.model.AddBuilding
import com.nineleaps.conferenceroombooking.model.Location
import com.nineleaps.conferenceroombooking.services.ResponseListener
import com.nineleaps.conferenceroombooking.services.RestClient
import com.nineleaps.conferenceroombooking.utils.Constants
import com.nineleaps.conferenceroombooking.utils.ErrorException
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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
                listener.onFailure(ErrorException.error(t))
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
                listener.onFailure(ErrorException.error(t))
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

    //--------------------------------------------api call for get Location details ----------------------------
    /**
     * make API call and calls the methods of interface
     */
    fun getLocationDetails(listener: ResponseListener){
        val getLocationRequestCall:Call<List<Location>> = RestClient.getWebServiceData()?.getAllLocation()!!
        getLocationRequestCall.enqueue(object :Callback<List<Location>>{
            override fun onFailure(call: Call<List<Location>>, t: Throwable) {
                listener.onFailure(ErrorException.error(t))
            }

            override fun onResponse(call: Call<List<Location>>, response: Response<List<Location>>) {
                if ((response.code() == Constants.OK_RESPONSE) or (response.code() == Constants.NO_CONTENT_FOUND)){
                    if (response.body()!!.isEmpty()){
                        listener.onSuccess(ArrayList<Location>())
                    }
                    listener.onSuccess(response.body()!!)
                }
                else{
                    listener.onFailure(response.code())
                }
            }

        })
    }


}
