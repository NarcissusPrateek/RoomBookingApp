package com.nineleaps.conferenceroombooking.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.conferenceroomapp.model.ManagerConference
import com.nineleaps.conferenceroombooking.model.RoomDetails
import com.nineleaps.conferenceroombooking.recurringMeeting.repository.ManagerConferenceRoomRepository
import com.nineleaps.conferenceroombooking.services.ResponseListener

class ManagerConferenceRoomViewModel : ViewModel() {

    /**
     * a object which will hold the reference to the corrosponding repository class
     */
    var mManagerConferenceRoomRepository: ManagerConferenceRoomRepository? = null

    /**
     * a MutableLivedata variable which will hold the Value for the Livedata
     */
    var mConferenceRoomList =  MutableLiveData<List<RoomDetails>>()
    var mFailureCode =  MutableLiveData<Any>()

    var mSuggestedConferenceRoomList = MutableLiveData<List<RoomDetails>>()
    var mSuggestedFailureCode = MutableLiveData<Any>()

    fun setManagerConferenceRoomRepo(mRoomRepo: ManagerConferenceRoomRepository) {
        mManagerConferenceRoomRepository = mRoomRepo
    }
    /**
     * function will initialize the repository object and calls the method of repository which will make the api call
     */
    fun getConferenceRoomList(mRoom: ManagerConference) {
        mManagerConferenceRoomRepository!!.getConferenceRoomList(
            mRoom,
            object : ResponseListener {
                override fun onSuccess(success: Any) {
                    mConferenceRoomList.value = success as List<RoomDetails>
                }

                override fun onFailure(failure: Any) {
                    mFailureCode.value = failure
                }

            }
        )
    }

    /**
     * function will initialize the repository object and calls the method of repository which will make the api call
     * and function will return the value for MutableLivedata
     */
    fun getSuggestedConferenceRoomList(mInputDetailsForRoom: ManagerConference) {
        mManagerConferenceRoomRepository!!.getSuggestedRooms(mInputDetailsForRoom, object:
            ResponseListener {
            override fun onSuccess(success: Any) {
                mSuggestedConferenceRoomList.value = success as List<RoomDetails>
            }

            override fun onFailure(failure: Any) {
                mSuggestedFailureCode.value = failure
            }

        })
    }




    /**
     * function will return the MutableLiveData of List of buildings
     */
    fun returnSuccess(): MutableLiveData<List<RoomDetails>> {
        return mConferenceRoomList
    }

    /**
     * function will return the MutableLiveData of Int if something went wrong at server
     */
    fun returnFailure(): MutableLiveData<Any> {
        return mFailureCode
    }

    /**
     * function will return the MutableLiveData of List of buildings
     */
    fun returnSuccessForSuggested(): MutableLiveData<List<RoomDetails>> {
        return mSuggestedConferenceRoomList
    }

    /**
     * function will return the MutableLiveData of Int if something went wrong at server
     */
    fun returnFailureForSuggestedRooms(): MutableLiveData<Any> {
        return mSuggestedFailureCode
    }


}
