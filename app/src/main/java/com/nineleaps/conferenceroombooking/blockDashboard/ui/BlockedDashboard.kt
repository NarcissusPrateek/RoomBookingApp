package com.nineleaps.conferenceroombooking.blockDashboard.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Html.fromHtml
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.analytics.FirebaseAnalytics
import com.nineleaps.conferenceroombooking.BaseApplication
import com.nineleaps.conferenceroombooking.Blocked
import com.nineleaps.conferenceroombooking.Helper.BlockedDashboardNew
import com.nineleaps.conferenceroombooking.Helper.NetworkState
import com.nineleaps.conferenceroombooking.R
import com.nineleaps.conferenceroombooking.ViewModel.BlockedDashboardViewModel
import com.nineleaps.conferenceroombooking.blockDashboard.repository.BlockDashboardRepository
import com.nineleaps.conferenceroombooking.blockRoom.ui.BlockConferenceRoomActivity
import com.nineleaps.conferenceroombooking.bookingDashboard.ui.UserBookingsDashboardActivity
import com.nineleaps.conferenceroombooking.checkConnection.NoInternetConnectionActivity
import com.nineleaps.conferenceroombooking.utils.*
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_blocked_dashboard.*
import javax.inject.Inject

@SuppressLint("Registered")
@Suppress("DEPRECATION")
class BlockedDashboard : AppCompatActivity() {

    /**
     * Declaring Global variables and butterknife
     */

    @Inject
    lateinit var mBlockDashBoardRepo: BlockDashboardRepository

