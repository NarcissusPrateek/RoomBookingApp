package com.nineleaps.conferenceroombooking.recurringMeeting.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.analytics.FirebaseAnalytics
import com.nineleaps.conferenceroombooking.BaseApplication
import com.nineleaps.conferenceroombooking.Helper.NetworkState
import com.nineleaps.conferenceroombooking.Helper.UpcomingBookingAdapter
import com.nineleaps.conferenceroombooking.R
import com.nineleaps.conferenceroombooking.bookingDashboard.repository.BookingDashboardRepository
import com.nineleaps.conferenceroombooking.bookingDashboard.ui.UserBookingsDashboardActivity
import com.nineleaps.conferenceroombooking.bookingDashboard.viewModel.BookingDashboardViewModel
import com.nineleaps.conferenceroombooking.checkConnection.NoInternetConnectionActivity
import com.nineleaps.conferenceroombooking.model.BookingDashboardInput
import com.nineleaps.conferenceroombooking.model.Dashboard
import com.nineleaps.conferenceroombooking.model.GetIntentDataFromActvity
import com.nineleaps.conferenceroombooking.updateBooking.ui.UpdateBookingActivity
import com.nineleaps.conferenceroombooking.utils.*
import com.orhanobut.hawk.Hawk
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_upcoming_booking.*
import javax.inject.Inject

class UpcomingBookingFragment : Fragment() {

    @Inject
    lateinit var mBookedDashboardRepo: BookingDashboardRepository
    private lateinit var mProgressBar: ProgressBar
    private var finalList = ArrayList<Dashboard>()
    private lateinit var mBookingDashBoardViewModel: BookingDashboardViewModel
    private lateinit var acct: GoogleSignInAccount
    private lateinit var progressDialog: ProgressDialog
    private lateinit var mBookingListAdapter: UpcomingBookingAdapter
    private lateinit var mFirebaseAnalytics: FirebaseAnalytics
    private var bookingId = 0
    lateinit var email: String
    private var recurringmeetingId: String? = null
    private var makeApiCallOnResume = false
    var pagination: Int = 1
    var hasMoreItem: Boolean = false
    var mBookingDashboardInput = BookingDashboardInput()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        HideSoftKeyboard.hideKeyboard(activity!!)
        return inflater.inflate(R.layout.fragment_upcoming_booking, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        observeData()
    }

    private fun initBookedDashBoardRepo() {
        mBookingDashBoardViewModel.setBookedRoomDashboardRepo(mBookedDashboardRepo)
    }

