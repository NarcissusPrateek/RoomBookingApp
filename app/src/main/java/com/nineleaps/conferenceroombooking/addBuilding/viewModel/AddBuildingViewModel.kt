package com.nineleaps.conferenceroombooking.addBuilding.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nineleaps.conferenceroombooking.addBuilding.repository.AddBuildingRepository
import com.nineleaps.conferenceroombooking.model.AddBuilding
import com.nineleaps.conferenceroombooking.services.ResponseListener

open class AddBuildingViewModel : ViewModel() {

    /**
     * a object which will hold the reference to the corrosponding repository class
     */
    var mAddBuildingRepository: AddBuildingRepository? = null

    /**
     * a MutableLivedata variable which will hold the response from server
     */
    var mSuccessForAddBuilding = MutableLiveData<Int>()
    var mFailureForAddBuilding = MutableLiveData<Any>()

    var mSuccessForUpdateBuilding = MutableLiveData<Int>()
    var mFailureForUpdateBuilding = MutableLiveData<Any>()

    /**
     * initialize repo object
     */
    fun setBuildingRepository(mAddBuildingRepository: AddBuildingRepository) {
        this.mAddBuildingRepository = mAddBuildingRepository
    }

    /**
     * function will initialize the repository object and calls the method of repository which will make the api call
     * and assign values to the live data objects
     */
    fun addBuildingDetails(mAddBuilding: AddBuilding) {
        mAddBuildingRepository!!.addBuildingDetails(mAddBuilding, object :
            ResponseListener {
            override fun onFailure(failure: Any) {
                mFailureForAddBuilding.value = failure
            }

            override fun onSuccess(success: Any) {
                mSuccessForAddBuilding.value = success as Int
            }
        })
    }

    /**
     * return positive response from server
     */
    fun returnSuccessForAddBuilding(): MutableLiveData<Int> {
        return mSuccessForAddBuilding
    }

    /**
     * return negative response from server
     */
    fun returnFailureForAddBuilding(): MutableLiveData<Any> {
        return mFailureForAddBuilding
    }


    //-----------------------------------------update building details ----------------------------------------------------
    /**
     * function will initialize the repository object and calls the method of repository which will make the api call
     * and assign values to the live data objects
     */
    fun updateBuildingDetails(mAddBuilding: AddBuilding) {
        mAddBuildingRepository!!.updateBuildingDetails(mAddBuilding, object :
            ResponseListener {
            override fun onFailure(failure: Any) {
                mFailureForUpdateBuilding.value = failure
            }

            override fun onSuccess(success: Any) {
                mSuccessForUpdateBuilding.value = success as Int
            }
        })
    }

    /**
     * return positive response from server
     */
    fun returnSuccessForUpdateBuilding(): MutableLiveData<Int> {
        return mSuccessForUpdateBuilding
    }

    /**
     * return negative response from server
     */
    fun returnFailureForUpdateBuilding(): MutableLiveData<Any> {
        return mFailureForUpdateBuilding
    }

}
