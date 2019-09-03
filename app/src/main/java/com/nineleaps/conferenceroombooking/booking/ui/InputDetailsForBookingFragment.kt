package com.nineleaps.conferenceroombooking.booking.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
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
import com.nineleaps.conferenceroombooking.model.GetIntentDataFromActvity
import com.nineleaps.conferenceroombooking.model.RoomDetails
import com.nineleaps.conferenceroombooking.utils.*
import kotlinx.android.synthetic.main.activity_booking_input.*
import kotlinx.android.synthetic.main.activity_booking_input_from_user.booking_scroll_view
import kotlinx.android.synthetic.main.activity_booking_input_from_user.capacity_layout
import kotlinx.android.synthetic.main.activity_booking_input_from_user.date
import kotlinx.android.synthetic.main.activity_booking_input_from_user.date_layout
import kotlinx.android.synthetic.main.activity_booking_input_from_user.from_time_layout
import kotlinx.android.synthetic.main.activity_booking_input_from_user.search_room
import kotlinx.android.synthetic.main.activity_booking_input_from_user.suggestions
import javax.inject.Inject


@Suppress("DEPRECATION")
class InputDetailsForBookingFragment : Fragment() {

    @Inject
    lateinit var mRoomRepo: ConferenceRoomRepository

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

    @BindView(R.id.recycler_view_room_list)
    lateinit var mRecyclerView: RecyclerView

    @BindView(R.id.filter_edit_text)
    lateinit var filterEditText: EditText

    @BindView(R.id.booking_scroll_view)
    lateinit var scrollView: NestedScrollView

    lateinit var intent: Intent

    private val roomList = ArrayList<RoomDetails>()

    private lateinit var progressDialog: ProgressDialog

    private lateinit var mSetDataFromActivity: GetIntentDataFromActvity

    private lateinit var mConferenceRoomViewModel: ConferenceRoomViewModel

    private var mInputDetailsForRoom = InputDetailsForRoom()

    private lateinit var acct: GoogleSignInAccount

