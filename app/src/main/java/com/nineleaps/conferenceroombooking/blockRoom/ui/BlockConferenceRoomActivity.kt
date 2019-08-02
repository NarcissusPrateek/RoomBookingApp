package com.nineleaps.conferenceroombooking.blockRoom.ui

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.Html.fromHtml
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.analytics.FirebaseAnalytics
import com.nineleaps.conferenceroombooking.BaseApplication
import com.nineleaps.conferenceroombooking.Helper.NetworkState
import com.nineleaps.conferenceroombooking.Models.ConferenceList
import com.nineleaps.conferenceroombooking.R
import com.nineleaps.conferenceroombooking.blockRoom.repository.BlockRoomRepository
import com.nineleaps.conferenceroombooking.blockRoom.viewModel.BlockRoomViewModel
import com.nineleaps.conferenceroombooking.checkConnection.NoInternetConnectionActivity
import com.nineleaps.conferenceroombooking.manageBuildings.repository.BuildingsRepository
import com.nineleaps.conferenceroombooking.manageBuildings.viewModel.BuildingViewModel
import com.nineleaps.conferenceroombooking.model.BlockRoom
import com.nineleaps.conferenceroombooking.model.Building
import com.nineleaps.conferenceroombooking.utils.*
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_spinner.*
import javax.inject.Inject

@Suppress("NAME_SHADOWING", "DEPRECATION")
class BlockConferenceRoomActivity : AppCompatActivity() {

    /**
     * Declaring Global variables and butterknife
     */
    @Inject
    lateinit var mBlockRoomRepo: BlockRoomRepository

    @Inject
    lateinit var mBuildingRepo: BuildingsRepository

