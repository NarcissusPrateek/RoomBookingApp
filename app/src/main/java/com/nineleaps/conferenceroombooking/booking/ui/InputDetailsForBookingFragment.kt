package com.nineleaps.conferenceroombooking.booking.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.example.conferenceroomapp.model.InputDetailsForRoom
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.nineleaps.conferenceroombooking.BaseApplication
import com.nineleaps.conferenceroombooking.Helper.NetworkState
import com.nineleaps.conferenceroombooking.Helper.RoomAdapter
import com.nineleaps.conferenceroombooking.R
import com.nineleaps.conferenceroombooking.booking.repository.ConferenceRoomRepository
import com.nineleaps.conferenceroombooking.booking.viewModel.ConferenceRoomViewModel
import com.nineleaps.conferenceroombooking.bookingDashboard.ui.UserBookingsDashboardActivity
import com.nineleaps.conferenceroombooking.checkConnection.NoInternetConnectionActivity
import com.nineleaps.conferenceroombooking.manageBuildings.repository.BuildingsRepository
import com.nineleaps.conferenceroombooking.manageBuildings.viewModel.BuildingViewModel
import com.nineleaps.conferenceroombooking.model.Building
import com.nineleaps.conferenceroombooking.model.GetIntentDataFromActvity
import com.nineleaps.conferenceroombooking.model.RoomDetails
import com.nineleaps.conferenceroombooking.utils.*
import kotlinx.android.synthetic.main.activity_booking_input.*
import kotlinx.android.synthetic.main.activity_booking_input_from_user.booking_scroll_view
import kotlinx.android.synthetic.main.activity_booking_input_from_user.building_name_spinner
import kotlinx.android.synthetic.main.activity_booking_input_from_user.capacity_layout
import kotlinx.android.synthetic.main.activity_booking_input_from_user.date
import kotlinx.android.synthetic.main.activity_booking_input_from_user.date_layout
import kotlinx.android.synthetic.main.activity_booking_input_from_user.from_time_layout
import kotlinx.android.synthetic.main.activity_booking_input_from_user.horizontal_line_below_search_button
import kotlinx.android.synthetic.main.activity_booking_input_from_user.purpose_layout
import kotlinx.android.synthetic.main.activity_booking_input_from_user.search_room
import kotlinx.android.synthetic.main.activity_booking_input_from_user.suggestions
import kotlinx.android.synthetic.main.activity_booking_input_from_user.text_view_error_spinner_building
import javax.inject.Inject


class InputDetailsForBookingFragment : Fragment() {

    @Inject
    lateinit var mRoomRepo: ConferenceRoomRepository

    @Inject
    lateinit var mBuildingRepo: BuildingsRepository

