package com.nineleaps.conferenceroombooking.signIn.repository


import com.nineleaps.conferenceroombooking.model.SignIn
import com.nineleaps.conferenceroombooking.services.ResponseListener
import com.nineleaps.conferenceroombooking.services.RestClient
import com.nineleaps.conferenceroombooking.utils.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

class CheckRegistrationRepository @Inject constructor(){

    /**
     * function will initialize the MutableLivedata Object and than call a function for api call
     */
    fun checkRegistration(token: String, deviceId: String, listener: ResponseListener)  {
        /**
         * api call using retrofit
         */
        val requestCall: Call<SignIn> = RestClient.getWebServiceData()?.getRequestCode(token, deviceId)!!
        requestCall.enqueue(object : Callback<SignIn> {
            override fun onFailure(call: Call<SignIn>, t: Throwable) {
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
            override fun onResponse(call: Call<SignIn>, response: Response<SignIn>) {
                if ((response.code() == Constants.OK_RESPONSE) or (response.code() == Constants.SUCCESSFULLY_CREATED)) {
                    listener.onSuccess(response.body()!!)
                } else {
                    listener.onFailure(response.code())
                }
            }
        })
    }






}
