package com.nineleaps.conferenceroombooking.bookingDashboard.ui

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import butterknife.BindView
import butterknife.ButterKnife
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.material.navigation.NavigationView
import com.nineleaps.conferenceroombooking.BaseApplication
import com.nineleaps.conferenceroombooking.Helper.GoogleGSO
import com.nineleaps.conferenceroombooking.R
import com.nineleaps.conferenceroombooking.SignIn
import com.nineleaps.conferenceroombooking.blockDashboard.ui.BlockedDashboard
import com.nineleaps.conferenceroombooking.booking.ui.InputDetailsForBookingFragment
import com.nineleaps.conferenceroombooking.bookingDashboard.repository.BookingDashboardRepository
import com.nineleaps.conferenceroombooking.bookingDashboard.viewModel.BookingDashboardViewModel
import com.nineleaps.conferenceroombooking.manageBuildings.ui.BuildingDashboard
import com.nineleaps.conferenceroombooking.recurringMeeting.ui.CancelledBookingFragment
import com.nineleaps.conferenceroombooking.recurringMeeting.ui.PreviousBookingFragment
import com.nineleaps.conferenceroombooking.recurringMeeting.ui.RecurringBookingInputDetails
import com.nineleaps.conferenceroombooking.recurringMeeting.ui.UpcomingBookingFragment
import com.nineleaps.conferenceroombooking.splashScreen.ui.SplashScreen
import com.nineleaps.conferenceroombooking.utils.*
import com.orhanobut.hawk.Hawk
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_main2.*
import kotlinx.android.synthetic.main.activity_user_dashboard.*
import kotlinx.android.synthetic.main.app_bar_main2.*
import kotlinx.android.synthetic.main.nav_header_main2.view.*
import javax.inject.Inject


