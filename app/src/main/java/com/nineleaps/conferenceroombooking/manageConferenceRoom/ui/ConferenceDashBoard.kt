package com.nineleaps.conferenceroombooking.manageConferenceRoom.ui

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.OnClick
import com.nineleaps.conferenceroombooking.BaseActivity
import com.nineleaps.conferenceroombooking.BaseApplication
import com.nineleaps.conferenceroombooking.ConferenceRoomDashboard.repository.ManageConferenceRoomRepository
import com.nineleaps.conferenceroombooking.ConferenceRoomDashboard.viewModel.ManageConferenceRoomViewModel
import com.nineleaps.conferenceroombooking.Helper.ConferenceRecyclerAdapter
import com.nineleaps.conferenceroombooking.Helper.NetworkState
import com.nineleaps.conferenceroombooking.Models.ConferenceList
import com.nineleaps.conferenceroombooking.R
import com.nineleaps.conferenceroombooking.addConferenceRoom.ui.AddingConference
import com.nineleaps.conferenceroombooking.blockRoom.ui.BlockConferenceRoomActivity
import com.nineleaps.conferenceroombooking.bookingDashboard.ui.UserBookingsDashboardActivity
import com.nineleaps.conferenceroombooking.checkConnection.NoInternetConnectionActivity
import com.nineleaps.conferenceroombooking.utils.*
import com.orhanobut.hawk.Hawk
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_conference_dash_board.*
import javax.inject.Inject


class ConferenceDashBoard : BaseActivity() {
    /**
     * Declaring Global variables and butter knife
     */
    @Inject
    lateinit var mManageRoomRepo: ManageConferenceRoomRepository

    @BindView(R.id.conference_list)
    lateinit var recyclerView: RecyclerView

    var buildingId: Int = 0

    private var cardPosition: Int = -1

    private lateinit var mManageConferenceRoomViewModel: ManageConferenceRoomViewModel

    private lateinit var conferenceRoomAdapter: ConferenceRecyclerAdapter

    private var mConferenceList = ArrayList<ConferenceList>()

    /**
     * Passing the Layout Resource to the Base Activity
     */
    override fun getLayoutResource(): Int {
        return R.layout.activity_conference_dash_board
    }

    /**
     * OnCreate Activity initialize related to the Adding Conference
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        observeDataForConferenceRoom()
        observeDataForDeleteConferenceRoom()
    }

    /**
     * initialize objects
     */
    fun init() {
        initActionBar(getString(R.string.Conference_Rooms))
        initComponentForManageRoomRepo()
        initLateInitalizerVariables()
        initManageRoomRepo()
        if (NetworkState.appIsConnectedToInternet(this)) {
            getConference(buildingId)
        } else {
            val i = Intent(this, NoInternetConnectionActivity::class.java)
            startActivityForResult(i, Constants.RES_CODE)
        }
    }

    /*
       Dependency Injection of Conference Dashboard
    */
    private fun initComponentForManageRoomRepo() {
        (application as BaseApplication).getmAppComponent()?.inject(this)
    }

    /**
     *Get the Conference Repository instance from the View Model
     */
    private fun initManageRoomRepo() {
        mManageConferenceRoomViewModel.setManageRoomRepo(mManageRoomRepo)
    }

    /*
      Initialize View Model
    */
    private fun initLateInitalizerVariables() {
        buildingId = getIntentData()
        mManageConferenceRoomViewModel = ViewModelProviders.of(this).get(ManageConferenceRoomViewModel::class.java)
    }

    /**
     * OnRestart Activity
     */
    override fun onRestart() {
        super.onRestart()
        when {
            NetworkState.appIsConnectedToInternet(this) -> getConference(buildingId)
            else -> {
                val i = Intent(this, NoInternetConnectionActivity::class.java)
                startActivityForResult(i, Constants.RES_CODE)
            }
        }
    }

    /**
     * onActivity Result when Internet is available
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.RES_CODE && resultCode == Activity.RESULT_OK) {
            getConference(buildingId)
        }
    }

    /**
     * observe data from server
     */
    private fun observeDataForConferenceRoom() {
        mManageConferenceRoomViewModel.returnConferenceRoomList().observe(this, Observer {
            hideProgressDialog()
            mConferenceList.clear()
            if (it.isNotEmpty()) {
                empty_view_blocked1.visibility = View.GONE
                mConferenceList.addAll(it)
            } else {

                empty_view_blocked1.visibility = View.VISIBLE
                empty_view_blocked1.setBackgroundColor(ContextCompat.getColor(this,R.color.white))
            }
            setAdapter()
        })
        mManageConferenceRoomViewModel.returnFailureForConferenceRoom().observe(this, Observer {
            hideProgressDialog()
            if (it == Constants.INVALID_TOKEN || it == Constants.FORBIDDEN || it == Constants.UNPROCESSABLE) ShowDialogForSessionExpired.signOut(
                this,
                ConferenceDashBoard()
            )
            else if (it == Constants.NO_CONTENT_FOUND) {

                empty_view_blocked1.visibility = View.VISIBLE
                empty_view_blocked1.setBackgroundColor(ContextCompat.getColor(this,R.color.white))
            } else {
                ShowToast.show(this, it as Int)
                finish()
            }
        })

    }
    /**
     * observe data from server for deletion
     */
    private fun observeDataForDeleteConferenceRoom(){
        mManageConferenceRoomViewModel.returnSuccessForDeleteRoom().observe(this, Observer {
            hideProgressDialog()
            if (cardPosition != -1)
                mConferenceList.remove(mConferenceList[cardPosition])
            if (mConferenceList.isEmpty()) {
                empty_view_blocked1.visibility = View.VISIBLE
                empty_view_blocked1.setBackgroundColor(ContextCompat.getColor(this,R.color.white))
            }
            conferenceRoomAdapter.notifyDataSetChanged()
            Toasty.success(this, getString(R.string.successfull_deletion)).show()
        })

        mManageConferenceRoomViewModel.returnFailureForDeleteRoom().observe(this, Observer {
            hideProgressDialog()
            if (it == Constants.UNPROCESSABLE || it == Constants.INVALID_TOKEN || it == Constants.FORBIDDEN) {
                ShowDialogForSessionExpired.showAlert(this, UserBookingsDashboardActivity())
            } else {
                ShowToast.show(this, it as Int)
            }
        })
    }