    @BindView(R.id.block_recyclerView)
    lateinit var recyclerView: RecyclerView
    @BindView(R.id.block_dashboard_progress_bar)
    lateinit var mProgressBar: ProgressBar
    @BindView(R.id.block_dashboard_refresh_layout)
    lateinit var refreshLayout: SwipeRefreshLayout
    private lateinit var acct: GoogleSignInAccount
    private lateinit var blockedAdapter: BlockedDashboardNew
    private lateinit var mBlockedDashboardViewModel: BlockedDashboardViewModel
    private lateinit var mFirebaseAnalytics: FirebaseAnalytics
    private lateinit var progressDialog: ProgressDialog
    private var mBlockRoomList = ArrayList<Blocked>()
    private var bookingId = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blocked_dashboard)
        ButterKnife.bind(this)
        init()
        observeData()
    }

    private fun initActionBar() {
        val actionBar = supportActionBar
        actionBar!!.title = fromHtml("<font color=\"#FFFFFF\">" + getString(R.string.Blocked_Rooms) + "</font>")
    }

    private fun initLateInitializerVariables() {
        mBlockedDashboardViewModel = ViewModelProviders.of(this).get(BlockedDashboardViewModel::class.java)
        progressDialog = GetProgress.getProgressDialog(getString(R.string.progress_message), this)
    }

    /**
     * Initialize late init fields
     */
    @SuppressLint("ResourceAsColor")
    fun init() {
        initActionBar()
        initComponentForBlockDashBoard()
        initLateInitializerVariables()
        initBlockDashBoardRepo()
        acct = GoogleSignIn.getLastSignedInAccount(this)!!
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        refreshLayout.setColorSchemeColors(R.color.colorPrimary)
        when {
            NetworkState.appIsConnectedToInternet(this) -> loadBlocking()
            else -> {
                val i = Intent(this, NoInternetConnectionActivity::class.java)
                startActivityForResult(i, Constants.RES_CODE)
            }
        }
        refreshOnPull()
    }

    private fun initComponentForBlockDashBoard() {
        (application as BaseApplication).getmAppComponent()?.inject(this)
    }


    private fun initBlockDashBoardRepo() {
        mBlockedDashboardViewModel.setBlockedRoomDashboardRepo(mBlockDashBoardRepo)
    }

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
            mBlockedDashboardViewModel.getBlockedList()
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
            mProgressBar.visibility = View.GONE
            empty_view_blocked.visibility = View.GONE
            r2_block_dashboard.setBackgroundColor(Color.parseColor("#F7F7F7"))
            mBlockRoomList.clear()
            mBlockRoomList.addAll(it)
            setAdapter()
        })
        mBlockedDashboardViewModel.returnFailureCodeFromBlockedApi().observe(this, Observer {
            refreshLayout.isRefreshing = false
            mProgressBar.visibility = View.GONE
            when (it) {
                Constants.INVALID_TOKEN -> ShowDialogForSessionExpired.showAlert(this, BlockedDashboard())
                Constants.NO_CONTENT_FOUND -> {
                    empty_view_blocked.visibility = View.VISIBLE
                    r2_block_dashboard.setBackgroundColor(Color.parseColor("#FFFFFF"))
                    mBlockRoomList.clear()
                    setAdapter()
                }
                else -> ShowToast.show(this, it as Int)
            }
        })
        /**
         * observing data for Unblocking
         */
        mBlockedDashboardViewModel.returnSuccessCodeForUnBlockRoom().observe(this, Observer {
            progressDialog.dismiss()
            Toasty.success(this,getString(R.string.room_unblocked), Toast.LENGTH_SHORT, true).show()
            mBlockedDashboardViewModel.getBlockedList()
        })
        mBlockedDashboardViewModel.returnFailureCodeForUnBlockRoom().observe(this, Observer {
            progressDialog.dismiss()
            when (it) {
                Constants.INVALID_TOKEN -> ShowDialogForSessionExpired.showAlert(this, BlockedDashboard())
                else -> ShowToast.show(this, it as Int)
            }
        })
    }


    private fun setAdapter() {
        blockedAdapter = BlockedDashboardNew(
            mBlockRoomList,
            this,
            object : BlockedDashboardNew.UnblockRoomListener {
                override fun onClickOfUnblock(bookingId: Int) {
                    when {
                        NetworkState.appIsConnectedToInternet(this@BlockedDashboard) -> confirmAlertDialogToUnblockRoom(bookingId)
                        else -> {
                            val i = Intent(this@BlockedDashboard, NoInternetConnectionActivity::class.java)
                            startActivityForResult(i, Constants.RES_CODE2)
                        }
                    }
                }
            })
        recyclerView.adapter = blockedAdapter
    }

    private fun confirmAlertDialogToUnblockRoom(mBookingId: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.confirm))
        builder.setMessage(getString(R.string.unblock_room_confirmation_message))
        builder.setPositiveButton(getString(R.string.yes)) { _, _ ->
            if(NetworkState.appIsConnectedToInternet(this)) {
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

    private fun unBlockLogFirebase() {
        val unBlockBundle = Bundle()
        mFirebaseAnalytics.logEvent(getString(R.string.UnBlockRoom),unBlockBundle)
        mFirebaseAnalytics.setAnalyticsCollectionEnabled(true)
        mFirebaseAnalytics.setMinimumSessionDuration(5000)
        mFirebaseAnalytics.setSessionTimeoutDuration(1000000)
        mFirebaseAnalytics.setUserId(acct.email)
        mFirebaseAnalytics.setUserProperty(getString(R.string.Roll_Id),GetPreference.getRoleIdFromPreference(this).toString())
    }


    @OnClick(R.id.maintenance)
    fun blockConferenceActivity() {
        val maintenanceIntent = Intent(applicationContext, BlockConferenceRoomActivity::class.java)
        startActivity(maintenanceIntent)
    }

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
        mProgressBar.visibility = View.VISIBLE
        mBlockedDashboardViewModel.getBlockedList()
    }

    /**
     * function calls the ViewModel of Unblock
     */
    private fun unblockRoom(mBookingId: Int) {
        progressDialog.show()
        mBlockedDashboardViewModel.unBlockRoom(mBookingId)
    }
}
