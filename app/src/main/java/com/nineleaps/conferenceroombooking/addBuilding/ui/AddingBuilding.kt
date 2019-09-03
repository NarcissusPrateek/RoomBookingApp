package com.nineleaps.conferenceroombooking.addBuilding.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import butterknife.BindView
import butterknife.OnClick
import com.google.firebase.analytics.FirebaseAnalytics
import com.nineleaps.conferenceroombooking.BaseActivity
import com.nineleaps.conferenceroombooking.BaseApplication
import com.nineleaps.conferenceroombooking.Helper.NetworkState
import com.nineleaps.conferenceroombooking.R
import com.nineleaps.conferenceroombooking.addBuilding.repository.AddBuildingRepository
import com.nineleaps.conferenceroombooking.addBuilding.viewModel.AddBuildingViewModel
import com.nineleaps.conferenceroombooking.checkConnection.NoInternetConnectionActivity
import com.nineleaps.conferenceroombooking.model.AddBuilding
import com.nineleaps.conferenceroombooking.model.Location
import com.nineleaps.conferenceroombooking.utils.Constants
import com.nineleaps.conferenceroombooking.utils.HideSoftKeyboard
import com.nineleaps.conferenceroombooking.utils.ShowDialogForSessionExpired
import com.nineleaps.conferenceroombooking.utils.ShowToast
import com.orhanobut.hawk.Hawk
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_adding_building.*
import javax.inject.Inject


class AddingBuilding : BaseActivity() {

    /**
     * Declaring Global variables and binned view for using butter knife
     */
    @Inject
    lateinit var mAddBuildingRepository: AddBuildingRepository

    @BindView(R.id.edit_text_building_name)
    lateinit var buildingNameEditText: EditText

    private lateinit var mAddBuildingViewModel: AddBuildingViewModel

    private var mAddBuilding = AddBuilding()

    private var flag = false

    private var mUpdateBuildingDetails = AddBuilding()

    private var locationName = "Select Location"

    private lateinit var mFirebaseAnalytics: FirebaseAnalytics

    private var locationId = -1

    /**
     * Passing the Layout Resource to the Base Activity
     */
    override fun getLayoutResource(): Int {
        return R.layout.activity_adding_building
    }

    /**
     * OnCreate Activity initialize related to the Adding Conference
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        getDataFromIntent()
        observeData()
        if (NetworkState.appIsConnectedToInternet(this)) {
            getLocationDetails()
        } else {
            val i = Intent(this@AddingBuilding, NoInternetConnectionActivity::class.java)
            startActivityForResult(i, Constants.RES_CODE2)
        }
    }

    /**
     * initialize all lateinit variables
     */
    fun init() {
        initActionBar(getString(R.string.Add_Buildings))
        initComponent()
        initLateInitializerVariables()
        initAddingBuildingRepository()
        initTextChangeListener()
        buildingNameEditText.requestFocus()
        HideSoftKeyboard.setUpUI(findViewById(R.id.add_building_layout), this)
        HideSoftKeyboard.setUpUI(findViewById(R.id.add_building_layout), this)
        HideSoftKeyboard.setUpUI(findViewById(R.id.location_Spinner), this)
    }

    /*
    Dependency Injection of Add Building
     */
    private fun initComponent() {
        (application as BaseApplication).getmAppComponent()?.inject(this)
    }

    /**
     *Get the Intent from the Data If the Building needs to be Updated
     */
    private fun getDataFromIntent() {
        flag = intent.getBooleanExtra(Constants.FLAG, false)
        if (flag) {
            button_add_building.text = getString(R.string.update_button)
            mUpdateBuildingDetails.buildingId = intent.getIntExtra(Constants.BUILDING_ID, 0)
            buildingNameEditText.text =
                Editable.Factory.getInstance().newEditable(intent.getStringExtra(Constants.BUILDING_NAME))
            locationName = intent.getStringExtra(Constants.BUILDING_PLACE)
            locationId = intent.getIntExtra(Constants.LOCATION_ID, 0)
        }
    }

    /*
        Text Listener in the BuildingName Edit Text
     */
    private fun initTextChangeListener() {
        textChangeListenerOnBuildingName()
    }

    /**
     * add text change listener for the building Name
     */
    private fun textChangeListenerOnBuildingName() {
        buildingNameEditText.addTextChangedListener(object : TextWatcher {
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
     * function will invoke whenever the add button is clicked
     */
    @OnClick(R.id.button_add_building)
    fun getBuildingDetails() {
        HideSoftKeyboard.hideKeyboard(this)
        if (validateInputs()) {
            if (NetworkState.appIsConnectedToInternet(this)) {
                if (flag) {
                    mUpdateBuildingDetails.buildingName = buildingNameEditText.text.toString().trim()
                    mUpdateBuildingDetails.place = locationId
                    updateBuildingDetails(mUpdateBuildingDetails)
                } else {
                    addDataToObject(mAddBuilding)
                    addBuild(mAddBuilding)
                    logFirebaseAnalytics()
                }
            } else {
                val i = Intent(this@AddingBuilding, NoInternetConnectionActivity::class.java)
                startActivityForResult(i, Constants.RES_CODE)
            }
        }
    }

    /*
        Log the Event in the Firebase
     */
    private fun logFirebaseAnalytics() {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        mFirebaseAnalytics.logEvent(getString(R.string.Add_Buildings), null)
        mFirebaseAnalytics.setAnalyticsCollectionEnabled(true)
        mFirebaseAnalytics.setSessionTimeoutDuration(1000000)
        mFirebaseAnalytics.setUserId("pratheekBilla")
        mFirebaseAnalytics.setUserProperty(getString(R.string.Roll_Id), Hawk.get<Int>(Constants.ROLE_CODE).toString())
    }

    /*
        Get the Building Repository instance from the View Model
     */
    private fun initAddingBuildingRepository() {
        mAddBuildingViewModel.setBuildingRepository(mAddBuildingRepository)
    }

    /*
    Initialize View Model
     */
    private fun initLateInitializerVariables() {
        mAddBuildingViewModel = ViewModelProviders.of(this).get(AddBuildingViewModel::class.java)
    }

    /**
     * on Activity Result when the Network State is available
     */
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                Constants.RES_CODE -> {
                    addDataToObject(mAddBuilding)
                    addBuild(mAddBuilding)
                }
                Constants.RES_CODE2 -> {
                    getLocationDetails()
                }
            }
        }
    }

