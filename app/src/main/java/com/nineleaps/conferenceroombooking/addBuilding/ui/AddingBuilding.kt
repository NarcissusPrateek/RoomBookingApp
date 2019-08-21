package com.nineleaps.conferenceroombooking.addBuilding.ui

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.Html.fromHtml
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
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
import com.nineleaps.conferenceroombooking.model.Location
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

    private lateinit var mAddBuildingViewModel: AddBuildingViewModel
    private var mAddBuilding = AddBuilding()
    private lateinit var progressDialog: ProgressDialog
    var flag = false
    var mUpdateBuildingDetails = AddBuilding()
    private var locationName ="Select Location"
    private var locationId = -1
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
        if(NetworkState.appIsConnectedToInternet(this)) {
            getLocationDetails()
        } else {
            val i = Intent(this@AddingBuilding, NoInternetConnectionActivity::class.java)
            startActivityForResult(i, Constants.RES_CODE2)
        }
        HideSoftKeyboard.setUpUI(findViewById(R.id.add_building_layout), this)
        HideSoftKeyboard.setUpUI(findViewById(R.id.add_building_layout), this)
        HideSoftKeyboard.setUpUI(findViewById(R.id.location_Spinner), this)
    }

    private fun initComponent() {
        (application as BaseApplication).getmAppComponent()?.inject(this)
    }

    private fun getDataFromIntent() {
        flag = intent.getBooleanExtra(Constants.FLAG, false)
        if (flag) {
            button_add_building.text = getString(R.string.update_button)
            mUpdateBuildingDetails.buildingId = intent.getIntExtra(Constants.BUILDING_ID, 0)
            buildingNameEditText.text =
                Editable.Factory.getInstance().newEditable(intent.getStringExtra(Constants.BUILDING_NAME))
            locationName = intent.getStringExtra(Constants.BUILDING_PLACE)
            locationId = intent.getIntExtra(Constants.LOCATION_ID,0)
            }
    }

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
        FirebaseAnalytic.firebaseAnalytics(
            FirebaseAnalytics.getInstance(this),
            this,
            getString(R.string.Add_Buildings),
            "pratheekbilla1997@gmail.com"
        )
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
        if (resultCode == Activity.RESULT_OK ){
            when (requestCode){
                Constants.RES_CODE ->{
                    addDataToObject(mAddBuilding)
                    addBuild(mAddBuilding)
                }
                Constants.RES_CODE2 ->{
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

        mAddBuildingViewModel.returnMGetLocationList().observe(this, Observer {
            progressDialog.dismiss()
            setSpinner(it)
        })

        mAddBuildingViewModel.returnMFailureForGetLocation().observe(this, Observer {
            progressDialog.dismiss()
            if (it == Constants.UNPROCESSABLE || it == Constants.INVALID_TOKEN || it == Constants.FORBIDDEN) {
                ShowDialogForSessionExpired.showAlert(this, AddingBuilding())
            } else {
                ShowToast.show(this, it as Int)
            }
        })
    }

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
        if (flag){
            val mAdapter = location_Spinner.adapter as ArrayAdapter<String>
            val postion = mAdapter.getPosition(locationName)
            location_Spinner.setSelection(postion)
        }
        location_Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                /**
                 * It selects the first conference room
                 */
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
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
        progressDialog.show()
        mAddBuildingViewModel.addBuildingDetails(mBuilding)
    }

    private fun updateBuildingDetails(mUpdateBuildingDetails: AddBuilding) {
        progressDialog.show()
        mAddBuildingViewModel.updateBuildingDetails(mUpdateBuildingDetails)
    }

    private fun getLocationDetails(){
        progressDialog.show()
        mAddBuildingViewModel.getLocation()
    }
}