    var mSetIntentData = GetIntentDataFromActvity()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.activity_booking_input, container, false)
        ButterKnife.bind(this, view)
        setClickListenerOnEditText()
        softKeyboard()
        filterEditText.onRightDrawableClicked {
            it.text.clear()
        }
        return view
    }

    private fun softKeyboard() {
        HideSoftKeyboard.hideKeyboard(activity!!)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        observeData()
    }

    private fun clickListenerOnSearchButton() {
        if (NetworkState.appIsConnectedToInternet(activity!!)) {

            search_room.setOnClickListener {
                HideSoftKeyboard.hideKeyboard(activity!!)
                filterSearch()
                suggestions.visibility = View.GONE
                roomCapacityEditText.onEditorAction(EditorInfo.IME_ACTION_DONE)
                validationOnDataEnteredByUser()
            }

        } else {
            val i = Intent(activity!!, NoInternetConnectionActivity::class.java)
            startActivityForResult(i, Constants.RES_CODE)
        }
    }

    private fun filterSearch() {
        setClickListenerOnEditText()
    }

    private fun initTextChangeListener() {
        textChangeListenerOnDateEditText()
        textChangeListenerOnFromTimeEditText()
        textChangeListenerOnToTimeEditText()
        textChangeListenerOnRoomCapacity()
    }

    private fun init() {
        setPickerToEditText()
        initComponentForInputDetails()
        initTextChangeListener()
        initLateInitializerVariables()
        initRoomRepository()
        clickListenerOnSearchButton()
        initRecyclerView()
    }

    private fun initComponentForInputDetails() {
        (activity?.application as BaseApplication).getmAppComponent()?.inject(this)
    }

    private fun initRoomRepository() {
        mConferenceRoomViewModel.setConferenceRoomRepo(mRoomRepo)
    }

    private fun initLateInitializerVariables() {
        acct = GoogleSignIn.getLastSignedInAccount(activity)!!
        mInputDetailsForRoom.email = acct.email.toString()
        progressDialog = GetProgress.getProgressDialog(getString(R.string.searching_processing), activity!!)

        mConferenceRoomViewModel = ViewModelProviders.of(this).get(ConferenceRoomViewModel::class.java)
        mSetDataFromActivity = GetIntentDataFromActvity()
    }

    private fun getViewModelForConferenceRoomList(mInputDetailsForRoom: InputDetailsForRoom) {
        progressDialog.show()
        mConferenceRoomViewModel.getConferenceRoomList(mInputDetailsForRoom)
    }

    fun initRecyclerView() {
        customAdapter =
            RoomAdapter(roomList, activity!!, object : RoomAdapter.ItemClickListener {
                override fun onItemClick(
                    roomId: Int?,
                    buidingId: Int?,
                    roomName: String?,
                    buildingName: String?
                ) {
                    mSetIntentData.buildingName = buildingName
                    mSetIntentData.roomName = roomName
                    mSetIntentData.buildingId = buidingId
                    mSetIntentData.roomId = roomId
                    mSetIntentData.capacity = roomCapacityEditText.text.toString()
                    mSetIntentData.date = dateEditText.text.toString()
                    mSetIntentData.isPurposeVisible = true
                    goToSelectMeetingMembersActivity()
                }
            },
                object : RoomAdapter.MoreAminitiesListner {
                    override fun moreAmenities(position: Int) {
                        showDialogForMoreAminities(roomList[position].amenities!!, position)

                    }

                })
        mRecyclerView.adapter = customAdapter
    }

    private fun showDialogForMoreAminities(items: HashMap<Int, String>, position: Int) {
        val arrayListOfItems = ArrayList<String>()

        for (item in items) {
            arrayListOfItems.add(item.value)
        }
        val listItems = arrayOfNulls<String>(arrayListOfItems.size)
        arrayListOfItems.toArray(listItems)
        val builder = AlertDialog.Builder(activity)
        builder.setItems(
            listItems
        ) { _, _ -> }
        val mDialog = builder.create()
        mDialog.show()
    }

    /**
     * observe data from server
     */
    private fun observeData() {
        //positive response
        mConferenceRoomViewModel.returnSuccess().observe(this, Observer {
            progressDialog.dismiss()
            if (it.isEmpty()) {
                roomList.clear()
                customAdapter.notifyDataSetChanged()
                horizontal_line_below_search_button.visibility = View.VISIBLE
                suggestions.visibility = View.VISIBLE
                suggestions.text = getString(R.string.no_rooms_available)
                filter_edit_text.visibility = View.GONE
            } else {
                roomList.clear()
                progressDialog.dismiss()

                filter_edit_text.visibility = View.VISIBLE
                roomList.addAll(it)
                customAdapter.notifyDataSetChanged()
                booking_scroll_view.post {
                    scrollView.smoothScrollTo(0, filterEditText.bottom)

                }
            }
        })
        // Negative response
        mConferenceRoomViewModel.returnFailure().observe(this, Observer {
            progressDialog.dismiss()
            if (it == Constants.UNPROCESSABLE || it == Constants.INVALID_TOKEN || it == Constants.FORBIDDEN) {
                ShowDialogForSessionExpired.showAlert(activity!!, UserBookingsDashboardActivity())
            } else {
                ShowToast.show(activity!!, it as Int)
            }
        })
    }

    /**
     *
     */
    private fun goToSelectMeetingMembersActivity() {
        mSetIntentData.fromTime = "${dateEditText.text} ${fromTimeEditText.text}"
        mSetIntentData.toTime = "${dateEditText.text} ${toTimeEditText.text}"
        intent = Intent(activity!!, SelectMeetingMembersActivity::class.java)
        intent.putExtra(Constants.EXTRA_INTENT_DATA, mSetIntentData)
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.RES_CODE && resultCode == Activity.RESULT_OK) {
            getViewModelForConferenceRoomList(mInputDetailsForRoom)
        } else {

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
     * check validation for all input fields
     */
    private fun validate(): Boolean {
        if (!validateFromTime() or !validateToTime() or !validateDate() or !validateRoomCapacity()) {
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
        mInputDetailsForRoom.fromTime =
            FormatTimeAccordingToZone.formatDateAsUTC(dateEditText.text.toString() + " " + fromTimeEditText.text.toString())
        mInputDetailsForRoom.toTime =
            FormatTimeAccordingToZone.formatDateAsUTC(dateEditText.text.toString() + " " + toTimeEditText.text.toString())
        if (NetworkState.appIsConnectedToInternet(activity!!)) {
            getViewModelForConferenceRoomList(mInputDetailsForRoom)
        } else {
            val i = Intent(activity!!, NoInternetConnectionActivity::class.java)
            startActivityForResult(i, Constants.RES_CODE)
        }
    }

    /**
     * take input from edit text and set addTextChangedListener
     */
    private fun setClickListenerOnEditText() {
        filterEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                /**
                 * Nothing Here
                 */
            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                when {
                    charSequence.isEmpty() -> filterEditText.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.ic_search,
                        0
                    )
                    else -> filterEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_clear, 0)
                }
            }

            override fun afterTextChanged(editable: Editable) {
                filter(editable.toString())
            }
        })
    }

    /**
     * filter matched data from employee list and set updated list to adapter
     */
    fun filter(text: String) {
        val filterName = java.util.ArrayList<RoomDetails>()
        for (s in roomList) {
            if (s.roomName!!.toLowerCase().contains(text.toLowerCase()) || s.buildingName!!.toLowerCase().contains(text.toLowerCase())) {
                filterName.add(s)
            }
        }
        customAdapter.filterList(filterName)
        // no items present in recyclerview than give option for add other emails
        when {
            customAdapter.itemCount == 0 -> suggestions.visibility = View.VISIBLE
            else -> suggestions.visibility = View.GONE
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun EditText.onRightDrawableClicked(onClicked: (view: EditText) -> Unit) {
        this.setOnTouchListener { v, event ->
            var hasConsumed = false
            when {
                v is EditText && event.x >= v.width - v.totalPaddingRight -> {
                    if (event.action == MotionEvent.ACTION_UP) {
                        onClicked(this)
                    }
                    hasConsumed = true
                }
            }
            hasConsumed
        }
    }

}