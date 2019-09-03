package com.nineleaps.conferenceroombooking.blockDashboard.ui

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import butterknife.BindView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.analytics.FirebaseAnalytics
import com.nineleaps.conferenceroombooking.BaseActivity
import com.nineleaps.conferenceroombooking.BaseApplication
import com.nineleaps.conferenceroombooking.Blocked
import com.nineleaps.conferenceroombooking.Helper.BlockedDashboardNew
import com.nineleaps.conferenceroombooking.Helper.NetworkState
import com.nineleaps.conferenceroombooking.R
import com.nineleaps.conferenceroombooking.ViewModel.BlockedDashboardViewModel
import com.nineleaps.conferenceroombooking.blockDashboard.repository.BlockDashboardRepository
import com.nineleaps.conferenceroombooking.bookingDashboard.ui.UserBookingsDashboardActivity
import com.nineleaps.conferenceroombooking.checkConnection.NoInternetConnectionActivity
import com.nineleaps.conferenceroombooking.utils.*
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_blocked_dashboard.*
import javax.inject.Inject


class BlockedDashboard : BaseActivity() {


    /**
     * Declaring Global variables and butterknife
     */

    @Inject
    lateinit var mBlockDashBoardRepo: BlockDashboardRepository

    @BindView(R.id.block_recyclerView)
    lateinit var recyclerView: RecyclerView

    @BindView(R.id.block_dashboard_refresh_layout)
    lateinit var refreshLayout: SwipeRefreshLayout

    private lateinit var acct: GoogleSignInAccount

    private lateinit var blockedAdapter: BlockedDashboardNew

    private lateinit var mBlockedDashboardViewModel: BlockedDashboardViewModel

    private lateinit var mFirebaseAnalytics: FirebaseAnalytics

    private var mBlockRoomList = ArrayList<Blocked>()

    private var bookingId = -1


    /**
     * Passing the Layout Resource to the Base Activity
     */
    override fun getLayoutResource(): Int {
        return R.layout.activity_blocked_dashboard
    }

    /**
     * OnCreate Activity initialize related to the Adding Conference
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        observeData()
    }

    /**
     * Inititlize View Model
     */
    private fun initLateInitializerVariables() {
        mBlockedDashboardViewModel = ViewModelProviders.of(this).get(BlockedDashboardViewModel::class.java)

    }

