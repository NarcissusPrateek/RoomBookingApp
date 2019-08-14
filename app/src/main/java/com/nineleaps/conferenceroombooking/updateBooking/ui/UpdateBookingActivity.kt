@file:Suppress("DEPRECATION")

package com.nineleaps.conferenceroombooking.updateBooking.ui


import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextUtils
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.material.chip.Chip
import com.google.firebase.analytics.FirebaseAnalytics
import com.nineleaps.conferenceroombooking.BaseApplication
import com.nineleaps.conferenceroombooking.Helper.NetworkState
import com.nineleaps.conferenceroombooking.Helper.SelectMembers
import com.nineleaps.conferenceroombooking.R
import com.nineleaps.conferenceroombooking.booking.repository.EmployeeRepository
import com.nineleaps.conferenceroombooking.booking.ui.SelectMeetingMembersActivity
import com.nineleaps.conferenceroombooking.booking.viewModel.SelectMemberViewModel
import com.nineleaps.conferenceroombooking.checkConnection.NoInternetConnectionActivity
import com.nineleaps.conferenceroombooking.model.EmployeeList
import com.nineleaps.conferenceroombooking.model.GetIntentDataFromActvity
import com.nineleaps.conferenceroombooking.model.UpdateBooking
import com.nineleaps.conferenceroombooking.updateBooking.repository.UpdateBookingRepository
import com.nineleaps.conferenceroombooking.updateBooking.viewModel.UpdateBookingViewModel
import com.nineleaps.conferenceroombooking.utils.*
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_select_meeting_members.*
import kotlinx.android.synthetic.main.activity_select_meeting_members.chip_group
import kotlinx.android.synthetic.main.activity_select_meeting_members.select_member_recycler_view
import kotlinx.android.synthetic.main.activity_update_booking.*
import java.util.regex.Pattern
import javax.inject.Inject

class UpdateBookingActivity : AppCompatActivity() {

    @Inject
    lateinit var mUpdateBookingRepo: UpdateBookingRepository
    @Inject
    lateinit var mSelectEmployeeRepo: EmployeeRepository


    private lateinit var mUpdateBookingViewModel: UpdateBookingViewModel
    private var mUpdateBooking = UpdateBooking()
    lateinit var mFirebaseAnalytics: FirebaseAnalytics

    @BindView(R.id.Purpose)
    lateinit var purposeEditText: EditText

    @BindView(R.id.fromTime_update)
    lateinit var newFromTime: EditText

    @BindView(R.id.search_edit_text)
    lateinit var searchEditText: EditText

    @BindView(R.id.add_email)
    lateinit var addEmailButton: Button

    @BindView(R.id.toTime_update)
    lateinit var newToTime: EditText

    private lateinit var progressDialog: ProgressDialog

    lateinit var customAdapter: SelectMembers
    private lateinit var mSelectMemberViewModel: SelectMemberViewModel
    private lateinit var attendee: MutableList<String>
    private lateinit var acct: GoogleSignInAccount
    private val employeeList = ArrayList<EmployeeList>()
    private val selectedName = ArrayList<String>()
    private val selectedEmail = ArrayList<String>()
    private var count = 0