@Suppress("DEPRECATION")
class UserBookingsDashboardActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    @Inject
    lateinit var mBookingDahBoardRepo: BookingDashboardRepository
    @BindView(R.id.user_booking_dashboard_progress_bar)
    lateinit var mProgressBar:ProgressBar
    private lateinit var acct: GoogleSignInAccount
    private lateinit var mBookingDashBoardViewModel: BookingDashboardViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        ButterKnife.bind(this)
        setNavigationViewItem()
        softKeyBoard()
        init()
        bottom_navigation.setOnNavigationItemSelectedListener { item ->
            var selectedFragment: Fragment? = null
            when (item.itemId) {
                R.id.nav_home -> selectedFragment = UpcomingBookingFragment()
                R.id.nav_previous -> selectedFragment = PreviousBookingFragment()
                R.id.nav_cancelled -> selectedFragment = CancelledBookingFragment()
                R.id.nav_new_booking -> selectedFragment = InputDetailsForBookingFragment()
            }
            if (selectedFragment != null) {
                supportFragmentManager.beginTransaction().replace(
                    R.id.fragment_container,
                    selectedFragment
                ).commit()
            }
            true
        }
        //I added this if statement to keep the selected fragment when rotating the device
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(
                R.id.fragment_container,
                UpcomingBookingFragment()
            ).commit()
        }

    }

    private fun softKeyBoard() {
        HideSoftKeyboard.hideKeyboard(this)
    }


    private fun init() {
        initComponentForBookingDashBoard()
        mBookingDashBoardViewModel = ViewModelProviders.of(this).get(BookingDashboardViewModel::class.java)
        acct = GoogleSignIn.getLastSignedInAccount(this)!!
        initBookingDashBoardRepo()
        observeData()
    }

    private fun initComponentForBookingDashBoard() {
        (application as BaseApplication).getmAppComponent()?.inject(this)
    }

    private fun initBookingDashBoardRepo() {
        mBookingDashBoardViewModel.setBookedRoomDashboardRepo(mBookingDahBoardRepo)
    }


    private fun getPasscode() {
        //progressDialog.show()
        mProgressBar.visibility = View.VISIBLE
        mBookingDashBoardViewModel.getPasscode(false, acct.email!!)
    }

    /**
     * all observer for LiveData
     */
    private fun observeData() {
        /**
         * observing data for booking list
         */
        mBookingDashBoardViewModel.returnPasscode().observe(this, androidx.lifecycle.Observer {
            //progressDialog.dismiss()
            mProgressBar.visibility = View.GONE
            showAlertForPasscode(it)
        })
        mBookingDashBoardViewModel.returnPasscodeFailed().observe(this, androidx.lifecycle.Observer {
           // progressDialog.dismiss()
            mProgressBar.visibility = View.GONE
            if (it == Constants.UNPROCESSABLE || it == Constants.INVALID_TOKEN || it == Constants.FORBIDDEN) {
                showAlert()
            } else {
                ShowToast.show(this, it as Int)
            }
        })
    }


    /**
     * show dialog for passcode
     */
    private fun showAlertForPasscode(passcode: String) {
        val dialog = GetAleretDialog.getDialogForPasscode(
            this,
            getString(R.string.do_not_share),
            passcode
        )
        dialog.setPositiveButton(R.string.ok) { _, _ ->
        }
        dialog.setNeutralButton(getString(R.string.get_new_passcode)) { _, _ ->
            mProgressBar.visibility = View.VISIBLE
            mBookingDashBoardViewModel.getPasscode(true, acct.email!!)
        }
        val builder = GetAleretDialog.showDialog(dialog)
        ColorOfDialogButton.setColorOfDialogButton(builder)
    }

    /**
     * show dialog for session expired
     */
    private fun showAlert() {
        val dialog = GetAleretDialog.getDialog(
            this, getString(R.string.session_expired), "Your session is expired!\n" +
                    getString(R.string.session_expired_messgae)
        )
        dialog.setPositiveButton(R.string.ok) { _, _ ->
            signOut()
        }
        val builder = GetAleretDialog.showDialog(dialog)
        ColorOfDialogButton.setColorOfDialogButton(builder)
    }

    /**
     * this function will set action to the item in navigation drawer like HR or Logout or Project Manager
     */
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        softKeyBoard()
        when (item.itemId) {
            R.id.logout -> {
                showAlertForSignout()
            }
            R.id.HR -> {
                startActivity(Intent(this@UserBookingsDashboardActivity, BlockedDashboard::class.java))
            }
            R.id.project_manager -> {
                startActivity(Intent(this@UserBookingsDashboardActivity, RecurringBookingInputDetails::class.java))
            }
            R.id.hr_add -> {
                startActivity(Intent(this@UserBookingsDashboardActivity, BuildingDashboard::class.java))
            }
            R.id.get_passcode -> {
                getPasscode()
            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    /**
     * deslect item in navigation drawer after selection
     */
    override fun onResume() {
        super.onResume()
        val size = nav_view.menu.size()
        for (i in 0 until size) {
            nav_view.menu.getItem(i).isCheckable = false
        }
    }


    /**
     * this function will set item in navigation drawer according to the data stored in the sharedpreference
     * if the code is 11 than the role is HR
     * if the code is 12 than the role is Project manager
     * else the role is normal user
     */
    private fun setItemInDrawerByRole() {
        val pref = getSharedPreferences(Constants.PREFERENCE, Context.MODE_PRIVATE)
        val code = Hawk.get<Int>(Constants.ROLE_CODE)
        val navMenu = nav_view.menu
        if (code != Constants.MANAGER_CODE) {
            navMenu.findItem(R.id.project_manager).isVisible = false
        }
        if (code != Constants.Facility_Manager) {
            navMenu.findItem(R.id.HR).isVisible = false
            navMenu.findItem(R.id.hr_add).isVisible = false
        }
    }


    /**
     * this function will set items in the navigation view and calls another function for setting the item according to role
     * if there is no image at the particular url provided by google than we will set a dummy imgae
     * else we set the image provided by google and set the employeeList according to the google display employeeList
     */
    @SuppressLint("SetTextI18n")
    fun setNavigationViewItem() {
        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        nav_view.setNavigationItemSelectedListener(this)
        val viewH = nav_view.getHeaderView(0)
        val acct = GoogleSignIn.getLastSignedInAccount(this@UserBookingsDashboardActivity)
        viewH.nv_profile_name.text = "Hello, ${acct!!.displayName}"
        val personPhoto = acct.photoUrl
        viewH.nv_profile_email.text = acct.email
        Glide.with(applicationContext).load(personPhoto).thumbnail(1.0f).into(viewH.nv_profile_image)
        setItemInDrawerByRole()
    }

    /**
     * Sign out from application
     */
    private fun signOut() {
        Hawk.deleteAll()
        val mGoogleSignInClient: GoogleSignInClient = GoogleGSO.getGoogleSignInClient(this)
        mGoogleSignInClient.signOut()
            .addOnCompleteListener(this) {
                Toasty.success(this, getString(R.string.successfully_sign_out), Toasty.LENGTH_SHORT).show()
                startActivity(Intent(this@UserBookingsDashboardActivity, SignIn::class.java))
                finish()
            }
    }

    //clear activity stack
    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

    /**
     * show dialog for sign out
     */
    private fun showAlertForSignout() {
        val dialog = GetAleretDialog.getDialog(
            this, getString(R.string.logout), getString(R.string.logout_message)
        )
        dialog.setPositiveButton(R.string.yes) { _, _ ->
            signOut()
        }
        dialog.setNegativeButton(R.string.cancel) { _, _ ->
            // do nothing
        }
        val builder = GetAleretDialog.showDialog(dialog)
        val mPositiveButton: Button = builder.getButton(DialogInterface.BUTTON_POSITIVE)
        val mNegativeButton: Button = builder.getButton(DialogInterface.BUTTON_NEGATIVE)

        /**
         * for positive button color code red
         */
        mPositiveButton.setBackgroundColor(Color.WHITE)
        mPositiveButton.setTextColor(Color.RED)

        /**
         * for Negative button color code Red
         */
        mNegativeButton.setBackgroundColor(Color.WHITE)
        mNegativeButton.setTextColor(Color.RED)

    }
}