    /**
     * Initialize late init fields
     */
    fun init() {
        initActionBar(getString(R.string.Blocked_Rooms))
        initComponentForBlockDashBoard()
        initLateInitializerVariables()
        initBlockDashBoardRepo()
        acct = GoogleSignIn.getLastSignedInAccount(this)!!
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        refreshLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorPrimary))
        when {
            NetworkState.appIsConnectedToInternet(this) -> loadBlocking()
            else -> {
                val i = Intent(this, NoInternetConnectionActivity::class.java)
                startActivityForResult(i, Constants.RES_CODE)
            }
        }
        initRecyclerView()
        refreshOnPull()
    }

    /**
     *   Dependency Injection of Add Building
     */
    private fun initComponentForBlockDashBoard() {
        (application as BaseApplication).getmAppComponent()?.inject(this)
    }

    /*
      Get the BlockDashboard Repository instance from the View Model
    */
    private fun initBlockDashBoardRepo() {
        mBlockedDashboardViewModel.setBlockedRoomDashboardRepo(mBlockDashBoardRepo)
    }

    /**
     * on Activity Result when the Network State is available
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.RES_CODE && resultCode == Activity.RESULT_OK) {
            loadBlocking()
        }
        if (requestCode == Constants.RES_CODE2 && resultCode == Activity.RESULT_OK) {
            unblockRoom(bookingId)
        }
    }

    /**
     * refresh on pull
     */
    private fun refreshOnPull() {
        refreshLayout.setOnRefreshListener {
            loadBlocking()
        }
    }

    /**
     * observing data for BlockDashboardList
     */
    private fun observeData() {
        /**
         * observing data for BlockDashboardList
         */
        mBlockedDashboardViewModel.returnBlockedRoomList().observe(this, Observer {
            refreshLayout.isRefreshing = false

            if (it.isNotEmpty()) {
                empty_view_blocked.visibility = View.GONE
                mBlockRoomList.clear()
                mBlockRoomList.addAll(it)
                blockedAdapter.notifyDataSetChanged()
                hideProgressDialog()
            } else {
                empty_view_blocked.visibility = View.VISIBLE
                r2_block_dashboard.setBackgroundColor(Color.parseColor("#FFFFFF"))
                mBlockRoomList.clear()
                blockedAdapter.notifyDataSetChanged()
                hideProgressDialog()
            }

        })
        mBlockedDashboardViewModel.returnFailureCodeFromBlockedApi().observe(this, Observer {
            refreshLayout.isRefreshing = false

            when (it) {
                Constants.INVALID_TOKEN, Constants.FORBIDDEN, Constants.UNPROCESSABLE -> {
                    ShowDialogForSessionExpired.showAlert(
                        this,
                        BlockedDashboard()
                    )
                    hideProgressDialog()
                }
                Constants.NO_CONTENT_FOUND -> {
                    empty_view_blocked.visibility = View.VISIBLE
                    r2_block_dashboard.setBackgroundColor(Color.parseColor("#FFFFFF"))
                    mBlockRoomList.clear()
                    blockedAdapter.notifyDataSetChanged()
                    hideProgressDialog()
                }
                else -> ShowToast.show(this, it as Int)
            }
        })
        /**
         * observing data for Unblocking
         */
        mBlockedDashboardViewModel.returnSuccessCodeForUnBlockRoom().observe(this, Observer {
            hideProgressDialog()
            Toasty.success(this, getString(R.string.room_unblocked), Toast.LENGTH_SHORT, true).show()
            loadBlocking()
        })
        mBlockedDashboardViewModel.returnFailureCodeForUnBlockRoom().observe(this, Observer {
            hideProgressDialog()
            when (it) {
                Constants.INVALID_TOKEN, Constants.UNPROCESSABLE, Constants.FORBIDDEN -> ShowDialogForSessionExpired.showAlert(
                    this,
                    BlockedDashboard()
                )
                else -> ShowToast.show(this, it as Int)
            }
        })
    }

    /**
     * Initialize the RecyclerView
     */
    private fun initRecyclerView() {
        blockedAdapter = BlockedDashboardNew(
            mBlockRoomList,
            this,
            object : BlockedDashboardNew.UnblockRoomListener {
                override fun onClickOfUnblock(bookingId: Int) {
                    when {
                        NetworkState.appIsConnectedToInternet(this@BlockedDashboard) -> confirmAlertDialogToUnblockRoom(
                            bookingId
                        )
                        else -> {
                            val i = Intent(this@BlockedDashboard, NoInternetConnectionActivity::class.java)
                            startActivityForResult(i, Constants.RES_CODE2)
                        }
                    }
                }
            })
        recyclerView.adapter = blockedAdapter
    }

    /**
     * AlertDialog for Unblock the Room
     */
    private fun confirmAlertDialogToUnblockRoom(mBookingId: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.confirm))
        builder.setMessage(getString(R.string.unblock_room_confirmation_message))
        builder.setPositiveButton(getString(R.string.yes)) { _, _ ->
            if (NetworkState.appIsConnectedToInternet(this)) {
                unblockRoom(mBookingId)
            } else {
                bookingId = mBookingId
                val i = Intent(this@BlockedDashboard, NoInternetConnectionActivity::class.java)
                startActivityForResult(i, Constants.RES_CODE2)
            }

            unBlockLogFirebase()
        }
        builder.setNegativeButton(getString(R.string.no)) { _, _ ->
            //disable the AlertDialog
        }
        val dialog: AlertDialog = builder.create()
        dialog.setCancelable(false)
        dialog.show()
        ColorOfDialogButton.setColorOfDialogButton(dialog)
    }

    /**
     * logging into Firebase of Unblock Event
     */
    private fun unBlockLogFirebase() {
        val unBlockBundle = Bundle()
        mFirebaseAnalytics.logEvent(getString(R.string.UnBlockRoom), unBlockBundle)
        mFirebaseAnalytics.setAnalyticsCollectionEnabled(true)
        mFirebaseAnalytics.setSessionTimeoutDuration(1000000)
        mFirebaseAnalytics.setUserId(acct.email)
        mFirebaseAnalytics.setUserProperty(
            getString(R.string.Roll_Id),
            GetPreference.getRoleIdFromPreference().toString()
        )
    }

    /**
     * On Restart of Activity
     */
    override fun onRestart() {
        super.onRestart()
        if (NetworkState.appIsConnectedToInternet(this)) {
            loadBlocking()
        } else {
            val i = Intent(this@BlockedDashboard, NoInternetConnectionActivity::class.java)
            startActivityForResult(i, Constants.RES_CODE)
        }
    }

    /**
     * Redirects to the UserBookingDashBoardActivity
     */
    override fun onBackPressed() {
        startActivity(Intent(this, UserBookingsDashboardActivity::class.java))
        finish()
    }

    /**
     * function calls the ViewModel of blockedList
     */
    private fun loadBlocking() {
        showProgressDialog(this)
        mBlockedDashboardViewModel.getBlockedList()
    }

    /**
     * function calls the ViewModel of Unblock
     */
    private fun unblockRoom(mBookingId: Int) {
        showProgressDialog(this)
        mBlockedDashboardViewModel.unBlockRoom(mBookingId)
    }
}
