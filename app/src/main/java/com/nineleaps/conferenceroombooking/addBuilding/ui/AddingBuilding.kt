package com.nineleaps.conferenceroombooking.addBuilding.ui

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.Html.fromHtml
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.google.firebase.analytics.FirebaseAnalytics
import com.nineleaps.conferenceroombooking.BaseApplication
import com.nineleaps.conferenceroombooking.Helper.NetworkState
import com.nineleaps.conferenceroombooking.R
import com.nineleaps.conferenceroombooking.addBuilding.repository.AddBuildingRepository
import com.nineleaps.conferenceroombooking.addBuilding.viewModel.AddBuildingViewModel
import com.nineleaps.conferenceroombooking.checkConnection.NoInternetConnectionActivity
import com.nineleaps.conferenceroombooking.model.AddBuilding
import com.nineleaps.conferenceroombooking.utils.*
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_adding_building.*
import javax.inject.Inject

@Suppress("DEPRECATION")
class AddingBuilding : AppCompatActivity() {


    @Inject
    lateinit var mAddBuildingRepository: AddBuildingRepository

    /**
     * Declaring Global variables and binned view for using butter knife
     */
    @BindView(R.id.edit_text_building_name)
    lateinit var buildingNameEditText: EditText
    @BindView(R.id.edit_text_building_place)
    lateinit var buildingPlaceEditText: EditText
    private lateinit var mAddBuildingViewModel: AddBuildingViewModel
    private var mAddBuilding = AddBuilding()
    private lateinit var progressDialog: ProgressDialog
    var flag = false
    var mUpdateBuildingDetails = AddBuilding()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adding_building)
        ButterKnife.bind(this)
        init()
        getDataFromIntent()
        observeData()
    }


    /**
     * initialize all lateinit variables
     */
    fun init() {
        initActionBar()
        initComponent()
        initLateInitializerVariables()
        initAddingBuildingRepository()
        initTextChangeListener()
        buildingNameEditText.requestFocus()
        HideSoftKeyboard.setUpUI(findViewById(R.id.add_building_layout),this)
        HideSoftKeyboard.childUI(findViewById(R.id.add_building_layout),this)
    }

   private fun initComponent() {
        (application as BaseApplication).getmAppComponent()?.inject(this)
    }

    private fun getDataFromIntent() {
        flag = intent.getBooleanExtra(Constants.FLAG, false)
        if (flag) {
            button_add_building.text = getString(R.string.update_button)
            mUpdateBuildingDetails.buildingId = intent.getIntExtra(Constants.BUILDING_ID, 0)
            buildingNameEditText.text = Editable.Factory.getInstance().newEditable(intent.getStringExtra(Constants.BUILDING_NAME))
            buildingPlaceEditText.text = Editable.Factory.getInstance().newEditable(intent.getStringExtra(Constants.BUILDING_PLACE))
        }
    }

    private fun initTextChangeListener() {
        textChangeListenerOnBuildingName()
        textChangeListenerOnBuildingPlace()
    }

    /**
     * add text change listener for the building Name
     */
    private fun textChangeListenerOnBuildingName() {
        buildingNameEditText.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // nothing here
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // nothing here
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateBuildingName()
            }
        })
    }
    /**
     * add text change listener for the building place
     */
    private fun textChangeListenerOnBuildingPlace() {
        buildingPlaceEditText.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // nothing here
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // nothing here
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateBuildingPlace()
            }
        })
    }


    /**
     * function will invoke whenever the add button is clicked
     */
    @OnClick(R.id.button_add_building)
    fun getBuildingDetails() {
        FirebaseAnalytic.firebaseAnalytics(FirebaseAnalytics.getInstance(this),this,"Add Building","pratheekbilla1997@gmail.com")
        HideSoftKeyboard.hideKeyboard(this)
        if (validateInputs()) {
            if (NetworkState.appIsConnectedToInternet(this)) {
                if(flag) {
                    mUpdateBuildingDetails.buildingName = buildingNameEditText.text.toString().trim()
                    mUpdateBuildingDetails.place =  buildingPlaceEditText.text.toString().trim()
                    updateBuildingDetails(mUpdateBuildingDetails)
                } else {
                    addDataToObject(mAddBuilding)
                    addBuild(mAddBuilding)
                }
            } else {
                val i = Intent(this@AddingBuilding, NoInternetConnectionActivity::class.java)
                startActivityForResult(i, Constants.RES_CODE)
            }
        }
    }




    private fun initAddingBuildingRepository() {
        mAddBuildingViewModel.setBuildingRepository(mAddBuildingRepository)
    }



    private fun initActionBar() {
        val actionBar = supportActionBar
        actionBar!!.title = fromHtml("<font color=\"#FFFFFF\">" + getString(R.string.Add_Buildings) + "</font>")
    }

    private fun initLateInitializerVariables() {
        progressDialog = GetProgress.getProgressDialog(getString(R.string.progress_message_processing), this)
        mAddBuildingViewModel = ViewModelProviders.of(this).get(AddBuildingViewModel::class.java)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.RES_CODE && resultCode == Activity.RESULT_OK) {
            addDataToObject(mAddBuilding)
            addBuild(mAddBuilding)
        }
    }

    /**
     * observing data for adding Building
     */
    private fun observeData() {
        mAddBuildingViewModel.returnSuccessForAddBuilding().observe(this, Observer {
            progressDialog.dismiss()
            Toasty.success(this, getString(R.string.building_added), Toast.LENGTH_SHORT, true).show()
            finish()
        })
        mAddBuildingViewModel.returnFailureForAddBuilding().observe(this, Observer {
            progressDialog.dismiss()
            if (it == Constants.UNPROCESSABLE || it == Constants.INVALID_TOKEN || it == Constants.FORBIDDEN) {
                ShowDialogForSessionExpired.showAlert(this, AddingBuilding())
            } else {
                ShowToast.show(this, it as Int)
            }
        })

        mAddBuildingViewModel.returnSuccessForUpdateBuilding().observe(this, Observer {
            progressDialog.dismiss()
            Toasty.success(this, getString(R.string.building_details_updated), Toast.LENGTH_SHORT, true).show()
            finish()
        })
        mAddBuildingViewModel.returnFailureForUpdateBuilding().observe(this, Observer {
            progressDialog.dismiss()
            if (it == Constants.UNPROCESSABLE || it == Constants.INVALID_TOKEN || it == Constants.FORBIDDEN) {
                ShowDialogForSessionExpired.showAlert(this, AddingBuilding())
            } else {
                ShowToast.show(this, it as Int)
            }
        })
    }

    /**
     *  set values to the different properties of object which is required for api call
     */
    private fun addDataToObject(mAddBuilding: AddBuilding) {
        mAddBuilding.buildingName = buildingNameEditText.text.toString().trim()
        mAddBuilding.place = buildingPlaceEditText.text.toString().trim()
    }

    /**
     * validation for field building name for empty condition
     */
    private fun validateBuildingName(): Boolean {
        val input = buildingNameEditText.text.toString().trim()
        return if (input.isEmpty()) {
            building_name_layout.error = getString(R.string.field_cant_be_empty)
            false
        } else {
            building_name_layout.error = null
            true
        }
    }

    /**
     * validate all input fields
     */
    private fun validateInputs(): Boolean {
        if (!validateBuildingName() or !validateBuildingPlace()) {
            return false
        }
        return true
    }
    /**
     * validation for building place for empty condition
     */
    private fun validateBuildingPlace(): Boolean {
        val input = buildingPlaceEditText.text.toString().trim()
        return if (input.isEmpty()) {
            location_layout.error = getString(R.string.field_cant_be_empty)
            false
        } else {
            location_layout.error = null
            true
        }
    }


    /**
     * function calls the ViewModel of addingBuilding and send data to the backend
     */
    private fun addBuild(mBuilding: AddBuilding) {

        /**
         * Get the progress dialog from GetProgress Helper class
         */
        progressDialog.show()
        mAddBuildingViewModel.addBuildingDetails(mBuilding)
    }

    private fun updateBuildingDetails(mUpdateBuildingDetails: AddBuilding) {
        progressDialog.show()
        mAddBuildingViewModel.updateBuildingDetails(mUpdateBuildingDetails)
    }
}
