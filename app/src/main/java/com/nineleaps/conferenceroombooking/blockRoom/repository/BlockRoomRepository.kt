package com.nineleaps.conferenceroombooking.blockRoom.repository

import com.nineleaps.conferenceroombooking.Models.ConferenceList
import com.nineleaps.conferenceroombooking.model.BlockRoom
import com.nineleaps.conferenceroombooking.model.BlockingConfirmation
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

class BlockRoomRepository @Inject constructor(){
    /**
     *  function will make API call
     */
    fun blockRoom(mRoom: BlockRoom, token: String, listener: ResponseListener) {

        /**
         * make API call usnig retrofit
         */
        val requestCall: Call<ResponseBody> = RestClient.getWebServiceData()?.blockconference(token, mRoom)!!
        requestCall.enqueue(object : Callback<ResponseBody> {
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
     * ---------------------------------------------------------------------------------------------------------------------------
     */

    /**
     * function will initialize the MutableLivedata Object and than make API Call
     * Passing the Context and model and call API, In return sends the status of LiveData
     */
    fun getRoomList(buildingId: Int, token: String, listener: ResponseListener) {

        /**
         *  api call using retrofit
         */
        val requestCall: Call<List<ConferenceList>> = RestClient.getWebServiceData()?.conferenceList(token, buildingId)!!
        requestCall.enqueue(object : Callback<List<ConferenceList>> {
            override fun onFailure(call: Call<List<ConferenceList>>, t: Throwable) {
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

            override fun onResponse(
                call: Call<List<ConferenceList>>,
                response: Response<List<ConferenceList>>
            ) {
                if (response.code() == Constants.OK_RESPONSE) {
                    listener.onSuccess(response.body()!!)
                } else {
                    listener.onFailure(response.code())
                }
            }
        })
    }

    /**
     * ---------------------------------------------------------------------------------------------------------------------------
     */

    fun blockingStatus(mRoom: BlockRoom, token: String, listener: ResponseListener) {
        /**
         * API call using retrofit
         */
        val requestCall: Call<BlockingConfirmation> = RestClient.getWebServiceData()?.blockConfirmation(token, mRoom)!!
        requestCall.enqueue(object : Callback<BlockingConfirmation> {
            override fun onFailure(call: Call<BlockingConfirmation>, t: Throwable) {
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

            override fun onResponse(call: Call<BlockingConfirmation>, response: Response<BlockingConfirmation>) {
                if (response.code() == Constants.OK_RESPONSE || response.code() == Constants.NO_CONTENT_FOUND) {
                    if (response.code() == Constants.NO_CONTENT_FOUND) {
                        val blockingConfirmation = BlockingConfirmation()
                        blockingConfirmation.mStatus = 0
                        listener.onSuccess(blockingConfirmation)
                    } else {
                        val blockingConfirmation = response.body()
                        blockingConfirmation!!.mStatus = 1
                        listener.onSuccess(blockingConfirmation)
                    }
                }else {
                    listener.onFailure(response.code())
                }
            }
        })
    }
}