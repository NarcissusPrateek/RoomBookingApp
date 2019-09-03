package com.nineleaps.conferenceroombooking.signIn.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import butterknife.BindView
import butterknife.OnClick
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import com.nineleaps.conferenceroombooking.BaseActivity
import com.nineleaps.conferenceroombooking.BaseApplication
import com.nineleaps.conferenceroombooking.Helper.GoogleGSO
import com.nineleaps.conferenceroombooking.Helper.NetworkState
import com.nineleaps.conferenceroombooking.R
import com.nineleaps.conferenceroombooking.bookingDashboard.ui.UserBookingsDashboardActivity
import com.nineleaps.conferenceroombooking.checkConnection.NoInternetConnectionActivity
import com.nineleaps.conferenceroombooking.model.SignIn
import com.nineleaps.conferenceroombooking.signIn.repository.CheckRegistrationRepository
import com.nineleaps.conferenceroombooking.signIn.viewModel.CheckRegistrationViewModel
import com.nineleaps.conferenceroombooking.utils.Constants
import com.nineleaps.conferenceroombooking.utils.GetAleretDialog
import com.nineleaps.conferenceroombooking.utils.GetPreference
import com.nineleaps.conferenceroombooking.utils.ShowToast
import com.orhanobut.hawk.Hawk
import javax.inject.Inject

class SignIn : BaseActivity() {

    /**
     * Declaring Global variables and binned view for using butter knife
     */
    @Inject
    lateinit var mCheckRegistrationRepo: CheckRegistrationRepository

    @BindView(R.id.sign_in_progress_bar)
    lateinit var mProgressBar: ProgressBar

    private val RC_SIGN_IN = 0

    private var mGoogleSignInClient: GoogleSignInClient? = null

    private lateinit var mFirebaseAnalytics: FirebaseAnalytics

    private lateinit var mCheckRegistrationViewModel: CheckRegistrationViewModel

    private lateinit var auth: FirebaseAuth

    /**
     * Passing the Layout Resource to the Base Activity
     */
    override fun getLayoutResource(): Int {
        return R.layout.activity_signin
    }

    /**
     * OnCreate Activity initialize related to the SignIn
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        auth = FirebaseAuth.getInstance()
        initialize()
        observeData()
    }

    /**
     * OnClick on SignIn Button
     */
    @OnClick(R.id.sign_in_button)
    fun signIn() {
        startIntentToGoogleSignIn()
    }

    /**
     * function intialize all items of UI, sharedPreference and calls the setAnimationToLayout function to set the animation to the layouts
     */
    fun initialize() {
        initComponentForSignIn()
        mCheckRegistrationViewModel = ViewModelProviders.of(this).get(CheckRegistrationViewModel::class.java)
        initRegistrationRepo()
        initializeGoogleSignIn()
    }


    /*
    Dependency Injection of Sign
     */
    private fun initComponentForSignIn() {
        (application as BaseApplication).getmAppComponent()?.inject(this)
    }

    /**
     *
     */
    private fun initRegistrationRepo() {
        mCheckRegistrationViewModel.setCheckRegistrationRepo(mCheckRegistrationRepo)
    }

    /**
     * function will starts a explict intent for the google sign in
     */
    private fun startIntentToGoogleSignIn() {
        val signInIntent = mGoogleSignInClient!!.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    /**
     * function will initialize the GoogleSignInClient
     */
    private fun initializeGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.firebase_google_sigIn_oAuth_key))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

    }

    /**
     * function will automatically invoked once the control will return from the explict intent and than call another
     * method to do further task
     */
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
        if (requestCode == Constants.RES_CODE && resultCode == Activity.RESULT_OK) {
            checkRegistration()
        }
    }

    /**
     * function will call a another function which connects to the backend.
     */
    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(account!!)
            if (NetworkState.appIsConnectedToInternet(this)) {
                checkRegistration()
            } else {
                val i = Intent(this@SignIn, NoInternetConnectionActivity::class.java)
                startActivityForResult(i, Constants.RES_CODE)
            }
        } catch (e: ApiException) {
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        setTokenToAccessToken(acct.idToken)
    }

    /**
     * Sign out from application
     */
    private fun signOut() {
        val mGoogleSignInClient = GoogleGSO.getGoogleSignInClient(this)
        mGoogleSignInClient.signOut()
            .addOnCompleteListener(this) {
            }
    }

    private fun saveCustomToken(idToken: String?) {
        Hawk.put(getString(R.string.token), "Bearer $idToken")
    }

    /**
     * on back pressed the function will clear the activity stack and will close the application
     */
    override fun onBackPressed() {
        finishAffinity()
        super.onBackPressed()
    }

    /**
     * this function will check whether the user is registered or not
     * if not registered than make an intent to registration activity
     */
    private fun checkRegistration() {
        mProgressBar.visibility = View.VISIBLE
        Hawk.put(getString(R.string.device_Id), FirebaseInstanceId.getInstance().token)
        mCheckRegistrationViewModel.checkRegistration(Hawk.get(getString(R.string.device_Id)))
    }

    private fun setTokenToAccessToken(idToken: String?) {
        Hawk.put(getString(R.string.token), idToken)
    }

    /**
     * observe data from server
     */
    private fun observeData() {
        //positive response from server
        mCheckRegistrationViewModel.returnSuccessCode().observe(this, Observer {
            mProgressBar.visibility = View.GONE
            setValueForSharedPreference(it)
        })
        // Negative response from server
        mCheckRegistrationViewModel.returnFailureCode().observe(this, Observer {
            mProgressBar.visibility = View.GONE
            ShowToast.show(this, it as Int)
            signOut()
        })
    }

    /**
     * a function which will set the value in shared preference
     */
    private fun setValueForSharedPreference(it: SignIn?) {
        Hawk.put(Constants.ROLE_CODE, it!!.statusCode!!.toInt())
        val code: String = it.statusCode.toString()
        saveCustomToken(it.token)
        GetPreference.setJWTToken(this, it.refreshToken!!, it.token!!)
        intentToNextActivity(code.toInt())
    }

    /**
     * this function will intent to some activity according to the received data from backend
     */
    private fun intentToNextActivity(code: Int?) {
        when (code) {
            Constants.HR_CODE, Constants.Facility_Manager, Constants.MANAGER_CODE, Constants.EMPLOYEE_CODE -> {
                startActivity(Intent(this@SignIn, UserBookingsDashboardActivity::class.java))
                finish()
            }
            else -> {
                val builder =
                    GetAleretDialog.getDialog(this, getString(R.string.error), getString(R.string.restart_app))
                builder.setPositiveButton(getString(R.string.ok)) { _, _ ->
                    finish()
                }
                GetAleretDialog.showDialog(builder)
            }
        }
    }
}