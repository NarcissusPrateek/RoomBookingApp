package com.nineleaps.conferenceroombooking.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nineleaps.conferenceroombooking.model.ManagerBooking
import com.nineleaps.conferenceroombooking.recurringMeeting.repository.ManagerBookingRepository
import com.nineleaps.conferenceroombooking.services.ResponseListener

class ManagerBookingViewModel: ViewModel() {

    /**
     * a object which will hold the reference to the corrosponding repository class
     */
    private var mManagerBookingRepository: ManagerBookingRepository? = null

    /**
     * a MutableLivedata variable which will hold the Value for the Livedata
     */
    var mSuccessCode =  MutableLiveData<Int>()

    var mErrorCode =  MutableLiveData<Any>()

    fun setManagerBookingRepo(mManagerBookingRepo: ManagerBookingRepository) {
        mManagerBookingRepository = mManagerBookingRepo
    }
   /**
     * function will initialize the repository object and calls the method of repository which will make the api call
     * and function will return the value for MutableLivedata
     */
    fun addBookingDetails(mBooking: ManagerBooking, token: String) {
       mManagerBookingRepository!!.addBookingDetails(mBooking, token, object:
           ResponseListener {
            override fun onSuccess(success: Any) {
                mSuccessCode.value = success as Int
            }

            override fun onFailure(failure: Any) {
                mErrorCode.value = failure
            }

        })
    }

    /**
     * function will return the MutableLiveData of List of buildings
     */
    fun returnSuccessForBooking(): MutableLiveData<Int> {
        return mSuccessCode
    }

    /**
     * function will return the MutableLiveData of Int if something went wrong at server
     */
    fun returnFailureForBooking(): MutableLiveData<Any> {
        return mErrorCode
    }
}