    /**
     * Initializing of Recycler View
     */
    private fun setAdapter() {
        conferenceRoomAdapter =
            ConferenceRecyclerAdapter(mConferenceList, object : ConferenceRecyclerAdapter.EditRoomDetails {
                override fun editRoom(position: Int) {
                    val intent = Intent(this@ConferenceDashBoard, AddingConference::class.java)
                    val editRoomDetails = EditRoomDetails()
                    editRoomDetails.mRoomDetail = mConferenceList[position]
                    intent.putExtra(Constants.FLAG, true)
                    intent.putExtra(Constants.EXTRA_INTENT_DATA, editRoomDetails)
                    startActivity(intent)
                }

            },
                object : ConferenceRecyclerAdapter.DeleteClickListner {
                    override fun deleteRoom(position: Int) {
                        showAlertDialogForDelete(mConferenceList[position].roomId, position)
                    }

                },
                object : ConferenceRecyclerAdapter.BlockClickListner {
                    override fun blockRoom(position: Int) {
                        val intent = Intent(this@ConferenceDashBoard, BlockConferenceRoomActivity::class.java)
                        intent.putExtra(Constants.BUILDING_ID, mConferenceList[position].buildingId!!.toInt())
                        intent.putExtra(Constants.ROOM_NAME, mConferenceList[position].roomName)
                        intent.putExtra(Constants.ROOM_ID, mConferenceList[position].roomId!!.toInt())
                        startActivity(intent)

                    }

                },
                object : ConferenceRecyclerAdapter.MoreAminitiesListner {
                    override fun moreAmenities(position: Int) {
                        showDialogForMoreAminities(mConferenceList[position].amenities!!)
                    }

                })
        recyclerView.adapter = conferenceRoomAdapter
    }

    /**
     * Show the List of Amenities in AlertDialog box if the amenity is more than 4
     */
    private fun showDialogForMoreAminities(items: HashMap<Int, String>) {
        val arrayListOfItems = ArrayList<String>()

        for (item in items) {
            arrayListOfItems.add(item.value)
        }
        val listItems = arrayOfNulls<String>(arrayListOfItems.size)
        arrayListOfItems.toArray(listItems)
        val builder = AlertDialog.Builder(this)
        builder.setItems(
            listItems
        ) { _, _ -> }
        val mDialog = builder.create()
        mDialog.show()
    }


    /**
     * Show Alert Dialog button for Delete
     */
    private fun showAlertDialogForDelete(roomId: Int?, position: Int) {
        val dialog = GetAleretDialog.getDialog(this, "Delete", "Are you sure you wnat to delete the Room")
        dialog.setPositiveButton(R.string.ok) { _, _ ->
            cardPosition = position
            mManageConferenceRoomViewModel.deleteConferenceRoom(roomId!!)
            getConference(buildingId)
        }
        dialog.setNegativeButton(R.string.cancel) { _, _ ->

        }
        val builder = GetAleretDialog.showDialog(dialog)
        ColorOfDialogButton.setColorOfDialogButton(builder)
    }

    /**
     * onClick on this button goes to AddingConference Activity
     */
    @OnClick(R.id.add_conferenece)
    fun addConfereeRoomFloatingActionButton() {
        goToNextActivity(buildingId)

    }

    /**
     * get the buildingId from the BuildingDashboard Activity
     */
    private fun getIntentData(): Int {
        val bundle: Bundle? = intent.extras!!
        return bundle!!.get(Constants.EXTRA_BUILDING_ID)!!.toString().toInt()
    }

    /**
     * Passing Intent and shared preference
     */
    private fun goToNextActivity(buildingId: Int) {
        Hawk.put(Constants.EXTRA_BUILDING_ID, buildingId)


        val intent = Intent(this, AddingConference::class.java)
        intent.putExtra(Constants.FLAG, false)
        intent.putExtra(Constants.EXTRA_BUILDING_ID, buildingId)
        startActivity(intent)
    }

    /**
     * function calls the ViewModel of Conference Room and observe data from the database
     */
    private fun getConference(buildingId: Int) {
        /**
         * getting Progress Dialog
         */
        showProgressDialog(this)
        mManageConferenceRoomViewModel.getConferenceRoomList(buildingId)
    }
}

