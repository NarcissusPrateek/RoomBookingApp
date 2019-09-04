package com.nineleaps.conferenceroombooking.recurringMeeting.ui

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.content.ContextCompat
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

    private var recurringArrayList = ArrayList<Dashboard>()

    private var bookingId = 0

    lateinit var email: String

    private var recurringmeetingId: String? = null

    private var makeApiCallOnResume = false

    private var cardPosition = -1

    var pagination: Int = 1

    var hasMoreItem: Boolean = false

    var currentPage: Int = 0

    var checkStatus: Boolean = false

    var isScrolledState: Boolean = false

    var mBookingDashboardInput = BookingDashboardInput()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        HideSoftKeyboard.hideKeyboard(activity!!)
        return inflater.inflate(R.layout.fragment_upcoming_booking, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        observeDataForListOfBookings()
        observeDataForCancelationOfBookings()
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
    fun init() {
        mProgressBar = activity!!.findViewById(R.id.upcoming_main_progress_bar)
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(activity!!)
        initRecyclerView()
        initComponentForUpcomingFragment()
        initLateInitializerVariables()
        initBookedDashBoardRepo()
        booking_refresh_layout.setColorSchemeColors(ContextCompat.getColor(activity!!,R.color.colorPrimary))
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
        WindowManager.disableInteraction(activity!!)
        mProgressBar.visibility = View.VISIBLE
        mBookingDashBoardViewModel.getBookingList(
            mBookingDashboardInput
        )
    }


    private fun signInAnalyticFirebase() {
        mFirebaseAnalytics.setAnalyticsCollectionEnabled(true)
        mFirebaseAnalytics.setUserId(email)
        mFirebaseAnalytics.setUserProperty(getString(R.string.Roll_Id), GetPreference.getRoleIdFromPreference().toString())
    }

    private fun initRecyclerView() {
        mBookingListAdapter = UpcomingBookingAdapter(
            finalList,
            activity!!,
            object : UpcomingBookingAdapter.CancelBtnClickListener {
                override fun onCLick(position: Int) {
                    cardPosition = position
                    showConfirmDialogForCancelMeeting(position)
                }
            },
            object : UpcomingBookingAdapter.ShowMembersListener {
                override fun showMembers(mEmployeeList: List<String>, position: Int) {
                    ShowAlertDialogForEmployeeList.showEmployeeList(mEmployeeList, position,finalList,activity!!)
                }

            },
            object : UpcomingBookingAdapter.EditBookingListener {
                override fun editBooking(mGetIntentDataFromActvity: GetIntentDataFromActvity) {
                    intentToUpdateBookingActivity(mGetIntentDataFromActvity)
                }
            },
            object : UpcomingBookingAdapter.MoreAminitiesListner {
                override fun moreAmenities(position: Int) {
                    showDialogForMoreAminities(finalList[position].amenities!!)
                }

            }
        )
        dashBord_recyclerView1.adapter = mBookingListAdapter
        dashBord_recyclerView1.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                    isScrolledState = true
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE)
                    isScrolledState = false
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(1) && hasMoreItem) {
                    pagination++
                    mBookingDashboardInput.pageNumber = pagination
                    upcoming_booking_progress_bar.visibility = View.VISIBLE
                    mBookingDashBoardViewModel.getBookingList(
                        mBookingDashboardInput
                    )
                } else if (!recyclerView.canScrollVertically(1) && isScrolledState && !checkStatus ) {
                    ShowToast.show(activity!!, Constants.NO_CONTENT_FOUND)
                }
            }
        })


    }


    /**
     * add refresh listener on pull down
     */
    private fun refreshOnPullDown() {
        booking_refresh_layout.setOnRefreshListener {
            checkStatus = true
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
    private fun observeDataForListOfBookings() {

        /**
         * observing data for booking list
         */
        mBookingDashBoardViewModel.returnSuccess().observe(this, Observer {
            upcoming_empty_view.visibility = View.GONE
            upcoming_booking_progress_bar.visibility = View.GONE
            booking_refresh_layout.isRefreshing = false
            WindowManager.enableInteraction(activity!!)
            mProgressBar.visibility = View.GONE
            progressDialog.dismiss()
            hasMoreItem = it.paginationMetaData!!.nextPage!!
            currentPage = it.paginationMetaData!!.currentPage!!
            setFilteredDataToAdapter(it.dashboard!!)
            checkStatus = false
        })

        mBookingDashBoardViewModel.returnFailure().observe(this, Observer {
            upcoming_booking_progress_bar.visibility = View.GONE
            booking_refresh_layout.isRefreshing = false
            WindowManager.enableInteraction(activity!!)
            mProgressBar.visibility = View.GONE
            progressDialog.dismiss()
            if (it == Constants.UNPROCESSABLE || it == Constants.INVALID_TOKEN || it == Constants.FORBIDDEN) {
                ShowDialogForSessionExpired.showAlert(activity!!, UserBookingsDashboardActivity())
            } else if (it == Constants.NO_CONTENT_FOUND && finalList.size == 0) {
                upcoming_empty_view.visibility = View.VISIBLE
                r1_dashboard.setBackgroundColor(ContextCompat.getColor(activity!!,R.color.empty_upcoming_dashboard))
            } else {
                ShowToast.show(activity!!, it as Int)
            }
        })




    }

    /**
     * observing data for cancel booking
     */
    private fun observeDataForCancelationOfBookings(){
        mBookingDashBoardViewModel.returnBookingCancelled().observe(this, Observer {
            Toasty.success(activity!!, getString(R.string.cancelled_successful), Toast.LENGTH_SHORT, true).show()
            checkStatus = true
            if (recurringmeetingId == null && finalList.size != 0)
                finalList.remove(finalList[cardPosition])
            else {
                finalList.removeAll(recurringArrayList)
                recurringArrayList.clear()
            }
            if (finalList.size == 0) {
                upcoming_empty_view.visibility = View.VISIBLE
                r1_dashboard.setBackgroundColor(ContextCompat.getColor(activity!!,R.color.empty_upcoming_dashboard))
            }
            dashBord_recyclerView1.adapter?.notifyDataSetChanged()
            progressDialog.dismiss()
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

    private fun showDialogForMoreAminities(items: List<String>) {

        val arrayListOfItems = ArrayList<String>()

        for (item in items) {
            arrayListOfItems.add(item)
        }
        val listItems = arrayOfNulls<String>(arrayListOfItems.size)
        arrayListOfItems.toArray(listItems)
        val builder = AlertDialog.Builder(activity!!)
        builder.setTitle(getString(R.string.amenities_title))
        builder.setItems(listItems) { _, _ ->

        }
        val mDialog = builder.create()
        mDialog.show()
    }

    /**
     * this function will call a function which will filter the data after that set the filtered data to adapter
     */
    private fun setFilteredDataToAdapter(dashboardItemList: List<Dashboard>) {
        finalList.addAll(dashboardItemList)
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


    private fun recurringCancellationList() {
        if (finalList.isNotEmpty()) {
            for (i in finalList.indices) {
                if (finalList[i].recurringmeetingId == recurringmeetingId) {
                    recurringArrayList.add(finalList[i])
                }
            }
        }
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
        val selectedList = mutableListOf<Int>()
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
                    recurringCancellationList()
                    recurringCancelBooking(bookingId, recurringmeetingId)
                    recurringCancelLogFirebaseAnalytics()
                } else {
                    val i = Intent(activity!!, NoInternetConnectionActivity::class.java)
                    startActivityForResult(i, Constants.RES_CODE2)
                }
            }

        }

        builder.setNegativeButton(getString(R.string.no)) { _, _ ->
            recurringArrayList.clear()
        }
        val dialog = GetAleretDialog.showDialog(builder)
        ColorOfDialogButton.setColorOfDialogButton(dialog)
    }

    private fun recurringCancelLogFirebaseAnalytics() {
        val cancellation = Bundle()
        mFirebaseAnalytics.logEvent(getString(R.string.recurring_cancellation), cancellation)
        mFirebaseAnalytics.setAnalyticsCollectionEnabled(true)
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

