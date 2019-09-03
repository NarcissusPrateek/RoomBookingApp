package com.nineleaps.conferenceroombooking.booking.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import butterknife.BindView
import butterknife.OnClick
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.material.chip.Chip
import com.google.firebase.analytics.FirebaseAnalytics
import com.nineleaps.conferenceroombooking.BaseActivity
import com.nineleaps.conferenceroombooking.BaseApplication
import com.nineleaps.conferenceroombooking.Helper.NetworkState
import com.nineleaps.conferenceroombooking.Helper.SelectMembers
import com.nineleaps.conferenceroombooking.R
import com.nineleaps.conferenceroombooking.booking.repository.BookingRepository
import com.nineleaps.conferenceroombooking.booking.repository.EmployeeRepository
import com.nineleaps.conferenceroombooking.booking.viewModel.BookingViewModel
import com.nineleaps.conferenceroombooking.booking.viewModel.SelectMemberViewModel
import com.nineleaps.conferenceroombooking.bookingDashboard.ui.UserBookingsDashboardActivity
import com.nineleaps.conferenceroombooking.checkConnection.NoInternetConnectionActivity
import com.nineleaps.conferenceroombooking.model.Booking
import com.nineleaps.conferenceroombooking.model.EmployeeList
import com.nineleaps.conferenceroombooking.model.GetIntentDataFromActvity
import com.nineleaps.conferenceroombooking.utils.*
import com.orhanobut.hawk.Hawk
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_select_meeting_members.*
import java.util.regex.Pattern
import javax.inject.Inject


class SelectMeetingMembersActivity : BaseActivity() {

    /**
     * Declaring Global variables and binned view for using butter knife
     */
    @Inject
    lateinit var mSelectEmployeeRepo: EmployeeRepository

    @Inject
    lateinit var mBookingRepo: BookingRepository

    @BindView(R.id.event_name_text_view)
    lateinit var purposeEditText: EditText

    private val employeeList = ArrayList<EmployeeList>()

    private val selectedName = ArrayList<String>()

    private val selectedEmail = ArrayList<String>()

    lateinit var customAdapter: SelectMembers

    @BindView(R.id.search_edit_text)
    lateinit var searchEditText: EditText

    @BindView(R.id.add_email)
    lateinit var addEmailButton: Button

    @BindView(R.id.select_meeting_members_progress_bar)
    lateinit var progressBar: ProgressBar

    private lateinit var mSelectMemberViewModel: SelectMemberViewModel


    private lateinit var mBookingViewModel: BookingViewModel

    private var mBooking = Booking()

    private lateinit var attendee: MutableList<String>

    private lateinit var acct: GoogleSignInAccount

    private lateinit var mFirebaseAnalytics: FirebaseAnalytics

    private lateinit var mGetIntentDataFromActivity: GetIntentDataFromActvity

    private var count = 0

    companion object {
        var selectedCapacity = 0
    }

    /**
     * Passing the Layout Resource to the Base Activity
     */
    override fun getLayoutResource(): Int {
        return R.layout.activity_select_meeting_members
    }

    /**
     * OnCreate Activity initialize related to the Selecting Members
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        observeData()
        setClickListenerOnEditText()
        //clear search edit text data
        searchEditText.onRightDrawableClicked {
            it.text.clear()
        }

    }

    /**
     * click on Add Email when the email is not present in the employee List
     */
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

    /**
     * initialize all lateinit fields
     */
    fun init() {
        initActionBar(getString(R.string.select_participipants))
        initComponentForSelectMembers()
        textChangeListenerOnPurposeEditText()
        initLateInitializerVariables()
        initSelectEmployeeRepo()
        hideSoftKeyBoard()
        initBookingRepo()
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        if (NetworkState.appIsConnectedToInternet(this)) {
            getViewModel()
        } else {
            val i = Intent(this, NoInternetConnectionActivity::class.java)
            startActivityForResult(i, Constants.RES_CODE)
        }
    }

    /**
     * Get the Building Repository instance from the View Model
     */
    private fun initBookingRepo() {
        mBookingViewModel.setBookingRepo(mBookingRepo)
    }

    /**
     * Hide SoftKeyBoard when onClick on the screen other than EditText
     */
    private fun hideSoftKeyBoard() {
        HideSoftKeyboard.setUpUI(findViewById(R.id.relative_layout_of_manager_select_meeting_members), this)
    }

    /*
    Inititlize View Model
     */
    private fun initLateInitializerVariables() {
        mGetIntentDataFromActivity = getIntentData()
        selectedCapacity = (mGetIntentDataFromActivity.capacity!!.toInt() + 1)
        mSelectMemberViewModel = ViewModelProviders.of(this).get(SelectMemberViewModel::class.java)
        acct = GoogleSignIn.getLastSignedInAccount(applicationContext)!!
        // getting view model object
        mBookingViewModel = ViewModelProviders.of(this).get(BookingViewModel::class.java)
    }

