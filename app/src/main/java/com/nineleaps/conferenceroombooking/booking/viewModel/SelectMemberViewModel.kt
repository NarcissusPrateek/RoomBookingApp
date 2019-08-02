package com.nineleaps.conferenceroombooking.booking.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nineleaps.conferenceroombooking.booking.repository.EmployeeRepository
import com.nineleaps.conferenceroombooking.model.EmployeeList
import com.nineleaps.conferenceroombooking.services.ResponseListener

class SelectMemberViewModel: ViewModel() {
    /**
     * a object which will hold the reference to the corrosponding repository class
     */
    private var mEmployeeRepository: EmployeeRepository? = null

    /**
     * a MutableLivedata variable which will hold the response from server
     */
    var mEmployeeList =  MutableLiveData<List<EmployeeList>>()

    var mErrorCodeFromServerForEmployees =  MutableLiveData<Any>()

    fun setEmployeeListRepo(mEmployeeListrepo: EmployeeRepository) {
        this.mEmployeeRepository = mEmployeeListrepo
    }

    /**
     * for Employee List
     */
    fun getEmployeeList(email: String) {
        mEmployeeRepository!!.getEmployeeList(email, object: ResponseListener {
            override fun onFailure(failure: Any) {
                mErrorCodeFromServerForEmployees.value = failure
            }
            override fun onSuccess(success: Any) {
                mEmployeeList.value = success as List<EmployeeList>
            }

        })

    }
    fun returnSuccessForEmployeeList(): MutableLiveData<List<EmployeeList>> {
        return mEmployeeList
    }

    /**
     * function will return the MutableLiveData of Int if something went wrong at server
     */
    fun returnFailureForEmployeeList(): MutableLiveData<Any> {
        return mErrorCodeFromServerForEmployees
    }

}