    @BindView(R.id.block_conference_room_progress_bar)
    lateinit var mProgressDialog: ProgressBar
    @BindView(R.id.fromTime_b)
    lateinit var fromTimeEditText: EditText
    @BindView(R.id.toTime_b)
    lateinit var toTimeEditText: EditText
    @BindView(R.id.date_block)
    lateinit var dateEditText: EditText
    @BindView(R.id.Purpose)
    lateinit var purposeEditText: EditText
    private lateinit var mBlockRoomViewModel: BlockRoomViewModel
    var room = BlockRoom()
    private lateinit var mFirebaseAnalytics: FirebaseAnalytics
    private lateinit var mBuildingViewModel: BuildingViewModel
    private lateinit var progressDialog: ProgressDialog
    private var mBuildingName = "Select Building"
    private var mRoomName = "Select Room"
    private var mBuildingId = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spinner)
        ButterKnife.bind(this)
        init()
        observeData()
        setDialogsToInputFields()
    }

    /**
     * initialize all lateinit variables
     */
    fun init() {
        initActionBar()
        initTextChangeListener()
        initComponent()
        initLateInitializerVariables()
        initBuildingRepo()
        initBlockRoomRepo()
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        if(NetworkState.appIsConnectedToInternet(this)) {
            getBuilding()
        } else {
            val i = Intent(this@BlockConferenceRoomActivity, NoInternetConnectionActivity::class.java)
            startActivityForResult(i, Constants.RES_CODE)
        }
    }

    private fun initComponent() {
        (application as BaseApplication).getmAppComponent()?.inject(this)
    }

    private fun initBlockRoomRepo() {
        mBlockRoomViewModel.setBlockRoomRepo(mBlockRoomRepo)
    }

    private fun initBuildingRepo() {
        mBuildingViewModel.setBuildingRepository(mBuildingRepo)
    }

    private fun initActionBar() {
        val actionBar = supportActionBar
        actionBar!!.title = fromHtml("<font color=\"#FFFFFF\">" + getString(R.string.Block) + "</font>")
    }

    private fun initTextChangeListener() {
        textChangeListenerOnDateEditText()
        textChangeListenerOnFromTimeEditText()
        textChangeListenerOnToTimeEditText()
        textChangeListenerOnPurposeEditText()

    }

    private fun initLateInitializerVariables() {
        progressDialog = GetProgress.getProgressDialog(getString(R.string.progress_message), this)
        mBuildingViewModel = ViewModelProviders.of(this).get(BuildingViewModel::class.java)
        mBlockRoomViewModel = ViewModelProviders.of(this).get(BlockRoomViewModel::class.java)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                Constants.RES_CODE -> getBuilding()

                Constants.RES_CODE2 -> {
                    conferenceRoomListFromBackend(mBuildingId)
                }
            }
        }
    }

    /**
     * observing data for building list
     */
    private fun observeData() {
        mBuildingViewModel.returnMBuildingSuccess().observe(this, Observer {
            mProgressDialog.visibility = View.GONE
            buildingListFromBackend(it)
        })

        mBuildingViewModel.returnMBuildingFailure().observe(this, Observer {
            mProgressDialog.visibility = View.GONE
            when (it) {
                Constants.INVALID_TOKEN -> ShowDialogForSessionExpired.showAlert(this, BlockConferenceRoomActivity())
                Constants.NO_CONTENT_FOUND -> Toast.makeText(this, getString(R.string.empty_building_list), Toast.LENGTH_SHORT).show()
                else -> {
                    ShowToast.show(this, it as Int)
                    finish()
                }
            }

        })

        // observer for Block room
        mBlockRoomViewModel.returnSuccessForBlockRoom().observe(this, Observer {
            progressDialog.dismiss()
            Toasty.success(this, getString(R.string.room_is_blocked), Toast.LENGTH_SHORT, true).show()
            finish()
        })

        mBlockRoomViewModel.returnResponseErrorForBlockRoom().observe(this, Observer {
            progressDialog.dismiss()
            if (it == Constants.INVALID_TOKEN) {
                ShowDialogForSessionExpired.showAlert(this, BlockConferenceRoomActivity())
            } else {
                ShowToast.show(this, it as Int)
            }
        })

        // observer for block confirmation
        mBlockRoomViewModel.returnSuccessForConfirmation().observe(this, Observer {
            progressDialog.dismiss()
            if (it.mStatus == 0) {
                blockConfirmed(room)
            } else {
                val builder = AlertDialog.Builder(this@BlockConferenceRoomActivity)
                builder.setTitle(getString(R.string.blockingStatus))
                val name = it.name
                val purpose = it.purpose
                builder.setMessage(
                        "Room is Booked by Employee '$name' for '$purpose'.\nAre you sure the 'BLOCKING' is Necessary?"
                )
                builder.setPositiveButton(getString(R.string.yes)) { _, _ ->
                    blockConfirmed(room)
                }
                builder.setNegativeButton(getString(R.string.no)) { _, _ ->
                    /**
                     * do nothing
                     */
                }
                builder.setCancelable(false)
                val dialog: AlertDialog = builder.create()
                dialog.show()
                ColorOfDialogButton.setColorOfDialogButton(dialog)
            }
        })

        mBlockRoomViewModel.returnResponseErrorForConfirmation().observe(this, Observer {
            progressDialog.dismiss()
            if (it == Constants.INVALID_TOKEN) {
                ShowDialogForSessionExpired.showAlert(this, BlockConferenceRoomActivity())
            } else {
                ShowToast.show(this, it as Int)
                finish()
            }
        })

        // observer for conference room list

        mBlockRoomViewModel.returnConferenceRoomList().observe(this, Observer {
            mProgressDialog.visibility = View.GONE
            setSpinnerToConferenceList(it)
        })

        mBlockRoomViewModel.returnResponseErrorForConferenceRoom().observe(this, Observer {
            mProgressDialog.visibility = View.GONE
            if (it == Constants.INVALID_TOKEN) {
                ShowDialogForSessionExpired.showAlert(this, BlockConferenceRoomActivity())
            } else {
                ShowToast.show(this, it as Int)
                finish()
            }
        })

    }

    /**
     * function will invoke whenever the block button is pressed
     */

    @OnClick(R.id.block_conference)
    fun blockButton() {
        if (validateInput()) {
            validationOnDataEnteredByUser()
        }
    }

    /**
     *  set values to the different properties of object which is required for api call
     */
    private fun addDataToObject() {
        val acct = GoogleSignIn.getLastSignedInAccount(applicationContext)
        val startTime = dateEditText.text.toString() + " " + fromTimeEditText.text.toString()
        val endTime = dateEditText.text.toString() + " " + toTimeEditText.text.toString()
        room.email = acct!!.email
        room.purpose = purposeEditText.text.toString()
        room.fromTime = FormatTimeAccordingToZone.formatDateAsUTC(startTime)
        room.toTime = FormatTimeAccordingToZone.formatDateAsUTC(endTime)
        room.status = getString(R.string.block_room)
    }

    /**
     * set the date and time picker
     */
    private fun setDialogsToInputFields() {

        fromTimeEditText.setOnClickListener {
            DateAndTimePicker.getTimePickerDialog(this, fromTimeEditText)
        }

        toTimeEditText.setOnClickListener {
            DateAndTimePicker.getTimePickerDialog(this, toTimeEditText)
        }

        dateEditText.setOnClickListener {
            DateAndTimePicker.getDatePickerDialog(this, dateEditText)
        }


    }

    /**
     * function calls the ViewModel of getbuilding
     */
    private fun getBuilding() {
        mProgressDialog.visibility = View.VISIBLE
        // make api call
        mBuildingViewModel.getBuildingList(GetPreference.getTokenFromPreference(this))
    }

    /**
     * function calls the ViewModel of blocking
     */
    private fun blocking(room: BlockRoom) {
        progressDialog.show()
        mBlockRoomViewModel.blockingStatus(room)
    }

    /**
     * function calls the ViewModel of blockingConfirmed
     */
    private fun blockConfirmed(mRoom: BlockRoom) {
        progressDialog.show()
        mBlockRoomViewModel.blockRoom(mRoom)
    }

    /**
     * function calls the ViewModel of getConference for the Selected Building
     */
    fun conferenceRoomListFromBackend(buildingId: Int) {
        mProgressDialog.visibility = View.VISIBLE
        mBlockRoomViewModel.getRoomList(buildingId)
    }

    /**
     *Setting the empty text to the Spinner if the data is empty
     */
    private fun buildingListFromBackend(it: List<Building>) {
        sendDataForSpinner(it)
    }

    /**
     * Setting the Building Data to the Spinner if the data is not empty
     */
    private fun sendDataForSpinner(it: List<Building>) {
        val items = mutableListOf<String>()
        val itemsId = mutableListOf<Int>()
        items.add(getString(R.string.select_building))
        itemsId.add(-1)
        for (item in it) {
            items.add(item.buildingName!!)
            itemsId.add(item.buildingId!!.toInt())
        }
        buiding_Spinner.adapter =
                ArrayAdapter<String>(this@BlockConferenceRoomActivity, R.layout.spinner_icon, R.id.spinner_text, items)
        buiding_Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                /**
                 * It selects the first building
                 */
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                room.bId = itemsId[position]
                mBuildingName = items[position]
                mBuildingId = itemsId[position]
                error_spinner_building_text_view.visibility = View.INVISIBLE
                if(NetworkState.appIsConnectedToInternet(this@BlockConferenceRoomActivity)) {
                    conferenceRoomListFromBackend(itemsId[position])
                } else {
                    val i = Intent(this@BlockConferenceRoomActivity, NoInternetConnectionActivity::class.java)
                    startActivityForResult(i, Constants.RES_CODE2)
                }
            }
        }
    }

    /**
     * Setting the Conference Data to the Spinner
     */
    private fun setSpinnerToConferenceList(it: List<ConferenceList>) {
        val conferencename = mutableListOf<String>()
        val conferenceid = mutableListOf<Int>()
        if (it.isEmpty()) {
            conferencename.add(getString(R.string.no_room_in_building))
            conferenceid.add(-1)
        } else {
            conferencename.add(getString(R.string.select_room))
        }
        conferenceid.add(-1)
        for (item in it) {
            conferencename.add(item.roomName!!)
            conferenceid.add(item.roomId!!)
        }
        conference_Spinner.adapter =
                ArrayAdapter<String>(
                        this@BlockConferenceRoomActivity,
                        R.layout.spinner_icon,
                        R.id.spinner_text,
                        conferencename
                )
        conference_Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                /**
                 * It selects the first conference room
                 */
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                room.cId = conferenceid[position]
                mRoomName = conferencename[position]
                error_spinner_room_text_view.visibility = View.INVISIBLE
            }
        }
    }


    /**
     * validate from time field
     */
    private fun validateFromTime(): Boolean {
        val input = fromTimeEditText.text.toString().trim()
        return if (input.isEmpty()) {
            block_from_time_layout.error = getString(R.string.field_cant_be_empty)
            false
        } else {
            block_from_time_layout.error = null
            true
        }
    }

    /**
     * validate to time field
     */
    private fun validateToTime(): Boolean {
        val input = toTimeEditText.text.toString().trim()
        return if (input.isEmpty()) {
            block_to_time_layout.error = getString(R.string.field_cant_be_empty)
            false
        } else {
            block_to_time_layout.error = null
            true
        }
    }

    /**
     * validate date field
     */
    private fun validateDate(): Boolean {
        val input = dateEditText.text.toString().trim()
        return if (input.isEmpty()) {
            block_date_layout.error = getString(R.string.field_cant_be_empty)
            false
        } else {
            block_date_layout.error = null
            true
        }
    }

    /**
     * validate purpose field
     */
    private fun validatePurpose(): Boolean {
        val input = purposeEditText.text.toString().trim()
        return if (input.isEmpty()) {
            purpose_layout.error = getString(R.string.field_cant_be_empty)
            false
        } else {
            purpose_layout.error = null
            true
        }
    }

    /**
     * validate building spinner
     */
    private fun validateBuildingSpinner(): Boolean {
        return if (mBuildingName == getString(R.string.select_building)) {
            error_spinner_building_text_view.visibility = View.VISIBLE
            false
        } else {
            error_spinner_building_text_view.visibility = View.INVISIBLE
            true
        }
    }

    /**
     * validate conference room spinner
     */
    private fun validateRoomSpinner(): Boolean {
        return if (mRoomName == getString(R.string.select_room)) {
            error_spinner_room_text_view.visibility = View.VISIBLE
            false
        } else {
            error_spinner_room_text_view.visibility = View.INVISIBLE
            true
        }
    }

    private fun validateInput(): Boolean {
        if (!validateFromTime() or !validateToTime() or !validateDate() or !validatePurpose() or !validateBuildingSpinner() or !validateRoomSpinner()) {
            return false
        }
        return true
    }

    /**
     * Validating the from-time and to-time having the conditions
     */
    private fun validationOnDataEnteredByUser() {
        val minmilliseconds: Long = 600000

        val startTime = fromTime_b.text.toString()
        val endTime = toTime_b.text.toString()

        val builder = AlertDialog.Builder(this@BlockConferenceRoomActivity)
        builder.setTitle(getString(R.string.check))
        try {
            val (elapsed, elapsed2) = ConvertTimeInMillis.calculateTimeInMilliseconds(
                    startTime,
                    endTime,
                    date_block.text.toString()
            )
            if (room.cId!!.compareTo(-1) == 0) {
                Toast.makeText(this, R.string.invalid_conference_room, Toast.LENGTH_SHORT).show()
            }
            if (elapsed2 < 0) {
                builder.setMessage(getString(R.string.invalid_from_time))
                builder.setPositiveButton(getString(R.string.ok_label)) { _, _ ->
                }
                val dialog: AlertDialog = builder.create()
                dialog.setCanceledOnTouchOutside(false)
                dialog.show()
                ColorOfDialogButton.setColorOfDialogButton(dialog)

            } else {
                if (minmilliseconds <= elapsed) {
                    blockRoom()
                } else {
                    val builder = AlertDialog.Builder(this@BlockConferenceRoomActivity).also {
                        it.setTitle(getString(R.string.check))
                        it.setMessage(getString(R.string.time_validation_message))
                    }
                    builder.setPositiveButton(getString(R.string.ok_label)) { _, _ ->
                    }
                    val dialog: AlertDialog = builder.create()
                    dialog.setCanceledOnTouchOutside(false)
                    dialog.show()
                    ColorOfDialogButton.setColorOfDialogButton(dialog)


                }
            }
        } catch (e: Exception) {
            Toast.makeText(this@BlockConferenceRoomActivity, getString(R.string.details_invalid), Toast.LENGTH_LONG)
                    .show()
        }
    }

    /**
     * Adding the data to the objects
     */
    private fun blockRoom() {
        addDataToObject()
        blocking(room)
        blockLogFirebaseAnalytics()
    }

    private fun blockLogFirebaseAnalytics() {
        val blockBundle = Bundle()
        mFirebaseAnalytics.logEvent(getString(R.string.Block_Room),blockBundle)
        mFirebaseAnalytics.setAnalyticsCollectionEnabled(true)
        mFirebaseAnalytics.setMinimumSessionDuration(5000)
        mFirebaseAnalytics.setSessionTimeoutDuration(1000000)
        mFirebaseAnalytics.setUserId(room.email)
        mFirebaseAnalytics.setUserProperty(getString(R.string.Roll_Id),GetPreference.getRoleIdFromPreference(this).toString())
    }

    /**
     * add text change listener for the start time edit text
     */
    private fun textChangeListenerOnFromTimeEditText() {
        fromTimeEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // nothing here
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // nothing here
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateFromTime()
            }
        })
    }

    /**
     * add text change listener for the end time edit text
     */
    private fun textChangeListenerOnToTimeEditText() {
        toTimeEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // nothing here
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // nothing here
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateToTime()
            }
        })
    }

    /**
     * add text change listener for the start edit text
     */
    private fun textChangeListenerOnDateEditText() {
        dateEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // nothing here
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // nothing here
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateDate()

            }
        })
    }

    /**
     * add text change listener for the purpose edit text
     */
    private fun textChangeListenerOnPurposeEditText() {
        purposeEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // nothing here
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // nothing here
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validatePurpose()
            }
        })
    }

}