    /**
     * observing data for adding Building
     */
    private fun observeData() {
        mAddBuildingViewModel.returnSuccessForAddBuilding().observe(this, Observer {
            hideProgressDialog()
            Toasty.success(this, getString(R.string.building_added), Toast.LENGTH_SHORT, true).show()
            finish()
        })
        mAddBuildingViewModel.returnFailureForAddBuilding().observe(this, Observer {
            hideProgressDialog()
            if (it == Constants.UNPROCESSABLE || it == Constants.INVALID_TOKEN || it == Constants.FORBIDDEN) {
                ShowDialogForSessionExpired.showAlert(this, AddingBuilding())
            } else if (it == Constants.UNAVAILABLE_SLOT) {
                ShowToast.show(this, Constants.BUILDING_PRESENT)
            } else {
                ShowToast.show(this, it as Int)
            }
        })

        mAddBuildingViewModel.returnSuccessForUpdateBuilding().observe(this, Observer {
            hideProgressDialog()
            Toasty.success(this, getString(R.string.building_details_updated), Toast.LENGTH_SHORT, true).show()
            finish()
        })
        mAddBuildingViewModel.returnFailureForUpdateBuilding().observe(this, Observer {
            hideProgressDialog()
            if (it == Constants.UNPROCESSABLE || it == Constants.INVALID_TOKEN || it == Constants.FORBIDDEN) {
                ShowDialogForSessionExpired.showAlert(this, AddingBuilding())
            } else {
                ShowToast.show(this, it as Int)
            }
        })

        mAddBuildingViewModel.returnMGetLocationList().observe(this, Observer {
            hideProgressDialog()
            setSpinner(it)
        })

        mAddBuildingViewModel.returnMFailureForGetLocation().observe(this, Observer {
            hideProgressDialog()
            if (it == Constants.UNPROCESSABLE || it == Constants.INVALID_TOKEN || it == Constants.FORBIDDEN) {
                ShowDialogForSessionExpired.showAlert(this, AddingBuilding())
            } else {
                ShowToast.show(this, it as Int)
            }
        })
    }

    /**
     * Set the Location into the SPinner
     */
    private fun setSpinner(locationList: List<Location>?) {
        val locationNameList = mutableListOf<String>()
        val locationIdList = mutableListOf<Int>()
        locationNameList.add(getString(R.string.select_location))
        locationIdList.add(-1)
        if (locationList!!.isEmpty()) {
            locationNameList.add(getString(R.string.no_location_available))
            locationIdList.add(-1)
        } else {
            for (location in locationList) {
                locationIdList.add(location.locationId!!)
                locationNameList.add(location.locaionName!!)
            }
        }
        location_Spinner.adapter =
            ArrayAdapter<String>(this, R.layout.spinner_icon, R.id.spinner_text, locationNameList)
        if (flag) {
            val mAdapter = location_Spinner.adapter as ArrayAdapter<String>
            val postion = mAdapter.getPosition(locationName)
            location_Spinner.setSelection(postion)
        }
        location_Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                error_spinner_location_text_view.visibility = View.VISIBLE
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                error_spinner_location_text_view.visibility = View.GONE
                locationId = locationIdList[position]
                locationName = locationNameList[position]
            }

        }
    }

    /**
     *  set values to the different properties of object which is required for api call
     */
    private fun addDataToObject(mAddBuilding: AddBuilding) {
        mAddBuilding.buildingName = buildingNameEditText.text.toString().trim()
        mAddBuilding.place = locationId
    }

    /**
     * validation for field building name for empty condition
     */
    private fun validateBuildingName(): Boolean {
        val input = buildingNameEditText.text.toString().trim()
        return validateEditText(input, building_name_layout)
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
        return if (locationId == -1) {
            error_spinner_location_text_view.visibility = View.VISIBLE
            false
        } else {
            error_spinner_location_text_view.visibility = View.GONE
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
        showProgressDialog(this)
        mAddBuildingViewModel.addBuildingDetails(mBuilding)
    }

    /*
    ViewModel of UpdateBuilding
     */
    private fun updateBuildingDetails(mUpdateBuildingDetails: AddBuilding) {
        showProgressDialog(this)
        mAddBuildingViewModel.updateBuildingDetails(mUpdateBuildingDetails)
    }

    /*
    ViewModel Of getLocation Details
     */
    private fun getLocationDetails() {
        showProgressDialog(this)
        mAddBuildingViewModel.getLocation()
    }
}
