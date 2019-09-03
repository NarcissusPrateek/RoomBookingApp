package com.nineleaps.conferenceroombooking.recurringMeeting.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AbsListView
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.nineleaps.conferenceroombooking.BaseApplication
import com.nineleaps.conferenceroombooking.Helper.CancelledBookingAdpter
import com.nineleaps.conferenceroombooking.Helper.NetworkState
import com.nineleaps.conferenceroombooking.R
import com.nineleaps.conferenceroombooking.bookingDashboard.repository.BookingDashboardRepository
import com.nineleaps.conferenceroombooking.bookingDashboard.ui.UserBookingsDashboardActivity
import com.nineleaps.conferenceroombooking.bookingDashboard.viewModel.BookingDashboardViewModel
import com.nineleaps.conferenceroombooking.checkConnection.NoInternetConnectionActivity
import com.nineleaps.conferenceroombooking.model.BookingDashboardInput
import com.nineleaps.conferenceroombooking.model.Dashboard
import com.nineleaps.conferenceroombooking.utils.*
import kotlinx.android.synthetic.main.fragment_cancelled_booking.*
import javax.inject.Inject

@Suppress("DEPRECATION")
class CancelledBookingFragment : Fragment() {
    @Inject
    lateinit var mBookedDashBoardRepo: BookingDashboardRepository

    private var finalList = ArrayList<Dashboard>()
    private lateinit var mProgressBar: ProgressBar
    private lateinit var mBookingDashBoardViewModel: BookingDashboardViewModel
    private lateinit var acct: GoogleSignInAccount
    private lateinit var progressDialog: ProgressDialog
    private lateinit var mBookingListAdapter: CancelledBookingAdpter
    var mBookingDashboardInput = BookingDashboardInput()
    var pagination: Int = 1
    var hasMoreItem: Boolean = false
    var isScrolledState: Boolean = false
    var currentPage: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        HideSoftKeyboard.hideKeyboard(activity!!)
        activity!!.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        return inflater.inflate(R.layout.fragment_cancelled_booking, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        observeData()
    }

    /**
     * Initialize all late init fields
     */
    @SuppressLint("ResourceAsColor")
    fun init() {
        // HideSoftKeyboard.hideSoftKeyboard(activity!!)
        mProgressBar = activity!!.findViewById(R.id.cancelled_main_progress_bar)
        initRecyclerView()
        initComponentForCancelledFragment()
        initLateInitializerVariables()
        initBookedDashBoardRepo()
        cancelled_booking_refresh_layout.setColorSchemeColors(R.color.colorPrimary)
        refreshOnPullDown()
        if (NetworkState.appIsConnectedToInternet(activity!!)) {
            getViewModel()
        } else {
            val i = Intent(activity, NoInternetConnectionActivity::class.java)
            startActivityForResult(i, Constants.RES_CODE)
        }
    }

    private fun initBookedDashBoardRepo() {
        mBookingDashBoardViewModel.setBookedRoomDashboardRepo(mBookedDashBoardRepo)
    }

    private fun initComponentForCancelledFragment() {
        (activity?.application as BaseApplication).getmAppComponent()?.inject(this)
    }

    private fun initLateInitializerVariables() {
        progressDialog = GetProgress.getProgressDialog(getString(R.string.progress_message), activity!!)
        acct = GoogleSignIn.getLastSignedInAccount(activity)!!
        mBookingDashBoardViewModel = ViewModelProviders.of(this).get(BookingDashboardViewModel::class.java)
        mBookingDashboardInput.pageSize = Constants.PAGE_SIZE
        mBookingDashboardInput.status = Constants.BOOKING_DASHBOARD_TYPE_CANCELLED
        mBookingDashboardInput.pageNumber = pagination
        mBookingDashboardInput.email = acct.email.toString()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.RES_CODE && resultCode == Activity.RESULT_OK) {
            getViewModel()
        }
    }

    private fun getViewModel() {
        mProgressBar.visibility = View.VISIBLE
        mBookingDashBoardViewModel.getBookingList(
            mBookingDashboardInput
        )
    }

    private fun initRecyclerView() {
        mBookingListAdapter = CancelledBookingAdpter(
            finalList,
            activity!!,
            object : CancelledBookingAdpter.ShowMembersListener {
                override fun showMembers(mEmployeeList: List<String>, position: Int) {
                    showMeetingMembers(mEmployeeList, position)
                }

            }
        )
        cancelled_recyclerView.adapter = mBookingListAdapter
        cancelled_recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
                    cancelled_progress_bar.visibility = View.VISIBLE
                    mBookingDashboardInput.pageNumber = pagination
                    mBookingDashBoardViewModel.getBookingList(
                        mBookingDashboardInput
                    )
                } else if (!recyclerView.canScrollVertically(1) && currentPage >= 1 && isScrolledState) {
                    ShowToast.show(activity!!, Constants.NO_CONTENT_FOUND)
                }
            }
        })
    }

    /**
     * add refresh listener on pull down
     */
    private fun refreshOnPullDown() {
        cancelled_booking_refresh_layout.setOnRefreshListener {
            finalList.clear()
            pagination = 1
            mBookingDashboardInput.pageNumber = pagination
            mBookingDashBoardViewModel.getBookingList(
                mBookingDashboardInput
            )
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
            cancelled_progress_bar.visibility = View.GONE
            cancelled_booking_refresh_layout.isRefreshing = false
            mProgressBar.visibility = View.GONE
            currentPage = it.paginationMetaData!!.currentPage!!
            hasMoreItem = it.paginationMetaData!!.nextPage!!
            setFilteredDataToAdapter(it.dashboard!!)

        })
        mBookingDashBoardViewModel.returnFailure().observe(this, Observer {
            cancelled_progress_bar.visibility = View.GONE
            cancelled_booking_refresh_layout.isRefreshing = false
            mProgressBar.visibility = View.GONE
            if (it == Constants.UNPROCESSABLE || it == Constants.INVALID_TOKEN || it == Constants.FORBIDDEN) {
                ShowDialogForSessionExpired.showAlert(activity!!, UserBookingsDashboardActivity())
            } else if (it == Constants.NO_CONTENT_FOUND && finalList.size == 0) {
                cancelled_empty_view.visibility = View.VISIBLE
                cancelled_dashboard.setBackgroundColor(ContextCompat.getColor(activity!!,R.color.empty_cancelled_dashboard))
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

        if (mEmployeeList.isEmpty()) {
            arrayListOfNames.add(finalList[position].organizer + getString(R.string.organizer))

        } else {
            arrayListOfNames.add(finalList[position].organizer + getString(R.string.organizer))

            for (item in mEmployeeList) {
                arrayListOfNames.add(item)
            }
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
        finalList.addAll(dashboardItemList)
        cancelled_recyclerView.adapter?.notifyDataSetChanged()
    }


}