package com.nineleaps.conferenceroombooking.splashScreen.ui

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import butterknife.BindView
import com.crashlytics.android.Crashlytics
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.nineleaps.conferenceroombooking.BaseActivity
import com.nineleaps.conferenceroombooking.BaseApplication
import com.nineleaps.conferenceroombooking.Helper.NetworkState
import com.nineleaps.conferenceroombooking.R
import com.nineleaps.conferenceroombooking.bookingDashboard.ui.UserBookingsDashboardActivity
import com.nineleaps.conferenceroombooking.checkConnection.NoInternetConnectionActivity
import com.nineleaps.conferenceroombooking.signIn.ui.SignIn
import com.nineleaps.conferenceroombooking.splashScreen.repository.GetRoleOfUser
import com.nineleaps.conferenceroombooking.splashScreen.viewModel.GetRoleOfUserViewModel
import com.nineleaps.conferenceroombooking.utils.Constants
import com.nineleaps.conferenceroombooking.utils.ShowDialogForSessionExpired
import com.nineleaps.conferenceroombooking.utils.ShowToast
import com.orhanobut.hawk.Hawk
import io.fabric.sdk.android.Fabric
import javax.inject.Inject


class SplashScreen : BaseActivity() {


    @Inject
    lateinit var mCheckegistationRepo: GetRoleOfUser

    @BindView(R.id.splash_screen_progress_bar)
    lateinit var mProgressBar: ProgressBar

    private lateinit var mGetRoleOfUserViewModel: GetRoleOfUserViewModel

    private var email = ""

    /**
     * Declaring Global variables and binned view for using butter knife
     */
    override fun getLayoutResource(): Int {
        return R.layout.activity_splash_screen
    }

    /**
     * Passing the Layout Resource to the Base Activity
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fabric.with(this, Crashlytics())
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        init()
        observeData()
        timeDelayForSplashScreen()
    }

    /**
     * TimeDelay for the Splash Screen
     */
    private fun timeDelayForSplashScreen() {
        val logoHandler = Handler()
        val logoRunnable = Runnable {
            val account = GoogleSignIn.getLastSignedInAccount(this)
            when {
                account != null -> when {
                    NetworkState.appIsConnectedToInternet(this) -> {
                        email = account.email!!
                        checkRegistration()
                    }

                    else -> {
                        val i = Intent(this@SplashScreen, NoInternetConnectionActivity::class.java)
                        startActivityForResult(i, Constants.RES_CODE)
                    }
                }
                else -> signIn()
            }
        }
        logoHandler.postDelayed(logoRunnable, 3000)
    }

    /**
     * on Activity Result when the Network State is available
     */
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.RES_CODE && resultCode == Activity.RESULT_OK) {
            checkRegistration()
        }
    }

    /**
     * function make a request to backend for checking whether the user is registered or not
     */
    private fun checkRegistration() {
        mProgressBar.visibility = View.VISIBLE
        mGetRoleOfUserViewModel.getUserRole(email)
    }

    /**
     * initialize all lateinit variables
     */
    fun init() {
        initComponentForSplashScreen()
        mGetRoleOfUserViewModel = ViewModelProviders.of(this).get(GetRoleOfUserViewModel::class.java)
        initGetRoleOfUserRepo()
    }

    /**
     * Dependency Injection of Splas
     */
    private fun initComponentForSplashScreen() {
        (application as BaseApplication).getmAppComponent()?.inject(this)
    }

    /**
     *  Get the Role Repository instance from the View Model
     */
    private fun initGetRoleOfUserRepo() {
        mGetRoleOfUserViewModel.setGetRoleOfUserRepo(mCheckegistationRepo)
    }


    private fun observeData() {
        mGetRoleOfUserViewModel.returnSuccessCodeForUserROle().observe(this, Observer {
            mProgressBar.visibility = View.GONE
            setValueForSharedPreference(it)
        })
        mGetRoleOfUserViewModel.returnFailureCodeForUserRole().observe(this, Observer {
            mProgressBar.visibility = View.GONE
            when (it) {
                Constants.INVALID_TOKEN, Constants.UNPROCESSABLE, Constants.FORBIDDEN -> signIn()
                else -> {
                    Toast.makeText(this, "" + it, Toast.LENGTH_SHORT).show()
                    ShowToast.show(this, it as Int)
                    finish()
                }
            }
        })
    }

    /**
     * pass the intent for the SignIn Activity
     */
    private fun signIn() {
        ShowDialogForSessionExpired.signOut(this, this)
        startActivity(Intent(applicationContext, SignIn::class.java))
        finish()
    }

    /**
     * according to the backend status function will redirect control to some other activity
     */
    private fun goToNextActivity(code: Int?) {
        when (code) {
            Constants.HR_CODE, Constants.Facility_Manager, Constants.MANAGER_CODE, Constants.EMPLOYEE_CODE -> {
                startActivity(Intent(this@SplashScreen, UserBookingsDashboardActivity::class.java))
                finish()
            }
            else -> {
                val builder = AlertDialog.Builder(this@SplashScreen)
                builder.setTitle(getString(R.string.error))
                builder.setMessage(getString(R.string.restart_app))
                builder.setPositiveButton(getString(R.string.ok)) { _, _ ->
                    finish()
                }
                val dialog: AlertDialog = builder.create()
                dialog.show()
            }
        }
    }

    /**
     * set value in shared preference
     */
    private fun setValueForSharedPreference(it: Int) {
        Hawk.put(Constants.ROLE_CODE,it)
        goToNextActivity(it)
    }
}
