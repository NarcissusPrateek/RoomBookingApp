package com.nineleaps.conferenceroombooking.booking.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.conferenceroomapp.model.InputDetailsForRoom
import com.nineleaps.conferenceroombooking.booking.repository.ConferenceRoomRepository
import com.nineleaps.conferenceroombooking.model.RoomDetails
import com.nineleaps.conferenceroombooking.services.ResponseListener

class ConferenceRoomViewModel : ViewModel() {

    /**
     * a object which will hold the reference to the corresponding repository class
     */
    private var mConferenceRoomRepository: ConferenceRoomRepository? = null

    /**
     * A MutableLiveData variable which will hold the Value for negative response from repository
     */
    private var errorCodeFromServer =  MutableLiveData<Any>()

    /**
     * a MutableLiveData variable which will hold the positive response for repository
     */
    var mConferenceRoomList =  MutableLiveData<List<RoomDetails>>()

    /**
     * A MutableLiveData variable which will hold the Value for negative response from repository
     */
//    private var errorCodeFromServerForSuggestedRooms =  MutableLiveData<Any>()

    /**
     * a MutableLiveData variable which will hold the positive response for repository
     */
//    var mSuggestedConferenceRoomList =  MutableLiveData<List<RoomDetails>>()

    fun setConferenceRoomRepo(mRoomRepo: ConferenceRoomRepository) {
        mConferenceRoomRepository = mRoomRepo
    }



    /**
     * function will initialize the repository object and calls the method of repository which will make the api call
     * and function will return the value for MutableLivedata
     */
    fun getConferenceRoomList(mInputDetailsForRoom: InputDetailsForRoom) {
        mConferenceRoomRepository!!.getConferenceRoomList(mInputDetailsForRoom, object:
            ResponseListener {
            override fun onSuccess(success: Any) {
                mConferenceRoomList.value = success as List<RoomDetails>
            }

            override fun onFailure(failure: Any) {
                errorCodeFromServer.value = failure
            }

        })
    }

    /**
     * function will initialize the repository object and calls the method of repository which will make the api call
     * and function will return the value for MutableLivedata
//     */
//    fun getSuggestedConferenceRoomList(mInputDetailsForRoom: InputDetailsForRoom) {
//        mConferenceRoomRepository!!.getSuggestedRooms(mInputDetailsForRoom, object:
//            ResponseListener {
//            override fun onSuccess(success: Any) {
//                mSuggestedConferenceRoomList.value = success as List<RoomDetails>
//            }
//
//            override fun onFailure(failure: Any) {
//                errorCodeFromServerForSuggestedRooms.value = failure
//            }
//
//        })
//    }


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
        return errorCodeFromServer
    }

    /**
     * function will return the MutableLiveData of List of buildings
     */
//    fun returnSuccessForSuggested(): MutableLiveData<List<RoomDetails>> {
//        return mSuggestedConferenceRoomList
//    }
//
//    /**
//     * function will return the MutableLiveData of Int if something went wrong at server
//     */
//    fun returnFailureForSuggestedRooms(): MutableLiveData<Any> {
//        return errorCodeFromServerForSuggestedRooms
//    }

}
