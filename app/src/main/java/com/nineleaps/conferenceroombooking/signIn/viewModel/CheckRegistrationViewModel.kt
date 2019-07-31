package com.nineleaps.conferenceroombooking.signIn.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nineleaps.conferenceroombooking.model.SignIn
import com.nineleaps.conferenceroombooking.services.ResponseListener
import com.nineleaps.conferenceroombooking.signIn.repository.CheckRegistrationRepository

class CheckRegistrationViewModel : ViewModel() {

    /**
     * a object which will hold the reference to the corrosponding repository class
     */
    var mCheckRegistrationRepository: CheckRegistrationRepository? = null

    /**
     * a MutableLivedata variable which will hold the Value for the Livedata
     */
    var mSuccessCode =  MutableLiveData<SignIn>()
    var mFailureCode =  MutableLiveData<Any>()

    fun setCheckRegistrationRepo(mCheckRegistrationRepo: CheckRegistrationRepository) {
        this.mCheckRegistrationRepository = mCheckRegistrationRepo
    }

    /**
     * function will initialize the repository object and calls the method of repository which will make the api call
     * and function will return the value for MutableLivedata
     */
    fun checkRegistration(token: String, deviceId: String){
        mCheckRegistrationRepository!!.checkRegistration(token, deviceId, object:
            ResponseListener {
            override fun onSuccess(success: Any) {
                mSuccessCode.value = success as SignIn
            }

            override fun onFailure(failure: Any) {
                mFailureCode.value = failure
            }

        })
    }

    fun returnSuccessCode(): MutableLiveData<SignIn> {
        return mSuccessCode
    }
    fun returnFailureCode(): MutableLiveData<Any> {
        return mFailureCode
    }

    //-----------------------------------------------User Registration
}