    private fun initComponentForUpcomingFragment() {
        (activity?.application as BaseApplication).getmAppComponent()?.inject(this)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.RES_CODE && resultCode == Activity.RESULT_OK) {
            getViewModel()
        }
        if (requestCode == Constants.RES_CODE2 && resultCode == Activity.RESULT_OK) {
            cancelBooking(bookingId)
        }
    }

    /**
     * Initialize all late init fields
     */
    @SuppressLint("ResourceAsColor")
    fun init() {
        Log.i("Role", Hawk.get<Int>(Constants.ROLE_CODE).toString())
        mProgressBar = activity!!.findViewById(R.id.upcoming_main_progress_bar)
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(activity!!)
        initRecyclerView()
        initComponentForUpcomingFragment()
        initLateInitializerVariables()
        initBookedDashBoardRepo()
        booking_refresh_layout.setColorSchemeColors(R.color.colorPrimary)
        refreshOnPullDown()
        if (NetworkState.appIsConnectedToInternet(activity!!)) {
            getViewModel()
        } else {
            val i = Intent(activity!!, NoInternetConnectionActivity::class.java)
            startActivityForResult(i, Constants.RES_CODE)
        }
    }

    private fun initLateInitializerVariables() {
        progressDialog = GetProgress.getProgressDialog(getString(R.string.progress_message), activity!!)
        acct = GoogleSignIn.getLastSignedInAccount(activity)!!
        mBookingDashBoardViewModel = ViewModelProviders.of(this).get(BookingDashboardViewModel::class.java)
        mBookingDashboardInput.pageSize = Constants.PAGE_SIZE
        mBookingDashboardInput.status = Constants.BOOKING_DASHBOARD_TYPE_UPCOMING
        mBookingDashboardInput.pageNumber = pagination
        email = acct.email.toString()
        mBookingDashboardInput.email = email
        signInAnalyticFirebase()
    }

    private fun getViewModel() {
        mProgressBar.visibility = View.VISIBLE
        mBookingDashBoardViewModel.getBookingList(
            mBookingDashboardInput
        )
    }


    private fun signInAnalyticFirebase() {
        mFirebaseAnalytics.setAnalyticsCollectionEnabled(true)
        mFirebaseAnalytics.setMinimumSessionDuration(5000)
        mFirebaseAnalytics.setSessionTimeoutDuration(1000000)
        mFirebaseAnalytics.setUserId(email)
        mFirebaseAnalytics.setUserProperty("Roll Id", GetPreference.getRoleIdFromPreference().toString())
    }

    private fun initRecyclerView() {
        mBookingListAdapter = UpcomingBookingAdapter(
            finalList,
            activity!!,
            object : UpcomingBookingAdapter.CancelBtnClickListener {
                override fun onCLick(position: Int) {
                    showConfirmDialogForCancelMeeting(position)
                }
            },
            object : UpcomingBookingAdapter.ShowMembersListener {
                override fun showMembers(mEmployeeList: List<String>, position: Int) {
                    showMeetingMembers(mEmployeeList, position)
                }

            },
            object : UpcomingBookingAdapter.EditBookingListener {
                override fun editBooking(mGetIntentDataFromActvity: GetIntentDataFromActvity) {
                    intentToUpdateBookingActivity(mGetIntentDataFromActvity)
                }
            }
        )
        dashBord_recyclerView1.adapter = mBookingListAdapter
        dashBord_recyclerView1.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1) && hasMoreItem) {
                    pagination++
                    mBookingDashboardInput.pageNumber = pagination
                    upcoming_booking_progress_bar.visibility = View.VISIBLE
                    mBookingDashBoardViewModel.getBookingList(
                        mBookingDashboardInput
                    )
                }
            }
        })
    }

    /**
     * add refresh listener on pull down
     */
    private fun refreshOnPullDown() {
        booking_refresh_layout.setOnRefreshListener {
            finalList.clear()
            pagination = 1
            mBookingDashboardInput.pageNumber = pagination
            if (NetworkState.appIsConnectedToInternet(activity!!)) {
                mBookingDashBoardViewModel.getBookingList(
                    mBookingDashboardInput
                )
            } else {
                val i = Intent(activity!!, NoInternetConnectionActivity::class.java)
                startActivityForResult(i, Constants.RES_CODE)
            }
        }
    }

    /**
     * all observer for LiveData
     */
    private fun observeData() {

        /**
         * observing data for booking list
         */
        mBookingDashBoardViewModel.returnSuccess().observe(this, Observer {
            upcoming_empty_view.visibility = View.GONE
            upcoming_booking_progress_bar.visibility = View.GONE
            booking_refresh_layout.isRefreshing = false
            mProgressBar.visibility = View.GONE
            progressDialog.dismiss()
            hasMoreItem = it.paginationMetaData!!.nextPage!!
            Log.i("@@@@@!!",it.dashboard!!.toString())
            setFilteredDataToAdapter(it.dashboard!!)
        })
        mBookingDashBoardViewModel.returnFailure().observe(this, Observer {
            upcoming_booking_progress_bar.visibility = View.GONE
            booking_refresh_layout.isRefreshing = false
            mProgressBar.visibility = View.GONE
            progressDialog.dismiss()
            if (it == Constants.UNPROCESSABLE || it == Constants.INVALID_TOKEN || it == Constants.FORBIDDEN) {
                ShowDialogForSessionExpired.showAlert(activity!!, UserBookingsDashboardActivity())
            } else if (it == Constants.NO_CONTENT_FOUND && finalList.size == 0) {
                upcoming_empty_view.visibility = View.VISIBLE
                r1_dashboard.setBackgroundColor(Color.parseColor("#F7F7F7"))
            } else {
                ShowToast.show(activity!!, it as Int)
            }
        })

        /**
         * observing data for cancel booking
         */
        mBookingDashBoardViewModel.returnBookingCancelled().observe(this, Observer {
            Toasty.success(activity!!, getString(R.string.cancelled_successful), Toast.LENGTH_SHORT, true).show()
            pagination = 1
            mBookingDashboardInput.pageNumber = pagination
            finalList.clear()
            dashBord_recyclerView1.adapter?.notifyDataSetChanged()
            mBookingDashBoardViewModel.getBookingList(
                mBookingDashboardInput
            )
        })

        mBookingDashBoardViewModel.returnCancelFailed().observe(this, Observer {
            progressDialog.dismiss()
            if (it == Constants.UNPROCESSABLE || it == Constants.INVALID_TOKEN || it == Constants.FORBIDDEN) {
                ShowDialogForSessionExpired.showAlert(activity!!, UserBookingsDashboardActivity())
            } else {
                ShowToast.show(activity!!, it as Int)
            }
        })

    }


    /**
     * Display the list of employee names in the alert dialog
     */
    fun showMeetingMembers(mEmployeeList: List<String>, position: Int) {
        val arrayListOfNames = ArrayList<String>()
        arrayListOfNames.add(finalList[position].organizer + getString(R.string.organizer))
        for (item in mEmployeeList) {
            arrayListOfNames.add(item)
        }
        val listItems = arrayOfNulls<String>(arrayListOfNames.size)
        arrayListOfNames.toArray(listItems)
        val builder = AlertDialog.Builder(activity!!)
        builder.setItems(
            listItems
        ) { _, _ -> }
        val mDialog = builder.create()
        mDialog.show()
    }


    /**
     * this function will call a function which will filter the data after that set the filtered data to adapter
     */
    private fun setFilteredDataToAdapter(dashboardItemList: List<Dashboard>) {
        Log.i("@@@@",dashboardItemList.toString())

        finalList.addAll(dashboardItemList)
        Log.i("@@@@!",finalList.toString())
        dashBord_recyclerView1.adapter?.notifyDataSetChanged()
    }

    /**
     * function will send intent to the UpdateActivity with data which is required for updation
     */
    fun intentToUpdateBookingActivity(mGetIntentDataFromActivity: GetIntentDataFromActvity) {
        makeApiCallOnResume = true
        val updateActivityIntent = Intent(activity!!, UpdateBookingActivity::class.java)
        updateActivityIntent.putExtra(Constants.EXTRA_INTENT_DATA, mGetIntentDataFromActivity)
        startActivity(updateActivityIntent)
    }


    /**
     * show a dialog to confirm cancel of booking
     * if ok button is pressed than cancelBooking function is called
     */
    fun showConfirmDialogForCancelMeeting(position: Int) {
        if (finalList[position].recurringmeetingId == null) {
            singleCancellationMeeting(position)
        } else {
            recurringCancellationMetting(position)
        }

    }

    private fun recurringCancellationMetting(position: Int) {
        var selectedList = mutableListOf<Int>()
        val items = arrayOf<CharSequence>("Cancel All")
        val builder =
            GetAleretDialog.getDialogforRecurring(
                activity!!,
                "Delete"
            )
        builder.setMultiChoiceItems(
            items,
            null
        ) { _, which, isChecked ->
            if (isChecked) {
                selectedList.add(which)
            } else if (selectedList.contains(which)) {
                selectedList.remove(which)
            }
        }
        builder.setPositiveButton(getString(R.string.ok)) { _, _ ->
            if (selectedList.isEmpty()) {
                bookingId = finalList[position].bookingId!!
                if (NetworkState.appIsConnectedToInternet(activity!!)) {
                    cancelBooking(finalList[position].bookingId!!)
                    singleCancelLogFirebaseAnaytics()
                } else {
                    val i = Intent(activity!!, NoInternetConnectionActivity::class.java)
                    startActivityForResult(i, Constants.RES_CODE2)
                }
            } else if (selectedList.any()) {
                bookingId = finalList[position].bookingId!!
                recurringmeetingId = finalList[position].recurringmeetingId
                if (NetworkState.appIsConnectedToInternet(activity!!)) {
                    recurringCancelBooking(bookingId, recurringmeetingId)
                    recurringCancelLogFirebaseAnalytics()
                } else {
                    val i = Intent(activity!!, NoInternetConnectionActivity::class.java)
                    startActivityForResult(i, Constants.RES_CODE2)
                }
            }

        }

        builder.setNegativeButton(getString(R.string.no)) { _, _ ->

        }
        val dialog = GetAleretDialog.showDialog(builder)
        ColorOfDialogButton.setColorOfDialogButton(dialog)
    }

    private fun recurringCancelLogFirebaseAnalytics() {
        val cancellation = Bundle()
        mFirebaseAnalytics.logEvent(getString(R.string.recurring_cancellation), cancellation)
        mFirebaseAnalytics.setAnalyticsCollectionEnabled(true)
        mFirebaseAnalytics.setMinimumSessionDuration(5000)
        mFirebaseAnalytics.setSessionTimeoutDuration(1000000)
        mFirebaseAnalytics.setUserId(email)
        mFirebaseAnalytics.setUserProperty(
            getString(R.string.Roll_Id),
            GetPreference.getRoleIdFromPreference().toString()
        )
    }

    private fun recurringCancelBooking(bookingId: Int, recurringmeetingId: String?) {
        progressDialog.show()
        mBookingDashBoardViewModel.recurringCancelBooking(
            bookingId,
            recurringmeetingId!!
        )
    }

    private fun singleCancellationMeeting(position: Int) {
        val mBuilder =
            GetAleretDialog.getDialog(
                activity!!,
                getString(R.string.cancel),
                getString(R.string.sure_cancel_meeting)
            )
        mBuilder.setPositiveButton(getString(R.string.yes)) { _, _ ->
            /**
             * object which is required for the API call
             */
            bookingId = finalList[position].bookingId!!
            if (NetworkState.appIsConnectedToInternet(activity!!)) {
                cancelBooking(finalList[position].bookingId!!)
                singleCancelLogFirebaseAnaytics()
            } else {
                val i = Intent(activity!!, NoInternetConnectionActivity::class.java)
                startActivityForResult(i, Constants.RES_CODE2)
            }
        }
        mBuilder.setNegativeButton(getString(R.string.no)) { _, _ ->
        }
        val dialog = GetAleretDialog.showDialog(mBuilder)
        ColorOfDialogButton.setColorOfDialogButton(dialog)
    }

    private fun singleCancelLogFirebaseAnaytics() {
        val cancellation = Bundle()
        mFirebaseAnalytics.logEvent(getString(R.string.single_cancellation), cancellation)
        mFirebaseAnalytics.setAnalyticsCollectionEnabled(true)
        mFirebaseAnalytics.setMinimumSessionDuration(5000)
        mFirebaseAnalytics.setSessionTimeoutDuration(1000000)
        mFirebaseAnalytics.setUserId(email)
        mFirebaseAnalytics.setUserProperty(
            getString(R.string.Roll_Id),
            GetPreference.getRoleIdFromPreference().toString()
        )
    }

    /**
     * A function for cancel a booking
     */
    private fun cancelBooking(mBookingId: Int) {
        progressDialog.show()
        mBookingDashBoardViewModel.cancelBooking(mBookingId)
    }

    override fun onResume() {
        super.onResume()
        if (makeApiCallOnResume) {
            finalList.clear()
            pagination = 1
            mBookingDashboardInput.pageNumber = pagination
            getViewModel()
            makeApiCallOnResume = false
        }
    }
}