    private lateinit var mIntentDataFromActivity: GetIntentDataFromActvity


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_booking)
        ButterKnife.bind(this)
        mIntentDataFromActivity = getIntentData()
        init()
        observerData()
        setClickListenerOnEditText()
        searchEditText.onRightDrawableClickedd {
            it.text.clear()
        }
        setValuesInEditText(mIntentDataFromActivity)
        setEditTextPicker()
    }

    private fun initActionBar() {
        val actionBar = supportActionBar
        actionBar!!.title = Html.fromHtml("<font color=\"#FFFFFF\">" + getString(R.string.update) + "</font>")
    }
    @OnClick(R.id.add_email)
    fun checkSearchEditTextContent() {
        if (validateEmailFormat()) {
            val email = searchEditText.text.toString().trim()
            if (email == acct.email) {
                Toast.makeText(this, getString(R.string.already_part_of_meeting), Toast.LENGTH_SHORT).show()
                return
            }
            addChip(email, email)
        } else {
            Toasty.info(this, getString(R.string.wrong_email), Toasty.LENGTH_SHORT, true).show()
        }
    }


    // call function of ViewModel which will make API call
    private fun getViewModel() {
        progressDialog.show()
        mSelectMemberViewModel.getEmployeeList(acct.email!!)
    }

    private fun addDataToObjects(mIntentDataFromActivity: GetIntentDataFromActvity) {
        mUpdateBooking.bookingId = mIntentDataFromActivity.bookingId
        mUpdateBooking.newFromTime =
            FormatTimeAccordingToZone.formatDateAsUTC(mIntentDataFromActivity.date + " " + newFromTime.text.toString().trim())
        mUpdateBooking.newtotime =
            (FormatTimeAccordingToZone.formatDateAsUTC(mIntentDataFromActivity.date + " " + newToTime.text.toString().trim()))
        mUpdateBooking.purpose = purposeEditText.text.toString()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.RES_CODE && resultCode == Activity.RESULT_OK) {
            addDataToObjects(mIntentDataFromActivity)
            validationOnDataEnteredByUser()
        }
    }

    @OnClick(R.id.update)
    fun updateMeeting() {
        purposeEditText.onEditorAction(EditorInfo.IME_ACTION_DONE)
        if (NetworkState.appIsConnectedToInternet(this)) {
            var emailString = ""
            val size = selectedName.size
            selectedEmail.indices.forEach { index ->
                emailString += selectedEmail[index]
                if (index != (size - 1)) {
                    emailString += ","
                }
            }
            if(!emailString.isEmpty())
                attendee = emailString.split(",").toMutableList()
            else
                attendee = emptyList<String>().toMutableList()
            mUpdateBooking.cCmail = attendee
            addDataToObjects(mIntentDataFromActivity)
            validationOnDataEnteredByUser()
        } else {
            val i = Intent(this, NoInternetConnectionActivity::class.java)
            startActivityForResult(i, Constants.RES_CODE)
        }
    }

    private fun validationOnDataEnteredByUser() {
        /**
         * Validate each input field whether they are empty or not
         * If the field contains no values we show a toast to user saying that the value is invalid for particular field
         */
        if (validate()) {
            val minMilliseconds: Long = Constants.MIN_MEETING_DURATION
            val startTime = newFromTime.text.toString()
            val endTime = newToTime.text.toString()
            try {
                val (elapsed, elapsed2) = ConvertTimeInMillis.calculateTimeInMilliseconds(
                    startTime,
                    endTime,
                    mIntentDataFromActivity.date.toString()
                )
                when {
                    elapsed2 < 0 -> {
                        showMessageForInvalidTime(getString(R.string.invalid_fromtime))
                    }
                    minMilliseconds <= elapsed -> {
                        updateMeetingDetails()
                        updateBookingLogFirebaseAnalytics()
                    }
                    else -> {
                        showMessageForInvalidTime(getString(R.string.time_validation_message))
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(this@UpdateBookingActivity, getString(R.string.details_invalid), Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    /**
     * function will show a alert dialog for invalid input for time from user.
     */
    private fun showMessageForInvalidTime(message: String) {
        val builder = GetAleretDialog.getDialog(
            this,
            getString(R.string.invalid),
            message
        )
        builder.setPositiveButton(getString(R.string.ok)) { _, _ ->
        }
        GetAleretDialog.showDialog(builder)
    }

    private fun updateBookingLogFirebaseAnalytics() {
        val update = Bundle()
        mFirebaseAnalytics.logEvent(getString(R.string.update_log), update)
        mFirebaseAnalytics.setAnalyticsCollectionEnabled(true)
        mFirebaseAnalytics.setMinimumSessionDuration(5000)
        mFirebaseAnalytics.setSessionTimeoutDuration(1000000)
        mFirebaseAnalytics.setUserId(GoogleSignIn.getLastSignedInAccount(this)!!.email)
        mFirebaseAnalytics.setUserProperty(
            getString(R.string.Roll_Id),
            GetPreference.getRoleIdFromPreference().toString()
        )
    }


    private fun updateMeetingDetails() {
        progressDialog.show()
        mUpdateBookingViewModel.updateBookingDetails(mUpdateBooking)
    }

    /**
     * initialize all lateinit variables
     */
    fun init() {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        initActionBar()
        initComponentForUpdateBooking()
        textChangeListenerOnPurposeEditText()
        initLateInitializerVariables()
        initSelectEmployeeRepository()

        initUpdateBookingRepo()
        if (NetworkState.appIsConnectedToInternet(this)) {
            getViewModel()
        } else {
            val i = Intent(this, NoInternetConnectionActivity::class.java)
            startActivityForResult(i, Constants.RES_CODE)
        }
    }

    private fun initSelectEmployeeRepository() {
        mSelectMemberViewModel.setEmployeeListRepo(mSelectEmployeeRepo)
    }

    private fun initComponentForUpdateBooking() {
        (application as BaseApplication).getmAppComponent()?.inject(this)
    }

    private fun initUpdateBookingRepo() {
        mUpdateBookingViewModel.setUpdateBookingRepo(mUpdateBookingRepo)
    }


    private fun initLateInitializerVariables() {
        progressDialog = GetProgress.getProgressDialog(getString(R.string.progress_message_processing), this)
        mUpdateBookingViewModel = ViewModelProviders.of(this).get(UpdateBookingViewModel::class.java)
        mSelectMemberViewModel = ViewModelProviders.of(this).get(SelectMemberViewModel::class.java)
        acct = GoogleSignIn.getLastSignedInAccount(applicationContext)!!

    }

    /**
     * observing data for update booking
     */
    private fun observerData() {
        // positive response from server
        mSelectMemberViewModel.returnSuccessForEmployeeList().observe(this, Observer {
            progressDialog.dismiss()
            employeeList.clear()
            employeeList.addAll(it)
            customAdapter = SelectMembers(it, object : SelectMembers.ItemClickListener {
                override fun onBtnClick(name: String?, email: String?) {
                    addChip(name!!, email!!)
                }
            })
            select_member_recycler_view.adapter = customAdapter
        })
        // Negative response from server
        mSelectMemberViewModel.returnFailureForEmployeeList().observe(this, Observer {
            progressDialog.dismiss()
            if (it == Constants.UNPROCESSABLE || it == Constants.INVALID_TOKEN || it == Constants.FORBIDDEN) {
                ShowDialogForSessionExpired.showAlert(this, SelectMeetingMembersActivity())
            } else {
                ShowToast.show(this, it as Int)
                finish()
            }
        })


        mUpdateBookingViewModel.returnBookingUpdated().observe(this, Observer {
            progressDialog.dismiss()
            Toasty.success(this, getString(R.string.booking_updated), Toast.LENGTH_SHORT, true).show()
            finish()
        })
        mUpdateBookingViewModel.returnUpdateFailed().observe(this, Observer {
            progressDialog.dismiss()
            if (it == Constants.UNPROCESSABLE || it == Constants.INVALID_TOKEN || it == Constants.FORBIDDEN) {
                ShowDialogForSessionExpired.showAlert(this, UpdateBookingActivity())
            } else {
                ShowToast.show(this, it as Int)
            }
        })
    }

    private fun validate(): Boolean {

        if (TextUtils.isEmpty(newFromTime.text.trim())) {
            Toast.makeText(applicationContext, getString(R.string.invalid_from_time), Toast.LENGTH_SHORT).show()
            return false
        } else if (TextUtils.isEmpty(newToTime.text.trim())) {
            Toast.makeText(applicationContext, getString(R.string.invalid_to_time), Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun setEditTextPicker() {
        newFromTime.setOnClickListener {
            DateAndTimePicker.getTimePickerDialog(this, newFromTime)
        }

        /**
         * set Time picker for the edittext toTime
         */
        newToTime.setOnClickListener {
            DateAndTimePicker.getTimePickerDialog(this, newToTime)
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun setValuesInEditText(mIntentDataFromActivity: GetIntentDataFromActvity) {
        purposeEditText.text = mIntentDataFromActivity.purpose!!.toEditable()
        newFromTime.text = mIntentDataFromActivity.fromTime!!.toEditable()
        newToTime.text = mIntentDataFromActivity.toTime!!.toEditable()
        if (mIntentDataFromActivity.cCMail != null && mIntentDataFromActivity.name != null) {
            val attendeeList = mIntentDataFromActivity.name!!.zip(mIntentDataFromActivity.cCMail!!)
            for (item in attendeeList) {
                addChip(item.first, item.second)
            }
        }
    }

    private fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)


    fun addChip(name: String, email: String) {
        if (!selectedEmail.contains(email)) {
            val chip = Chip(this)
            chip.text = name
            chip.isCloseIconVisible = true
            chip_group.addView(chip)
            chip.setOnCloseIconClickListener {
                selectedName.remove(name)
                selectedEmail.remove(email)
                chip_group.removeView(chip)
                count--
            }
            selectedName.add(name)
            selectedEmail.add(email)
            count++
        } else {
            Toast.makeText(this, getString(R.string.already_selected), Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * validate all input fields
     */
    private fun validatePurpose(): Boolean {
        return if (purposeEditText.text.toString().trim().isEmpty()) {
            layout6.error = getString(R.string.field_cant_be_empty)
            false
        } else {
            layout6.error = null
            true
        }
    }

    private fun getIntentData(): GetIntentDataFromActvity {
        return intent.extras!!.get(Constants.EXTRA_INTENT_DATA) as GetIntentDataFromActvity
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun EditText.onRightDrawableClickedd(onClicked: (view: EditText) -> Unit) {
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

    /**
     * take input from edit text and set addTextChangedListener
     */
    private fun setClickListenerOnEditText() {
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                /**
                 * Nothing Here
                 */
            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                /**
                 * Nothing here
                 */
                when {
                    charSequence.isEmpty() -> searchEditText.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.ic_search,
                        0
                    )
                    else -> searchEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_clear, 0)
                }
            }

            override fun afterTextChanged(editable: Editable) {
                filter(editable.toString())
            }
        })
    }

    /**
     * add text change listener for the purposeEditText edit text
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

    /**
     * filter matched data from employee list and set updated list to adapter
     */
    fun filter(text: String) {
        val filterName = java.util.ArrayList<EmployeeList>()
        for (s in employeeList) {
            if (s.name!!.toLowerCase().contains(text.toLowerCase()) || s.email!!.toLowerCase().contains(text.toLowerCase())) {
                filterName.add(s)
            }
        }
        customAdapter.filterList(filterName)
        // no items present in recyclerview than give option for add other emails
        when {
            customAdapter.itemCount == 0 -> addEmailButton.visibility = View.VISIBLE
            else -> addEmailButton.visibility = View.GONE
        }
    }

    // function checks for correct email format
    private fun validateEmailFormat(): Boolean {
        val email = searchEditText.text.toString().trim()
        val pat = Pattern.compile(Constants.MATCHER)
        return pat.matcher(email).matches()
    }
}
