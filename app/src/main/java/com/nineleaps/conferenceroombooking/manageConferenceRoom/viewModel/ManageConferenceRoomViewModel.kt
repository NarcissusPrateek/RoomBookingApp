package com.nineleaps.conferenceroombooking.ConferenceRoomDashboard.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nineleaps.conferenceroombooking.ConferenceRoomDashboard.repository.ManageConferenceRoomRepository
import com.nineleaps.conferenceroombooking.Models.ConferenceList
import com.nineleaps.conferenceroombooking.services.ResponseListener

class ManageConferenceRoomViewModel : ViewModel() {

    private var mManageConferenceRoomRepository: ManageConferenceRoomRepository? = null
    var mHrConferenceRoomList = MutableLiveData<List<ConferenceList>>()
    var mFailureCodeForHrConferenceRoom = MutableLiveData<Any>()

    var mSuccessForDeleteBuilding = MutableLiveData<Int>()
    var mFailureForDeleteBuilding = MutableLiveData<Any>()

    fun setManageRoomRepo(mManageRoomRepo: ManageConferenceRoomRepository) {
        mManageConferenceRoomRepository = mManageRoomRepo
    }

    fun getConferenceRoomList(buildingId: Int, token: String) {
        mManageConferenceRoomRepository!!.getConferenceRoomList(buildingId, token, object :
            ResponseListener {
            override fun onSuccess(success: Any) {
                mHrConferenceRoomList.value = success as List<ConferenceList>
            }

            override fun onFailure(failure: Any) {
                mFailureCodeForHrConferenceRoom.value = failure
            }

        })
    }

    fun returnConferenceRoomList(): MutableLiveData<List<ConferenceList>> {
        return mHrConferenceRoomList
    }

    fun returnFailureForConferenceRoom(): MutableLiveData<Any> {
        return mFailureCodeForHrConferenceRoom
    }

    fun deleteConferenceRoom(token: String, roomId: Int) {
        mManageConferenceRoomRepository!!.deleteBuilding(token, roomId, object : ResponseListener {
            override fun onSuccess(success: Any) {
                mSuccessForDeleteBuilding.value = success as Int
            }

            override fun onFailure(failure: Any) {
                mFailureForDeleteBuilding.value = failure
            }

        })
    }

    fun returnSuccessForDeleteRoom(): MutableLiveData<Int> {
        return mSuccessForDeleteBuilding
    }

    /**
     * return negative response from server
     */
    fun returnFailureForDeleteRoom(): MutableLiveData<Any> {
        return mFailureForDeleteBuilding
    }
}
