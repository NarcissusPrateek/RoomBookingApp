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
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.nineleaps.conferenceroombooking.BaseApplication
import com.nineleaps.conferenceroombooking.Helper.NetworkState
import com.nineleaps.conferenceroombooking.Helper.PreviousBookingAdapter
import com.nineleaps.conferenceroombooking.R
import com.nineleaps.conferenceroombooking.bookingDashboard.repository.BookingDashboardRepository
import com.nineleaps.conferenceroombooking.bookingDashboard.ui.UserBookingsDashboardActivity
import com.nineleaps.conferenceroombooking.bookingDashboard.viewModel.BookingDashboardViewModel
import com.nineleaps.conferenceroombooking.checkConnection.NoInternetConnectionActivity
import com.nineleaps.conferenceroombooking.model.BookingDashboardInput
import com.nineleaps.conferenceroombooking.model.Dashboard
import com.nineleaps.conferenceroombooking.utils.*
import kotlinx.android.synthetic.main.fragment_previous_booking.*
import javax.inject.Inject

@Suppress("DEPRECATION")
class PreviousBookingFragment : Fragment() {

    @Inject
    lateinit var mBookedDashBoardRepo: BookingDashboardRepository
    private lateinit var mProgressBar: ProgressBar
    private var finalList = ArrayList<Dashboard>()
    private lateinit var mBookingDashBoardViewModel: BookingDashboardViewModel
    private lateinit var acct: GoogleSignInAccount
    private lateinit var progressDialog: ProgressDialog
    private lateinit var mBookingListAdapter: PreviousBookingAdapter
    var mBookingDashboardInput = BookingDashboardInput()
    var pagination: Int = 1
    var hasMoreItem: Boolean = false
    var isApiCalled: Boolean = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_previous_booking, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        observeData()
    }

    private fun initBookedDashBoardRepo() {
        mBookingDashBoardViewModel.setBookedRoomDashboardRepo(mBookedDashBoardRepo)
    }

    private fun initComponentForPreviousFragment() {
        (activity?.application as BaseApplication).getmAppComponent()?.inject(this)
    }

    /**
     * Initialize all late init fields
     */
    @SuppressLint("ResourceAsColor")
    fun init() {
        mProgressBar = activity!!.findViewById(R.id.progress_bar)
        initRecyclerView()
        initComponentForPreviousFragment()
        initLateInitializerVariables()
        initBookedDashBoardRepo()
        if (NetworkState.appIsConnectedToInternet(activity!!)) {
            getViewModel()
        } else {
            val i = Intent(activity!!, NoInternetConnectionActivity::class.java)
            startActivityForResult(i, Constants.RES_CODE)
        }
        refreshOnPullDown()
    }

    @SuppressLint("ResourceAsColor")
    private fun initLateInitializerVariables() {
        progressDialog = GetProgress.getProgressDialog(getString(R.string.progress_message), activity!!)
        acct = GoogleSignIn.getLastSignedInAccount(activity)!!
        mBookingDashBoardViewModel = ViewModelProviders.of(this).get(BookingDashboardViewModel::class.java)
        previous__booking_refresh_layout.setColorSchemeColors(R.color.colorPrimary)
        mBookingDashboardInput.pageSize = Constants.PAGE_SIZE
        mBookingDashboardInput.status = Constants.BOOKING_DASHBOARD_TYPE_PREVIOUS
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
        //progressDialog.show()
        mBookingDashBoardViewModel.getBookingList(GetPreference.getTokenFromPreference(activity!!), mBookingDashboardInput)
    }

    private fun initRecyclerView() {
        mBookingListAdapter = PreviousBookingAdapter(
            finalList,
            activity!!,
            object : PreviousBookingAdapter.ShowMembersListener {
                override fun showMembers(mEmployeeList: List<String>, position: Int) {
                    showMeetingMembers(mEmployeeList, position)
                }

            }
        )
        previous__recyclerView.adapter = mBookingListAdapter
        previous__recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1) && !isApiCalled && hasMoreItem) {
                    isApiCalled = true
                    pagination++
                    previous_progress_bar.visibility = View.VISIBLE
                    mBookingDashboardInput.pageNumber = pagination
                    mBookingDashBoardViewModel.getBookingList(GetPreference.getTokenFromPreference(activity!!), mBookingDashboardInput)
                }
            }
        })
    }

    /**
     * add refresh listener on pull down
     */
    private fun refreshOnPullDown() {
        previous__booking_refresh_layout.setOnRefreshListener {
            finalList.clear()
            pagination = 1
            mBookingDashboardInput.pageNumber = pagination
            mBookingDashBoardViewModel.getBookingList(GetPreference.getTokenFromPreference(activity!!), mBookingDashboardInput)
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
            previous_progress_bar.visibility = View.GONE
            previous__booking_refresh_layout.isRefreshing = false
            mProgressBar.visibility = View.GONE
            //progressDialog.dismiss()
            previous__empty_view.visibility = View.GONE
            hasMoreItem = it.paginationMetaData!!.nextPage!!
            setFilteredDataToAdapter(it.dashboard!!)
        })
        mBookingDashBoardViewModel.returnFailure().observe(this, Observer {
            previous_progress_bar.visibility = View.GONE
            previous__booking_refresh_layout.isRefreshing = false
            mProgressBar.visibility = View.GONE
            //progressDialog.dismiss()
            if (it == Constants.INVALID_TOKEN) {
                ShowDialogForSessionExpired.showAlert(activity!!, UserBookingsDashboardActivity())
            } else if (it == Constants.NO_CONTENT_FOUND && finalList.size == 0) {
                previous__empty_view.visibility = View.VISIBLE
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
        arrayListOfNames.add(finalList[position].organizer!! + getString(R.string.organizer))
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
        finalList.addAll(dashboardItemList)
        previous__recyclerView.adapter?.notifyDataSetChanged()
    }
}

