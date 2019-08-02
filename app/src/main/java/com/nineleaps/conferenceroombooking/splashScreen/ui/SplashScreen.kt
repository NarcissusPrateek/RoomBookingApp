@file:Suppress("DEPRECATION")

package com.nineleaps.conferenceroombooking.splashScreen.ui

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.crashlytics.android.Crashlytics
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.nineleaps.conferenceroombooking.BaseApplication
import com.nineleaps.conferenceroombooking.Helper.NetworkState
import com.nineleaps.conferenceroombooking.R
import com.nineleaps.conferenceroombooking.SignIn
import com.nineleaps.conferenceroombooking.bookingDashboard.ui.UserBookingsDashboardActivity
import com.nineleaps.conferenceroombooking.checkConnection.NoInternetConnectionActivity
import com.nineleaps.conferenceroombooking.splashScreen.repository.GetRoleOfUser
import com.nineleaps.conferenceroombooking.splashScreen.viewModel.GetRoleOfUserViewModel
import com.nineleaps.conferenceroombooking.utils.Constants
import com.nineleaps.conferenceroombooking.utils.GetProgress
import com.nineleaps.conferenceroombooking.utils.ShowDialogForSessionExpired
import com.nineleaps.conferenceroombooking.utils.ShowToast
import io.fabric.sdk.android.Fabric
import javax.inject.Inject


class SplashScreen : AppCompatActivity() {

    @Inject
    lateinit var mCheckegistationRepo: GetRoleOfUser

    private lateinit var mProgressBar: ProgressBar
    private lateinit var prefs: SharedPreferences
    private lateinit var progressDialog: ProgressDialog
    private lateinit var mGetRoleOfUserViewModel: GetRoleOfUserViewModel
    private var email = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fabric.with(this, Crashlytics())
        setContentView(R.layout.activity_splash_screen)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        init()
        observeData()
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
        mProgressBar = findViewById(R.id.splash_screen_progress_bar)
        progressDialog = GetProgress.getProgressDialog(getString(R.string.progress_message), this)
        prefs = getSharedPreferences(Constants.PREFERENCE, Context.MODE_PRIVATE)
        mGetRoleOfUserViewModel = ViewModelProviders.of(this).get(GetRoleOfUserViewModel::class.java)
        initGetRoleOfUserRepo()
    }

    private fun initComponentForSplashScreen() {
        (application as BaseApplication).getmAppComponent()?.inject(this)
    }

    private fun initGetRoleOfUserRepo() {
        mGetRoleOfUserViewModel.setGetRoleOfUserRepo(mCheckegistationRepo)
    }


    private fun observeData() {
        mGetRoleOfUserViewModel.returnSuccessCodeForUserROle().observe(this, Observer {
            mProgressBar.visibility = View.GONE
            setValueForSharedPreference(it)
            Log.i("Role", it.toString())
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
        val editor = prefs.edit()
        editor.putInt(Constants.ROLE_CODE, it)
        editor.apply()
        goToNextActivity(it)
    }
}
