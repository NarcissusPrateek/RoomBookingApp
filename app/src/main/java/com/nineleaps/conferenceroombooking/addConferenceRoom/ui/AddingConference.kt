package com.nineleaps.conferenceroombooking.addConferenceRoom.ui

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.google.android.material.switchmaterial.SwitchMaterial
import com.nineleaps.conferenceroombooking.AddConferenceRoom
import com.nineleaps.conferenceroombooking.BaseApplication
import com.nineleaps.conferenceroombooking.Helper.NetworkState
import com.nineleaps.conferenceroombooking.R
import com.nineleaps.conferenceroombooking.addConferenceRoom.repository.AddConferenceRepository
import com.nineleaps.conferenceroombooking.addConferenceRoom.viewModel.AddConferenceRoomViewModel
import com.nineleaps.conferenceroombooking.checkConnection.NoInternetConnectionActivity
import com.nineleaps.conferenceroombooking.utils.*
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_adding_conference.*
import javax.inject.Inject

@Suppress("DEPRECATION")
class AddingConference : AppCompatActivity() {
    /**
     * Declaring Global variables and binned butter knife
     */

    @Inject
    lateinit var mAddRoomRepo: AddConferenceRepository

    @BindView(R.id.conference_Name)
    lateinit var conferenceRoomEditText: EditText

    @BindView(R.id.conference_capacity)
    lateinit var roomCapacity: EditText

    @BindView(R.id.monitor_check_box)
    lateinit var monitor: CheckBox

    @BindView(R.id.whiteboard_marker_check_box)
    lateinit var whiteboard: CheckBox

    @BindView(R.id.speaker_check_box)
    lateinit var speaker: CheckBox

    @BindView(R.id.extension_board_check_box)
    lateinit var extensionBoard: CheckBox

    @BindView(R.id.projector_check_box)
    lateinit var projector: CheckBox

    @BindView(R.id.permission_required)
    lateinit var switchButton: SwitchMaterial

