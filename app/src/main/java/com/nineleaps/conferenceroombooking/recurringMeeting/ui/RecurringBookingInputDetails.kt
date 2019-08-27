package com.nineleaps.conferenceroombooking.recurringMeeting.ui

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.Html.fromHtml
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import ca.antonious.materialdaypicker.MaterialDayPicker
import com.example.conferenceroomapp.model.ManagerConference
import com.nineleaps.conferenceroombooking.BaseApplication
import com.nineleaps.conferenceroombooking.Helper.NetworkState
import com.nineleaps.conferenceroombooking.Helper.RoomAdapter
import com.nineleaps.conferenceroombooking.R
import com.nineleaps.conferenceroombooking.ViewModel.ManagerConferenceRoomViewModel
import com.nineleaps.conferenceroombooking.checkConnection.NoInternetConnectionActivity
import com.nineleaps.conferenceroombooking.manageBuildings.repository.BuildingsRepository
import com.nineleaps.conferenceroombooking.manageBuildings.viewModel.BuildingViewModel
import com.nineleaps.conferenceroombooking.model.Building
import com.nineleaps.conferenceroombooking.model.GetIntentDataFromActvity
import com.nineleaps.conferenceroombooking.model.RoomDetails
import com.nineleaps.conferenceroombooking.recurringMeeting.repository.ManagerConferenceRoomRepository
import com.nineleaps.conferenceroombooking.utils.*
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_project_manager_input.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

@Suppress("NAME_SHADOWING", "DEPRECATION")
class RecurringBookingInputDetails : AppCompatActivity() {

    @Inject
    lateinit var mManagerRoomRepo: ManagerConferenceRoomRepository

    @Inject
    lateinit var mBuildingRepo: BuildingsRepository

    @BindView(R.id.project_manager_progress_bar)
    lateinit var progressBar: ProgressBar

    @BindView(R.id.fromTime_manager)
    lateinit var fromTimeEditText: EditText

    @BindView(R.id.toTime_manager)
    lateinit var toTimeEditText: EditText

    @BindView(R.id.to_date_manager)
    lateinit var dateToEditText: EditText

    @BindView(R.id.date_manager)
    lateinit var dateFromEditText: EditText

    @BindView(R.id.manager_recycler_view_room_list)
    lateinit var mRecyclerView: RecyclerView


    @BindView(R.id.manager_room_capacity)
    lateinit var roomCapacityEditText: EditText

    private lateinit var mManagerConferecneRoomViewModel: ManagerConferenceRoomViewModel

    private lateinit var customAdapter: RoomAdapter

    private lateinit var mIntentDataFromActivity: GetIntentDataFromActvity

    private lateinit var mBuildingsViewModel: BuildingViewModel

    private var listOfDays = ArrayList<String>()

    private var dataList = ArrayList<String>()

    private var fromTimeList = ArrayList<String>()

    private var toTimeList = ArrayList<String>()

    var mRoom = ManagerConference()

    private var dateFlag = 0