    /**
     * on Activity Result when the Network State is available
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.RES_CODE && resultCode == Activity.RESULT_OK) {
            getViewModel()
        } else if (requestCode == Constants.RES_CODE2 && resultCode == Activity.RESULT_OK) {
            addBooking()
        }

    }

    /**
     * observer data from ViewModel
     */
    private fun observeData() {
        // positive response from server
        mSelectMemberViewModel.returnSuccessForEmployeeList().observe(this, Observer {
            progressBar.visibility = View.GONE
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
            progressBar.visibility = View.GONE
            if (it == Constants.UNPROCESSABLE || it == Constants.INVALID_TOKEN || it == Constants.FORBIDDEN) {
                ShowDialogForSessionExpired.showAlert(this, SelectMeetingMembersActivity())
            } else {
                ShowToast.show(this, it as Int)
                finish()
            }
        })

        // positive response from server
        mBookingViewModel.returnSuccessForBooking().observe(this, Observer {
            hideProgressDialog()
            Toasty.success(this, getString(R.string.booked_successfully), Toast.LENGTH_SHORT, true).show()
            goToBookingDashboard()
        })
        // negative response from server
        mBookingViewModel.returnFailureForBooking().observe(this, Observer {
            hideProgressDialog()
            if (it == Constants.UNPROCESSABLE || it == Constants.INVALID_TOKEN || it == Constants.FORBIDDEN) {
                ShowDialogForSessionExpired.showAlert(this, SelectMeetingMembersActivity())
            } else {
                ShowToast.show(this, it as Int)
            }
        })
    }

    /**
     *  calls the function of view model to get the data from server
     */
    private fun addBooking() {
        showProgressDialog(this)
        mBookingViewModel.addBookingDetails(mBooking)
    }

    /**
     * set values to the different properties of object which is required for api call
     */
    private fun addDataToObject() {
        val acct = GoogleSignIn.getLastSignedInAccount(applicationContext)
        val mBookingDetails = getIntentData()
        mBooking.email = acct!!.email
        mBooking.purpose = purposeEditText.text.toString()
        mBooking.roomId = mBookingDetails.roomId!!.toInt()
        mBooking.buildingId = mBookingDetails.buildingId!!.toInt()
        mBooking.isPurposeVisible = mBookingDetails.isPurposeVisible
        mBooking.fromTime = FormatTimeAccordingToZone.formatDateAsUTC(mBookingDetails.fromTime!!)
        mBooking.toTime = FormatTimeAccordingToZone.formatDateAsUTC(mBookingDetails.toTime!!)
    }

    /**
     * Dependency Injection of Employee Members List
     */
    private fun initComponentForSelectMembers() {
        (application as BaseApplication).getmAppComponent()?.inject(this)
    }

    /**
     * ViewModel of SelectEmployee
     */
    private fun initSelectEmployeeRepo() {
        mSelectMemberViewModel.setEmployeeListRepo(mSelectEmployeeRepo)
    }


    /**
     *  redirect to UserBookingDashboardActivity
     */
    private fun goToBookingDashboard() {
        startActivity(Intent(this, UserBookingsDashboardActivity::class.java))
        finish()
    }

    /**
     *call function of ViewModel which will make API call
     */
    private fun getViewModel() {
        progressBar.visibility = View.VISIBLE
        mSelectMemberViewModel.getEmployeeList(acct.email!!)
    }

    /**
     * Booking the Room
     */
    @OnClick(R.id.next_activity)
    fun onClick() {
        hideSoftKeyBoard()
        purposeEditText.onEditorAction(EditorInfo.IME_ACTION_DONE)
        var emailString = ""
        val size = selectedName.size
        selectedEmail.indices.forEach { index ->
            emailString += selectedEmail[index]

            if (index != (size - 1)) {
                emailString += ","
            }
        }
        if (emailString.isNotEmpty())
            attendee = emailString.split(",").toMutableList()
        else
            attendee = emptyList<String>().toMutableList()
        mBooking.cCMail = attendee
        // show alert before booking
        if (validatePurpose()) {
            if (NetworkState.appIsConnectedToInternet(this)) {
                addDataToObject()
                addBooking()
                bookingLogFirebaseAnalytics()
            } else {
                val i = Intent(this@SelectMeetingMembersActivity, NoInternetConnectionActivity::class.java)
                startActivityForResult(i, Constants.RES_CODE2)
            }
        }

    }

    /**
     * logging the event of booking event
     */
    private fun bookingLogFirebaseAnalytics() {
        val bookingBundle = Bundle()
        mFirebaseAnalytics.logEvent(getString(R.string.singleBooking), bookingBundle)
        mFirebaseAnalytics.setAnalyticsCollectionEnabled(true)
        mFirebaseAnalytics.setSessionTimeoutDuration(1000000)
        mFirebaseAnalytics.setUserId(mBooking.email)
        mFirebaseAnalytics.setUserProperty(getString(R.string.Roll_Id), Hawk.get<Int>(Constants.ROLE_CODE).toString())
    }

    /**
     * add selected recycler item to chip and add this chip to chip group
     */
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
            scroll_view.post {
                scroll_view.smoothScrollBy(0, chip_group.bottom)
            }
        } else {
            Toast.makeText(this, getString(R.string.already_selected), Toast.LENGTH_SHORT).show()
        }
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
     * validate all input fields
     */
    private fun validatePurpose(): Boolean {
        return validateEditText(purposeEditText.text.toString().trim(), purpose_layout)
    }

    /**
     * get data from Intent
     */
    private fun getIntentData(): GetIntentDataFromActvity {
        return intent.extras!!.get(Constants.EXTRA_INTENT_DATA) as GetIntentDataFromActvity
    }

    /**
     * clear text in search bar whenever clear drawable clicked
     */
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
     * filter matched data from employee list and set updated list to adapter
     */
    fun filter(text: String) {
        val filterName = java.util.ArrayList<EmployeeList>()
        for (s in employeeList) {
            if (s.name!!.toLowerCase().contains(text.toLowerCase())) {
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


    /**
     * function checks for correct email format
     */
    private fun validateEmailFormat(): Boolean {
        val email = searchEditText.text.toString().trim()
        val pat = Pattern.compile(Constants.MATCHER)
        return pat.matcher(email).matches()
    }
}
