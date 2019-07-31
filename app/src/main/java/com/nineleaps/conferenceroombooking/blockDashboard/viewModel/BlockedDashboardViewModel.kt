package com.nineleaps.conferenceroombooking.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nineleaps.conferenceroombooking.Blocked
import com.nineleaps.conferenceroombooking.blockDashboard.repository.BlockDashboardRepository
import com.nineleaps.conferenceroombooking.services.ResponseListener

class BlockedDashboardViewModel : ViewModel() {

    private var mBlockDashboardRepository: BlockDashboardRepository? = null
    var mBlockedRoomList = MutableLiveData<List<Blocked>>()
    var mFailureCodeForBlockedRoomList = MutableLiveData<Any>()
    var mSuccessCodeForBlockRoom =  MutableLiveData<Int>()
    var mFailureCodeForBlockRoom =  MutableLiveData<Any>()

    fun setBlockedRoomDashboardRepo(mBlockRoomDashBoardRepo: BlockDashboardRepository) {
        this.mBlockDashboardRepository = mBlockRoomDashBoardRepo
    }

    fun getBlockedList(token: String) {
        mBlockDashboardRepository!!.getBlockedList(token, object :
            ResponseListener {
            override fun onSuccess(success: Any) {
                mBlockedRoomList.value = success as List<Blocked>
            }

            override fun onFailure(failure: Any) {
                mFailureCodeForBlockedRoomList.value = failure
            }

        })

    }
    fun returnBlockedRoomList(): MutableLiveData<List<Blocked>> {
        return mBlockedRoomList
    }

    fun returnFailureCodeFromBlockedApi(): MutableLiveData<Any> {
        return mFailureCodeForBlockedRoomList
    }
    //-----------------------------------------------------------------------------------------------------------

    fun unBlockRoom(token: String, bookingId: Int) {
        mBlockDashboardRepository!!.unblockRoom(token, bookingId, object:
            ResponseListener {
            override fun onSuccess(success: Any) {
                mSuccessCodeForBlockRoom.value = success as Int
            }

            override fun onFailure(failure: Any) {
                mFailureCodeForBlockRoom.value = failure
            }

        })
    }

    fun returnSuccessCodeForUnBlockRoom(): MutableLiveData<Int> {
        return mSuccessCodeForBlockRoom
    }
    fun returnFailureCodeForUnBlockRoom(): MutableLiveData<Any> {
        return mFailureCodeForBlockRoom
    }
}