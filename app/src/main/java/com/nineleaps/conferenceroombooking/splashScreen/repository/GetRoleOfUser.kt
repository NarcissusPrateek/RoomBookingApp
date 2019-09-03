package com.nineleaps.conferenceroombooking.splashScreen.repository

import com.nineleaps.conferenceroombooking.services.ResponseListener
import com.nineleaps.conferenceroombooking.services.RestClient
import com.nineleaps.conferenceroombooking.utils.Constants
import com.nineleaps.conferenceroombooking.utils.ErrorException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

class GetRoleOfUser @Inject constructor(){

    /**
     * function will initialize the MutableLivedata Object and than call a function for api call
     */
    fun getRole(email: String, listener: ResponseListener)  {
        /**
         * api call using retrofit
         */
        val requestCall: Call<Int> = RestClient.getWebServiceData()?.getRole(email)!!
        requestCall.enqueue(object : Callback<Int> {
            override fun onFailure(call: Call<Int>, t: Throwable) {
                listener.onFailure(ErrorException.error(t))
            }
            override fun onResponse(call: Call<Int>, response: Response<Int>) {
                if ((response.code() == Constants.OK_RESPONSE) or (response.code() == Constants.SUCCESSFULLY_CREATED)) {
                    listener.onSuccess(response.body()!!)
                } else {
                    listener.onFailure(response.code())
                }
            }
        })
    }






}