    @BindView(R.id.input_for_booking_progress_bar)
    lateinit var mProgressBar: ProgressBar
    @BindView(R.id.date)
    lateinit var dateEditText: EditText
    @BindView(R.id.fromTime)
    lateinit var fromTimeEditText: EditText
    @BindView(R.id.toTime)
    lateinit var toTimeEditText: EditText
    @BindView(R.id.room_capacity)
    lateinit var roomCapacityEditText: EditText
    private lateinit var customAdapter: RoomAdapter
    private lateinit var mBuildingsViewModel: BuildingViewModel
    @BindView(R.id.recycler_view_room_list)
    lateinit var mRecyclerView: RecyclerView
    @BindView(R.id.event_name_text_view)
    lateinit var purposeEditText: EditText
    @BindView(R.id.booking_scroll_view)
    lateinit var scrollView: NestedScrollView
    lateinit var intent:Intent
    private lateinit var mSetDataFromActivity: GetIntentDataFromActvity
    private lateinit var mConferenceRoomViewModel: ConferenceRoomViewModel
    private var mInputDetailsForRoom = InputDetailsForRoom()
    private var mSuggestedRoomApiIsCallled = false
    private lateinit var acct: GoogleSignInAccount
    var mSetIntentData = GetIntentDataFromActvity()
    var mBuildingName = Constants.SELECT_BUILDING
    var mBuildingId = -1
    var capacity = 0
    var buildingId = 0
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.activity_booking_input, container, false)
        ButterKnife.bind(this, view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        observeData()
    }

    private fun clickListenerOnSearchButton() {
        search_room.setOnClickListener {
            suggestions.visibility = View.GONE
            purposeEditText.onEditorAction(EditorInfo.IME_ACTION_DONE)
            roomCapacityEditText.onEditorAction(EditorInfo.IME_ACTION_DONE)
            validationOnDataEnteredByUser()
        }
    }

    private fun initTextChangeListener() {
        textChangeListenerOnDateEditText()
        textChangeListenerOnFromTimeEditText()
        textChangeListenerOnToTimeEditText()
        textChangeListenerOnPurposeEditText()
        textChangeListenerOnRoomCapacity()
    }

    private fun init() {
        //HideSoftKeyboard.hideSoftKeyboard(activity!!)
        setPickerToEditText()
        initComponentForInputDetails()
        initTextChangeListener()
        initLateInitializerVariables()
        initBuildingRepository()
        initRoomRepository()
        clickListenerOnSearchButton()
        if (NetworkState.appIsConnectedToInternet(activity!!)) {
            getViewModelForBuildingList()
        } else {
            startActivityForResult(Intent(activity!!, NoInternetConnectionActivity::class.java), Constants.RES_CODE)
        }
    }

    private fun initComponentForInputDetails() {
        (activity?.application as BaseApplication).getmAppComponent()?.inject(this)
    }
    private fun initRoomRepository() {
        mConferenceRoomViewModel.setConferenceRoomRepo(mRoomRepo)
    }

    private fun initBuildingRepository() {
        mBuildingsViewModel.setBuildingRepository(mBuildingRepo)
    }

    private fun initLateInitializerVariables() {
        acct = GoogleSignIn.getLastSignedInAccount(activity)!!
        mInputDetailsForRoom.email = acct.email.toString()
        mBuildingsViewModel = ViewModelProviders.of(this).get(BuildingViewModel::class.java)
        mConferenceRoomViewModel = ViewModelProviders.of(this).get(ConferenceRoomViewModel::class.java)
        mSetDataFromActivity = GetIntentDataFromActvity()
    }

    /**
     * get the object of ViewModel using ViewModelProviders and observers the data from backend
     */
    private fun getViewModelForBuildingList() {
        mProgressBar.visibility = View.VISIBLE
        mBuildingsViewModel.getBuildingList()
    }

    private fun getViewModelForConferenceRoomList(mInputDetailsForRoom: InputDetailsForRoom) {
        mProgressBar.visibility = View.VISIBLE
        mConferenceRoomViewModel.getConferenceRoomList(mInputDetailsForRoom)
    }

    private fun setAdapter(mListOfRooms: List<RoomDetails>) {
        mProgressBar.visibility = View.GONE
        customAdapter =
            RoomAdapter(mListOfRooms as ArrayList<RoomDetails>, activity!!, object : RoomAdapter.ItemClickListener {
                override fun onItemClick(roomId: Int?, buidingId: Int?, roomName: String?, buildingName: String?) {
                    mSetIntentData.buildingName = mBuildingName
                    mSetIntentData.roomName = roomName
                    mSetIntentData.buildingId = mBuildingId
                    mSetIntentData.roomId = roomId
                    mSetIntentData.capacity = roomCapacityEditText.text.toString()
                    mSetIntentData.date = dateEditText.text.toString()
                    mSetIntentData.isPurposeVisible = checkbox_private.isChecked
                    goToSelectMeetingMembersActivity()
                }
            })
        mRecyclerView.adapter = customAdapter
        booking_scroll_view.post {
            scrollView.smoothScrollTo(0, mRecyclerView.top)
        }
    }

    /**
     * observe data from server
     */
    private fun observeData() {
        mBuildingsViewModel.returnMBuildingSuccess().observe(this, Observer {
            mProgressBar.visibility = View.GONE
                            setBuildingSpinner(it)

        })
        mBuildingsViewModel.returnMBuildingFailure().observe(this, Observer {
            mProgressBar.visibility = View.GONE
            if (it == Constants.INVALID_TOKEN) {
                ShowDialogForSessionExpired.showAlert(activity!!, UserBookingsDashboardActivity())
            } else {
                ShowToast.show(activity!!, it as Int)
            }
        })

        //positive response
        mConferenceRoomViewModel.returnSuccess().observe(this, Observer {
            checkForStatusOfRooms(it)
        })
        // Negative response
        mConferenceRoomViewModel.returnFailure().observe(this, Observer {
            mProgressBar.visibility = View.GONE
            if (it == Constants.INVALID_TOKEN) {
                ShowDialogForSessionExpired.showAlert(activity!!, UserBookingsDashboardActivity())
            } else {
                ShowToast.show(activity!!, it as Int)
            }
        })

        // positive response for suggested rooms
        mConferenceRoomViewModel.returnSuccessForSuggested().observe(this, Observer {
            horizontal_line_below_search_button.visibility = View.VISIBLE
            suggestions.visibility = View.VISIBLE
            when {
                it.isEmpty() -> suggestions.text = getString(R.string.no_rooms_available)
                else -> suggestions.text = getString(R.string.suggestion_rooms)
            }
            setAdapter(it)
        })
        // negative response for suggested rooms
        mConferenceRoomViewModel.returnFailureForSuggestedRooms().observe(this, Observer {
            mProgressBar.visibility = View.GONE
            if (it == Constants.INVALID_TOKEN) {
                ShowDialogForSessionExpired.showAlert(activity!!, UserBookingsDashboardActivity())
            } else {
                ShowToast.show(activity!!, it as Int)

            }
        })
    }
    private fun goToSelectMeetingMembersActivity() {
        mSetIntentData.fromTime = "${dateEditText.text} ${fromTimeEditText.text}"
        mSetIntentData.toTime = "${dateEditText.text} ${toTimeEditText.text}"
        mSetIntentData.purpose = purposeEditText.text.toString()
        intent = Intent(activity!!, SelectMeetingMembersActivity::class.java)
        intent.putExtra(Constants.EXTRA_INTENT_DATA, mSetIntentData)
        startActivity(intent)
    }

    private fun checkForStatusOfRooms(mListOfRooms: List<RoomDetails>?) {
        var count = 0
        for (room in mListOfRooms!!) {
            if (room.status == getString(R.string.unavailable)) {
                count++
            }
        }
        if (count == mListOfRooms.size) {
            if (NetworkState.appIsConnectedToInternet(activity!!)) {
                makeCallToApiForSuggestedRooms()
            } else {
                val i = Intent(activity!!, NoInternetConnectionActivity::class.java)
                startActivityForResult(i, Constants.RES_CODE3)
            }

        } else {
            setAdapter(mListOfRooms)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.RES_CODE && resultCode == Activity.RESULT_OK) {
            getViewModelForBuildingList()
        }
        if (requestCode == Constants.RES_CODE3 && resultCode == Activity.RESULT_OK) {
            makeCallToApiForSuggestedRooms()
        }
    }
    private fun makeCallToApiForSuggestedRooms() {
        mSuggestedRoomApiIsCallled = true
        mConferenceRoomViewModel.getSuggestedConferenceRoomList(mInputDetailsForRoom)
    }

    private fun setBuildingSpinner(mBuildingList: List<Building>) {
        val buildingNameList = mutableListOf<String>()
        val buildingIdList = mutableListOf<Int>()

        buildingNameList.add(getString(R.string.select_building))
        buildingIdList.add(-1)
        for (mBuilding in mBuildingList) {
            buildingNameList.add(mBuilding.buildingName!!)
            buildingIdList.add(mBuilding.buildingId!!.toInt())
        }
        val adapter = ArrayAdapter<String>(activity!!, R.layout.spinner_icon, R.id.spinner_text, buildingNameList)
        building_name_spinner.adapter = adapter
        building_name_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, position: Int, id: Long) {
                mBuildingName = buildingNameList[position]
                mBuildingId = buildingIdList[position]
                text_view_error_spinner_building.visibility = View.INVISIBLE
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {
                mBuildingName = getString(R.string.select_building)
            }
        }
    }

    /**
     * function will attach date and time picker to the input fields
     */
    private fun setPickerToEditText() {

        /**
         * set Time picker for the edittext fromtime
         */
        fromTimeEditText.setOnClickListener {
            DateAndTimePicker.getTimePickerDialog(activity!!, fromTimeEditText)
        }

        /**
         * set Time picker for the edittext toTime
         */
        toTimeEditText.setOnClickListener {
            DateAndTimePicker.getTimePickerDialog(activity!!, toTimeEditText)
        }
        /**
         * set Date picker for the edittext dateEditText
         */
        dateEditText.setOnClickListener {
            DateAndTimePicker.getDatePickerDialog(activity!!, dateEditText)
        }
    }


    /**
     * add text change listener for the from time edit text
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
     * add text change listener for the to time edit text
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
     * add text change listener for the date edit text
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
        purposeEditText.addTextChangedListener(object: TextWatcher {
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

    /**
     * add text change listener for the room name
     */
    private fun textChangeListenerOnRoomCapacity() {
        roomCapacityEditText.addTextChangedListener(object : TextWatcher {
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

    /**
     * validation for spinner
     */
    private fun validateRoomCapacity(): Boolean {
        return if (roomCapacityEditText.text.toString().trim().isEmpty()) {
            capacity_layout.error = getString(R.string.field_cant_be_empty)
            false
        } else {
            val input = roomCapacityEditText.text.toString().toLong()
            if (input <= 0 || input > Int.MAX_VALUE) {
                capacity_layout.error = getString(R.string.room_capacity_must_be_more_than_0)
                false
            } else {
                capacity_layout.error = null
                true
            }
        }
    }

    /**
     * validate all input fields
     */
    private fun validatePurpose(): Boolean {
        return if (purposeEditText.text.toString().trim().isEmpty()) {
            purpose_layout.error = getString(R.string.field_cant_be_empty)
            false
        }else {
            purpose_layout.error = null
            true
        }
    }

    /**
     * validations for all input fields for empty condition
     */
    private fun validateFromTime(): Boolean {
        val input = fromTimeEditText.text.toString().trim()
        return if (input.isEmpty()) {
            from_time_layout.error = getString(R.string.field_cant_be_empty)
            false
        } else {
            from_time_layout.error = null
            true
        }
    }

    private fun validateToTime(): Boolean {
        val input = toTimeEditText.text.toString().trim()
        return if (input.isEmpty()) {
            to_time_layout.error = getString(R.string.field_cant_be_empty)
            false
        } else {
            to_time_layout.error = null
            true
        }
    }

    private fun validateDate(): Boolean {
        val input = dateEditText.text.toString().trim()
        return if (input.isEmpty()) {
            date_layout.error = getString(R.string.field_cant_be_empty)
            false
        } else {
            date_layout.error = null
            true
        }
    }

    /**
     * validate building spinner
     */
    private fun validateBuildingSpinner(): Boolean {
        return if (mBuildingName == getString(R.string.select_building)) {
            text_view_error_spinner_building.visibility = View.VISIBLE
            false
        } else {
            text_view_error_spinner_building.visibility = View.INVISIBLE
            true
        }
    }

    /**
     * check validation for all input fields
     */
    private fun validate(): Boolean {

        if (!validateFromTime() or !validateToTime() or !validateDate() or !validateBuildingSpinner() or !validatePurpose() or !validateRoomCapacity()) {
            return false
        }
        return true
    }


    /**
     * function will apply some validation on data entered by user
     */
    private fun validationOnDataEnteredByUser() {

        /**
         * Validate each input field whether they are empty or not
         * If the field contains no values we show a toast to user saying that the value is invalid for particular field
         */
        if (validate()) {
            validateTime(fromTimeEditText.text.toString(), toTimeEditText.text.toString())
        }
    }


    private fun validateTime(startTime: String, endTime: String) {
        val minMilliseconds: Long = Constants.MIN_MEETING_DURATION
        /**
         * setting a alert dialog instance for the current context
         */
        try {

            /**
             * getting the values for time validation variables from method calculateTimeInMillis
             */
            val (elapsed, elapsed2) = ConvertTimeInMillis.calculateTimeInMilliseconds(
                startTime,
                endTime,
                date.text.toString()
            )

            /**
             * if the elapsed2 < 0 that means the from time is less than the current time. In that case
             * we restrict the user to move forword and show some message in alert that the time is not valid
             */
            when {
                elapsed2 < 0 -> {
                    val builder = GetAleretDialog.getDialog(
                        activity!!,
                        getString(R.string.invalid),
                        getString(R.string.invalid_fromtime)
                    )
                    builder.setPositiveButton(getString(R.string.ok)) { _, _ ->
                    }
                    val alertDialog = GetAleretDialog.showDialog(builder)
                    ColorOfDialogButton.setColorOfDialogButton(alertDialog)
                }

                /**
                 * if MIN_MILLISECONDS <= elapsed that means the meeting duration is more than 15 min
                 * if the above condition is not true than we show a message in alert that the meeting duration must be greater than 15 min
                 * if MAX_MILLISECONDS >= elapsed that means the meeting duration is less than 4hours
                 * if the above condition is not true than we show show a message in alert that the meeting duration must be less than 4hours
                 * if above both conditions are true than entered time is correct and user is allowed to go to the next activity
                 */
                minMilliseconds <= elapsed -> setDataForApiCallToFetchRoomDetails()
                else -> {
                    val builder = GetAleretDialog.getDialog(
                        activity!!,
                        getString(R.string.invalid),
                        getString(R.string.time_validation_message)
                    )
                    builder.setPositiveButton(getString(R.string.ok)) { _, _ ->
                    }
                    val alertBuilder = GetAleretDialog.showDialog(builder)
                    ColorOfDialogButton.setColorOfDialogButton(alertBuilder)

                }
            }
        } catch (e: Exception) {
            Toast.makeText(activity!!, getString(R.string.details_invalid), Toast.LENGTH_LONG).show()
        }
    }

    private fun setDataForApiCallToFetchRoomDetails() {
        mInputDetailsForRoom.capacity = roomCapacityEditText.text.toString().toInt()
        mInputDetailsForRoom.buildingId = mBuildingId
        mInputDetailsForRoom.fromTime = FormatTimeAccordingToZone.formatDateAsUTC(dateEditText.text.toString() + " " + fromTimeEditText.text.toString())
        mInputDetailsForRoom.toTime = FormatTimeAccordingToZone.formatDateAsUTC(dateEditText.text.toString() + " " + toTimeEditText.text.toString())
        if (NetworkState.appIsConnectedToInternet(activity!!)) {
            getViewModelForConferenceRoomList(mInputDetailsForRoom)
        } else {
            val i = Intent(activity!!, NoInternetConnectionActivity::class.java)
            startActivityForResult(i, Constants.RES_CODE2)
        }
    }
}