    var mSetIntentData = GetIntentDataFromActvity()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project_manager_input)
        ButterKnife.bind(this)
        init()
        observerData()
    }

    private fun init() {
        initActionBar()
        initComponentForManagerBooking()
        initLateInitVariables()
        initBuildingRepo()
        initRoomRepo()
        setPickerToEditTexts()
        setTextChangeListener()
        hideSoftKeyBoard()
    }

    private fun hideSoftKeyBoard() {
        HideSoftKeyboard.setUpUI(findViewById(R.id.reurring_booking_scroll_view), this)
        HideSoftKeyboard.setUpUI(findViewById(R.id.manager_search_room), this)
    }

    private fun initComponentForManagerBooking() {
        (application as BaseApplication).getmAppComponent()?.inject(this)
    }

    private fun initRoomRepo() {
        mManagerConferecneRoomViewModel.setManagerConferenceRoomRepo(mManagerRoomRepo)
    }

    private fun initBuildingRepo() {
        mBuildingsViewModel.setBuildingRepository(mBuildingRepo)
    }


    //set action bar properties
    private fun initActionBar() {
        val actionBar = supportActionBar
        actionBar!!.title = fromHtml("<font color=\"#FFFFFF\">" + getString(R.string.Booking_Details) + "</font>")

    }

    // initialize late init properties
    private fun initLateInitVariables() {
        mBuildingsViewModel = ViewModelProviders.of(this)
            .get(BuildingViewModel::class.java)
        mManagerConferecneRoomViewModel = ViewModelProviders.of(this).get(ManagerConferenceRoomViewModel::class.java)
        mIntentDataFromActivity = GetIntentDataFromActvity()

    }

    //set textChangeListener for edit texts
    private fun setTextChangeListener() {
        textChangeListenerOnFromTimeEditText()
        textChangeListenerOnToTimeEditText()
        textChangeListenerOnFromDateEditText()
        textChangeListenerOnToDateEditText()
        textChangeListenerOnRoomCapacity()
        selectChangeListenerOnDaySlector()
    }

    @OnClick(R.id.manager_search_room)
    fun makeCallForRooms() {
        if (validate()) {
            manager_suggestions.visibility = View.GONE
            applyValidationOnDateAndTime()
        }
    }


    /**
     * get all date for each day selected by user in between from date and To date
     */
    private fun getDateAccordingToDay(
        start: String,
        end: String,
        fromDate: String,
        toDate: String,
        listOfDays: ArrayList<String>
    ) {
        dataList.clear()
        try {
            val simpleDateFormat = SimpleDateFormat(getString(R.string.yyyy_mm_dd), Locale.US)
            val d1 = simpleDateFormat.parse(fromDate)
            val d2 = simpleDateFormat.parse(toDate)
            val c1 = Calendar.getInstance()
            val c2 = Calendar.getInstance()
            c1.time = d1
            c2.time = d2
            while (c2.after(c1)) {
                if (listOfDays.contains(
                        c1.getDisplayName(
                            Calendar.DAY_OF_WEEK,
                            Calendar.LONG_FORMAT,
                            Locale.US
                        ).toUpperCase()
                    )
                ) {
                    dataList.add(simpleDateFormat.format(c1.time).toString())
                }
                c1.add(Calendar.DATE, 1)
            }
            if (listOfDays.contains(
                    c2.getDisplayName(
                        Calendar.DAY_OF_WEEK,
                        Calendar.LONG_FORMAT,
                        Locale.US
                    ).toUpperCase()
                )
            ) {
                dataList.add(simpleDateFormat.format(c1.time).toString())
            }
            getLists(start, end)
        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
        }
    }

    /**
     * this function returns all fromdate list and todate list
     */
    private fun getLists(start: String, end: String) {
        fromTimeList.clear()
        toTimeList.clear()
        for (item in dataList) {
            fromTimeList.add(FormatTimeAccordingToZone.formatDateAsUTC("$item $start"))
            toTimeList.add(FormatTimeAccordingToZone.formatDateAsUTC("$item $end"))
        }
    }

    //observe data from view model
    private fun observerData() {
        //positive response
        mManagerConferecneRoomViewModel.returnSuccess().observe(this, Observer {
            checkForStatusOfRooms(it)
        })
        // Negative response
        mManagerConferecneRoomViewModel.returnFailure().observe(this, Observer {
            progressBar.visibility = View.GONE
            if ( it == Constants.INVALID_TOKEN || it == Constants.FORBIDDEN) {
                ShowDialogForSessionExpired.showAlert(this, RecurringBookingInputDetails())
            }else{
                ShowToast.show(this, it as Int)

            }
        })
    }

    private fun checkForStatusOfRooms(mListOfRooms: List<RoomDetails>?) {
        setAdapter(mListOfRooms!!)
    }

    private fun setAdapter(mListOfRooms: List<RoomDetails>) {
        progressBar.visibility = View.GONE
        customAdapter =
            RoomAdapter(mListOfRooms as ArrayList<RoomDetails>, this, object : RoomAdapter.ItemClickListener {
                override fun onItemClick(roomId: Int?, buidingId: Int?, roomName: String?, buildingName: String?) {
                    mSetIntentData.fromTime = fromTimeEditText.text.toString()
                    mSetIntentData.toTime = toTimeEditText.text.toString()
                    mSetIntentData.date = dateFromEditText.text.toString()
                    mSetIntentData.toDate = dateToEditText.text.toString()
                    mSetIntentData.buildingName = buildingName
                    mSetIntentData.roomName = roomName
                    mSetIntentData.buildingId = buidingId
                    mSetIntentData.roomId = roomId
                    mSetIntentData.fromTimeList.clear()
                    mSetIntentData.toTimeList.clear()
                    mSetIntentData.fromTimeList.addAll(fromTimeList)
                    mSetIntentData.toTimeList.addAll(toTimeList)
                    mSetIntentData.capacity = roomCapacityEditText.text.toString()
                    goToSelectMeetingMembersActivity()
                }
            }, object : RoomAdapter.MoreAminitiesListner {
                override fun moreAmenities(position: Int) {
                    showDialogForMoreAminities(mListOfRooms[position].amenities!!)

                }

            })
        mRecyclerView.adapter = customAdapter

    }

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

    private fun goToSelectMeetingMembersActivity() {
        val intent = Intent(this@RecurringBookingInputDetails, ManagerSelectMeetingMembers::class.java)
        intent.putExtra(Constants.EXTRA_INTENT_DATA, mSetIntentData)
        startActivity(intent)
    }

    /**
     *  add text change listener for the room name
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
            manager_capacity_layout.error = getString(R.string.field_cant_be_empty)
            false
        } else {
            val input = roomCapacityEditText.text.toString().toLong()
            if (input <= 0 || input > Int.MAX_VALUE) {
                manager_capacity_layout.error = getString(R.string.room_capacity_must_be_more_than_0)
                false
            } else {
                manager_capacity_layout.error = null
                true
            }
        }
    }

    /**
     * set date and time pickers to edittext fields
     */
    private fun setPickerToEditTexts() {

        /**
         * set Time picker for the editText fromTimeEditText
         */
        fromTimeEditText.setOnClickListener {
            DateAndTimePicker.getTimePickerDialog(this, fromTimeEditText)
        }
        /**
         * set Time picker for the EditText toTimeEditText
         */
        toTimeEditText.setOnClickListener {
            DateAndTimePicker.getTimePickerDialog(this, toTimeEditText)
        }
        /**
         * set Date picker for the EditText dateEditText
         */
        dateFromEditText.setOnClickListener {
            dateFlag = 1
            DateAndTimePicker.getDatePickerDialog(this, dateFromEditText)
        }
        /**
         * set Date picker for the EditText dateToEditText
         */
        dateToEditText.setOnClickListener {
            if (validateFromDateForToDate())
                DateAndTimePicker.getDatePickerDialogForOneMonth(this, dateToEditText, dateFromEditText.text.toString())
        }
    }

    /**
     * validate from time for non empty condition
     */
    private fun validateFromTime(): Boolean {
        val input = fromTimeEditText.text.toString().trim()
        return if (input.isEmpty()) {
            manager_from_time_layout.error = getString(R.string.field_cant_be_empty)
            false
        } else {

            manager_from_time_layout.error = null
            true
        }
    }


    /**
     * validate to-time for non empty condition
     */
    private fun validateToTime(): Boolean {
        val input = toTimeEditText.text.toString().trim()
        return if (input.isEmpty()) {
            manager_to_time_layout.error = getString(R.string.field_cant_be_empty)
            false
        } else {
            manager_to_time_layout.error = null
            true
        }
    }

    /**
     * validate to-date for non empty condition
     */
    private fun validateFromDate(): Boolean {
        val input = dateFromEditText.text.toString().trim()
        return if (input.isEmpty()) {
            manager_from_date_layout.error = getString(R.string.field_cant_be_empty)
            false
        } else {
            manager_from_date_layout.error = null
            true
        }
    }

    private fun validateFromDateForToDate(): Boolean {
        val input = dateFromEditText.text.toString().trim()
        return if (input.isEmpty()) {
            manager_to_date_layout.error = "Enter Start Date"
            false
        } else {
            manager_to_date_layout.error = null
            true
        }
    }

    /**
     * validate from-date for non empty condition
     */
    private fun validateToDate(): Boolean {
        val input = dateToEditText.text.toString().trim()
        return if (input.isEmpty()) {
            manager_to_date_layout.error = getString(R.string.field_cant_be_empty)
            false
        } else {
            manager_to_date_layout.error = null
            true
        }
    }

    /**
     * validate day selector for non empty condition
     */
    private fun validateSelectedDayList(): Boolean {
        if (day_picker.selectedDays.isEmpty()) {
            error_day_selector_text_view.visibility = View.VISIBLE
            return false
        }
        error_day_selector_text_view.visibility = View.INVISIBLE
        return true
    }


    /**
     * this function ensures that user entered values for all editable fields
     */
    private fun validate(): Boolean {
        if (!validateFromTime() or !validateToTime() or !validateFromDate() or !validateToDate() or !validateSelectedDayList() or !validateRoomCapacity()) {
            return false
        }
        return true
    }

    /**
     * if MIN_MILIISECONDS <= elapsed that means the meeting duration is more than 15 min
     *  if the above condition is not true than we show a message in alert that the meeting duration must be greater than 15 min
     *  if MAX_MILLISECONDS >= elapsed that means the meeting duration is less than 4hours
     *  if the above condition is not true than we show show a message in alert that the meeting duration must be less than 4hours
     *  if above both conditions are true than entered time is correct and user is allowed to go to the next actvity
     */
    private fun applyValidationOnDateAndTime() {
        val minMilliseconds: Long = Constants.MIN_MEETING_DURATION
        /**
         * Get the start and end time of meeting from the input fields
         */

        val startTime = fromTimeEditText.text.toString()
        val endTime = toTimeEditText.text.toString()

        /**
         * setting a alert dialog instance for the current context
         */
        val builder = AlertDialog.Builder(this@RecurringBookingInputDetails)
        builder.setTitle("Check...")
        try {

            /**
             *  getting the values for time validation variables from method calculateTimeInMillis
             */
            val (elapsed, elapsed2) = ConvertTimeInMillis.calculateTimeInMilliseconds(
                startTime,
                endTime,
                dateFromEditText.text.toString()
            )
            /**
             * if the elapsed2 < 0 that means the from time is less than the current time. In that case
             * we restrict the user to move forword and show some message in alert that the time is not valid
             */

            if (elapsed2 < 0) {
                val builder =
                    GetAleretDialog.getDialog(this, getString(R.string.invalid), getString(R.string.invalid_fromtime))
                builder.setPositiveButton(getString(R.string.ok)) { _, _ ->
                }
                GetAleretDialog.showDialog(builder)
            } else if (minMilliseconds <= elapsed) {
                if (ConvertTimeInMillis.calculateDateInMilliseconds(
                        dateFromEditText.text.toString(),
                        dateToEditText.text.toString()
                    )
                ) {
                    goToBuildingsActivity()
                } else {
                    Toast.makeText(this, getString(R.string.invalid_fromDate_or_toDate), Toast.LENGTH_SHORT).show()
                }

            } else {
                val builder = GetAleretDialog.getDialog(
                    this,
                    getString(R.string.invalid),
                    getString(R.string.time_validation_message)
                )

                builder.setPositiveButton(getString(R.string.ok)) { _, _ ->
                }
                val dialog = GetAleretDialog.showDialog(builder)
                ColorOfDialogButton.setColorOfDialogButton(dialog)
            }
        } catch (e: Exception) {
            Toast.makeText(this@RecurringBookingInputDetails, getString(R.string.invalid_details), Toast.LENGTH_LONG)
                .show()
        }
    }

    /**
     * set data to the object which is used to send data from this activity to another activity and pass the intent
     */
    private fun goToBuildingsActivity() {
        mIntentDataFromActivity.listOfDays.clear()
        getListOfSelectedDays()
        getDateAccordingToDay(
            fromTimeEditText.text.toString(),
            toTimeEditText.text.toString(),
            dateFromEditText.text.toString(),
            dateToEditText.text.toString(),
            listOfDays
        )
        mRoom.fromTime?.clear()
        mRoom.toTime?.clear()
        if (fromTimeList.isEmpty() || toTimeList.isEmpty())
            getLists(fromTimeEditText.text.toString(),
                toTimeEditText.text.toString())
        mRoom.fromTime = fromTimeList
        mRoom.toTime = toTimeList
        mRoom.capacity = roomCapacityEditText.text.toString().toInt()
        if (dataList.isEmpty()) {
            Toast.makeText(this, getString(R.string.messgae_for_day_selector_when_nothing_selected), Toast.LENGTH_SHORT)
                .show()
        } else {
            getConferenceRoomViewModel()
        }
    }

    private fun getConferenceRoomViewModel() {
        if (NetworkState.appIsConnectedToInternet(this)) {
            progressBar.visibility = View.VISIBLE
            mManagerConferecneRoomViewModel.getConferenceRoomList(mRoom)
        } else {
            val i = Intent(this@RecurringBookingInputDetails, NoInternetConnectionActivity::class.java)
            startActivityForResult(i, Constants.RES_CODE2)
        }
    }

    /**
     * get all the selected days and add all days to another list listOfDays
     */
    private fun getListOfSelectedDays() {
        listOfDays.clear()
        for (day in day_picker.selectedDays) {
            listOfDays.add("$day")
        }
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
    private fun textChangeListenerOnFromDateEditText() {
        dateFromEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // nothing here
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // nothing here
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateFromDate()

            }
        })
    }

    /**
     * add text change listener for the end date edit text
     */
    private fun textChangeListenerOnToDateEditText() {
        dateToEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // nothing here
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // nothing here
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateToDate()
            }
        })
    }

    private fun selectChangeListenerOnDaySlector() {
        day_picker.setDaySelectionChangedListener {
            if (it.isEmpty())
                error_day_selector_text_view.visibility = View.VISIBLE
            else
                error_day_selector_text_view.visibility = View.GONE
        }

    }

}


