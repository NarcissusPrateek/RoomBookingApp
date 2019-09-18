package com.nineleaps.conferenceroombooking.blockRoom.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nineleaps.conferenceroombooking.Models.ConferenceList
import com.nineleaps.conferenceroombooking.blockRoom.repository.BlockRoomRepository
import com.nineleaps.conferenceroombooking.model.BlockRoom
import com.nineleaps.conferenceroombooking.model.BlockingConfirmation
import com.nineleaps.conferenceroombooking.services.ResponseListener


class BlockRoomViewModel : ViewModel() {
    /**
     * a object which will hold the reference to the corresponding repository class
     */
    private var mBlockRoomRepository: BlockRoomRepository? = null

    /**
     * a MutableLiveData variable which will hold the for positive response from server for the confirmation of blocking
     */
    var mConfirmation = MutableLiveData<BlockingConfirmation>()

    /**
     * a variable to hold positive response from backend for blocking room
     */
    var mSuccessForBlockRoom = MutableLiveData<Int>()

    /**
     * a variable to hold failure code from backend whenever unable to fetch the confirmation details from server
     */
    var mFailureCodeForConfirmationOfBlocking = MutableLiveData<Any>()

    /**
     * a variable to hold failure code from backend whenever unable to block the room
     */
    var mFailureCodeForBlockRoom = MutableLiveData<Any>()

    fun setBlockRoomRepo(mBlockRoomRepo: BlockRoomRepository) {
        this.mBlockRoomRepository = mBlockRoomRepo
    }

    /**
     * function will initialize the repository object and calls the method of repository which will make the api call
     * and function will return the value for MutableLivedata
     */
    fun blockRoom(mRoom: BlockRoom) {
        mBlockRoomRepository!!.blockRoom(mRoom, object : ResponseListener {
            override fun onSuccess(success: Any) {
                mSuccessForBlockRoom.value = success as Int
            }

            override fun onFailure(failure: Any) {
                mFailureCodeForBlockRoom.value = failure
            }

        })
    }

    fun returnSuccessForBlockRoom(): MutableLiveData<Int> {
        return mSuccessForBlockRoom
    }

    fun returnResponseErrorForBlockRoom(): MutableLiveData<Any> {
        return mFailureCodeForBlockRoom
    }

    /**
     * ----------------------------------------------------------------------------------------------------------------
     */

    /**
     * function will initialize the repository object and calls the method of repository which will make the api call
     * and function will return the value for MutableLivedata
     */
    fun blockingStatus(mRoom: BlockRoom) {
        mBlockRoomRepository!!.blockingStatus(mRoom, object :
            ResponseListener {
            override fun onSuccess(success: Any) {
                mConfirmation.value = success as BlockingConfirmation
            }

            override fun onFailure(failure: Any) {
                mFailureCodeForConfirmationOfBlocking.value = failure
            }

        })
    }

    fun returnSuccessForConfirmation(): MutableLiveData<BlockingConfirmation> {
        return mConfirmation
    }

    fun returnResponseErrorForConfirmation(): MutableLiveData<Any> {
        return mFailureCodeForConfirmationOfBlocking
    }


}