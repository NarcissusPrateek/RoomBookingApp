package com.nineleaps.conferenceroombooking.splashScreen.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nineleaps.conferenceroombooking.services.ResponseListener
import com.nineleaps.conferenceroombooking.splashScreen.repository.GetRoleOfUser

class GetRoleOfUserViewModel : ViewModel() {

    /**
     * a object which will hold the reference to the corrosponding repository class
     */
    var mCheckUserRole: GetRoleOfUser? = null

    /**
     * a MutableLivedata variable which will hold the Value for the Livedata
     */
    var mSuccessCode =  MutableLiveData<Int>()
    var mFailureCode =  MutableLiveData<Any>()

    fun setGetRoleOfUserRepo(mGetRoleRepo: GetRoleOfUser) {
        this.mCheckUserRole = mGetRoleRepo
    }

    /**
     * function will initialize the repository object and calls the method of repository which will make the api call
     * and function will return the value for MutableLivedata
     */
    fun getUserRole(email: String){
        mCheckUserRole!!.getRole(email, object:
            ResponseListener {
            override fun onSuccess(success: Any) {
                mSuccessCode.value = success as Int
            }

            override fun onFailure(failure: Any) {
                mFailureCode.value = failure
            }

        })
    }

    fun returnSuccessCodeForUserROle(): MutableLiveData<Int> {
        return mSuccessCode
    }
    fun returnFailureCodeForUserRole(): MutableLiveData<Any> {
        return mFailureCode
    }
}