    private lateinit var mAddConferenceRoomViewModel: AddConferenceRoomViewModel
    private var mConferenceRoom = AddConferenceRoom()
    private lateinit var progressDialog: ProgressDialog
    private var floorList: List<String> = listOf("Select Floor", "1st", "2nd", "3rd", "4th", "5th", "6th", "7th")
    private var roomFloor = Constants.SELECT_FLOOR
    private var flag = false
    var roomId = 0
    private var mEditRoomDetails = EditRoomDetails()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adding_conference)
        ButterKnife.bind(this)
        init()
        getIntentData()
        observeData()
    }

    /**
     * initialize all lateinit variables
     */
    fun init() {
        initActionBar()
        initComponent()
        initTextChangeListener()
        initLateInitializerVariables()
        initRoomRepository()
        intsetfloorSpinner()
    }

    private fun intsetfloorSpinner() {
        val adapter = ArrayAdapter<String>(this, R.layout.spinner_icon, R.id.spinner_text, floorList)
        conference_room_floor.adapter = adapter
        conference_room_floor.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                roomFloor = getString(R.string.select_floor)
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                roomFloor = floorList[position]

            }

        }
    }

    private fun initTextChangeListener() {
        textChangeListenerOnRoomName()
        textChangeListenerOnRoomCapacity()
    }

    private fun initComponent() {
        (application as BaseApplication).getmAppComponent()?.inject(this)
    }

    private fun initRoomRepository() {
        mAddConferenceRoomViewModel.setAddingConferenceRoomRepo(mAddRoomRepo)
    }

    private fun getIntentData() {
        flag = intent.getBooleanExtra(Constants.FLAG, false)
        if (flag) {
            add_conference_room.text = getString(R.string.update_button)
            mEditRoomDetails = intent.getSerializableExtra(Constants.EXTRA_INTENT_DATA) as EditRoomDetails
            roomId = mEditRoomDetails.mRoomDetail!!.roomId!!
            roomCapacity.text = mEditRoomDetails.mRoomDetail!!.capacity.toString().toEditable()
            conferenceRoomEditText.text = mEditRoomDetails.mRoomDetail!!.roomName!!.toEditable()
            switchButton.isChecked = mEditRoomDetails.mRoomDetail!!.permission!! != 0
            for (amenity in mEditRoomDetails.mRoomDetail!!.amenities!!) {
                when (amenity) {
                    Constants.PROJECTOR -> projector.isChecked = true
                    Constants.MONITOR -> monitor.isChecked = true
                    Constants.SPEAKER -> speaker.isChecked = true
                    Constants.WHITEBOARD_MARKER -> whiteboard.isChecked = true
                    Constants.EXTENSION_BOARD -> extensionBoard.isChecked = true
                }
            }
        } else {
            add_conference_room.text = getString(R.string.ADD)
        }
    }

    private fun initLateInitializerVariables() {
        progressDialog = GetProgress.getProgressDialog(getString(R.string.progress_message_processing), this)
        mAddConferenceRoomViewModel = ViewModelProviders.of(this).get(AddConferenceRoomViewModel::class.java)
    }

    private fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

    private fun initActionBar() {
        val actionBar = supportActionBar
        actionBar!!.title = Html.fromHtml("<font color=\"#FFFFFF\">" + getString(R.string.Add_Room) + "</font>")
    }

    /**
     * observing data for adding conference
     */
    fun observeData() {
        mAddConferenceRoomViewModel.returnSuccessForAddingRoom().observe(this, Observer {
            progressDialog.dismiss()
            Toasty.success(this, getString(R.string.room_add_success), Toast.LENGTH_SHORT, true).show()
            finish()
        })
        mAddConferenceRoomViewModel.returnFailureForAddingRoom().observe(this, Observer {
            progressDialog.dismiss()
            if (it == getString(R.string.invalid_token)) {
                ShowDialogForSessionExpired.showAlert(this, AddingConference())
            } else {
                ShowToast.show(this, it as Int)
            }
        })

        mAddConferenceRoomViewModel.returnSuccessForUpdateRoom().observe(this, Observer {
            progressDialog.dismiss()
            Toasty.success(this, getString(R.string.room_details_updated), Toast.LENGTH_SHORT, true).show()
            finish()
        })
        mAddConferenceRoomViewModel.returnFailureForUpdateRoom().observe(this, Observer {
            progressDialog.dismiss()
            when (it) {
                getString(R.string.invalid_token) -> ShowDialogForSessionExpired.showAlert(this, AddingConference())
                Constants.UNAVAILABLE_SLOT -> Toasty.info(
                    this,
                    getString(R.string.room_name_conflict_message),
                    Toasty.LENGTH_SHORT,
                    true
                ).show()
                else -> ShowToast.show(this, it as Int)
            }
        })
    }

    /**
     * function will invoke whenever the add button is clicked
     */
    @OnClick(R.id.add_conference_room)
    fun addRoomButton() {
        if (validateInputs()) {
            if (NetworkState.appIsConnectedToInternet(this)) {
                addDataToObject(mConferenceRoom)
                if (flag) {
                    mConferenceRoom.roomId = roomId
                    mConferenceRoom.newRoomName = conferenceRoomEditText.text.toString().trim()
                    updateRoomDetails()
                } else {
                    addRoom()
                }
            } else {
                val i = Intent(this@AddingConference, NoInternetConnectionActivity::class.java)
                startActivityForResult(i, Constants.RES_CODE)
            }

        }
    }

    // make api call with updated data to update room details
    private fun updateRoomDetails() {
        progressDialog.show()
        mAddConferenceRoomViewModel.updateConferenceDetails(GetPreference.getTokenFromPreference(this), mConferenceRoom)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.RES_CODE && resultCode == Activity.RESULT_OK) {
            addDataToObject(mConferenceRoom)
            addRoom()
        }
    }

    /**
     *  set values to the different properties of object which is required for api call
     */
    private fun addDataToObject(mConferenceRoom: AddConferenceRoom) {
        if (flag) {
            mConferenceRoom.bId = mEditRoomDetails.mRoomDetail!!.buildingId
        } else {
            val bundle: Bundle? = intent.extras
            mConferenceRoom.bId = bundle!!.get(Constants.EXTRA_BUILDING_ID)!!.toString().toInt()
        }
        mConferenceRoom.roomName = conferenceRoomEditText.text.toString().trim() + "" + "(" + roomFloor + " floor)"
        mConferenceRoom.capacity = roomCapacity.text.toString().toInt()
        mConferenceRoom.monitor = monitor.isChecked
        mConferenceRoom.speaker = speaker.isChecked
        mConferenceRoom.whiteBoardMarker = whiteboard.isChecked
        mConferenceRoom.extensionBoard = extensionBoard.isChecked
        mConferenceRoom.projector = projector.isChecked
        mConferenceRoom.permission = switchButton.isChecked
    }


    /**
     * validation for room employeeList
     */
    private fun validateRoomName(): Boolean {
        val input = conferenceRoomEditText.text.toString().trim()
        return if (input.isEmpty()) {
            room_name_layout_name.error = getString(R.string.field_cant_be_empty)
            false
        } else {
            room_name_layout_name.error = null
            true
        }
    }

    /**
     * validation for spinner
     */
    private fun validateRoomCapacity(): Boolean {
        return if (roomCapacity.text.toString().trim().isEmpty()) {
            room_capacity_layout.error = getString(R.string.field_cant_be_empty)
            false
        } else {
            val input = roomCapacity.text.toString().toLong()
            if (input <= 0 || input > Int.MAX_VALUE) {
                room_capacity_layout.error = getString(R.string.room_capacity_must_be_more_than_0)
                false
            } else {
                room_capacity_layout.error = null
                true
            }
        }
    }

    /**
     * validate building spinner
     */
    private fun validateFloorSpinner(): Boolean {
        return if (roomFloor == getString(R.string.select_floor)) {
            floor_error.visibility = View.VISIBLE
            false
        } else {
            floor_error.visibility = View.INVISIBLE
            true
        }
    }

    /**
     * validate all input fields
     */
    private fun validateInputs(): Boolean {
        if (!validateRoomName() or !validateRoomCapacity() or !validateFloorSpinner()) {
            return false
        }
        return true
    }

    /**
     * function calls the ViewModel of addingConference and data into the database
     */
    private fun addRoom() {
        progressDialog.show()
        mAddConferenceRoomViewModel.addConferenceDetails(GetPreference.getTokenFromPreference(this), mConferenceRoom)
    }

    /**
     * add text change listener for the room name
     */
    private fun textChangeListenerOnRoomName() {
        conferenceRoomEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // nothing here
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // nothing here
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateRoomName()
            }
        })
    }

    /**
     * add text change listener for the room name
     */
    private fun textChangeListenerOnRoomCapacity() {
        roomCapacity.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // nothing here
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // nothing here
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateRoomCapacity()
            }
        })
    }
}
