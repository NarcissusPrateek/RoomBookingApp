package com.nineleaps.conferenceroombooking.addConferenceRoom.ui

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import butterknife.BindView
import butterknife.OnClick
import com.google.android.material.switchmaterial.SwitchMaterial
import com.nineleaps.conferenceroombooking.*
import com.nineleaps.conferenceroombooking.Helper.NetworkState
import com.nineleaps.conferenceroombooking.addConferenceRoom.repository.AddConferenceRepository
import com.nineleaps.conferenceroombooking.addConferenceRoom.viewModel.AddConferenceRoomViewModel
import com.nineleaps.conferenceroombooking.checkConnection.NoInternetConnectionActivity
import com.nineleaps.conferenceroombooking.utils.*
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_adding_conference.*
import javax.inject.Inject

class AddingConference : BaseActivity() {
    /**
     * Declaring Global variables and binned butter knife
     */

    @Inject
    lateinit var mAddRoomRepo: AddConferenceRepository

    @BindView(R.id.conference_Name)
    lateinit var conferenceRoomEditText: EditText

    @BindView(R.id.conference_capacity)
    lateinit var roomCapacity: EditText

    @BindView(R.id.amenities_linear_layout)
    lateinit var amenitiesLinearLayout: LinearLayout

    private lateinit var checkBox: CheckBox

    @BindView(R.id.permission_required)
    lateinit var switchButton: SwitchMaterial

    @BindView(R.id.floor)
    lateinit var floorEditText: EditText

    private lateinit var mAddConferenceRoomViewModel: AddConferenceRoomViewModel

    private var mConferenceRoom = AddConferenceRoom()

    private var amenityName = ArrayList<String>()

    private var amenityId = ArrayList<Int>()

    private lateinit var amenitiesIdForEdit: MutableList<Int>

    private lateinit var amenities: HashMap<Int, String>

    private var checkboxList = ArrayList<CheckBox>()

    private var flag = false

    var roomId = 0

    var floor = 0

    private var mEditRoomDetails = EditRoomDetails()

    private var selectedAmenities = ArrayList<Int>()

    /**
     * Passing the Layout Resource to the Base Activity
     */
    override fun getLayoutResource(): Int {
        return R.layout.activity_adding_conference
    }

    /**
     * OnCreate Activity initialize related to the Adding Conference
     */
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        getIntentData()
        observeData()
    }

    /**
     * Hide Soft KeyBoard when click outside the EditText
     */
    private fun softKeyboard() {
        conferenceRoomEditText.requestFocus()
        HideSoftKeyboard.setUpUI(findViewById(R.id.add_conference_nestedScroll), this)
        HideSoftKeyboard.childUI(findViewById(R.id.add_conference_nestedScroll), this)
        HideSoftKeyboard.setUpUI(findViewById(R.id.permission_required), this)
        HideSoftKeyboard.setUpUI(findViewById(R.id.amenities_linear_layout),this)
    }

    /**
     * initialize all lateinit variables
     */
    fun init() {
        initActionBar(getString(R.string.Add_Room))
        initComponent()
        initTextChangeListener()
        initLateInitializerVariables()
        initRoomRepository()
        getAllAmenities()
        softKeyboard()
    }

    /**
     *Edit Text Change Listener
     */
    private fun initTextChangeListener() {
        textChangeListenerOnRoomName()
        textChangeListenerOnRoomCapacity()
        textChangeListenerOnRoomFloor()
    }

    /*
    Dependency Injection of Add Building
     */
    private fun initComponent() {
        (application as BaseApplication).getmAppComponent()?.inject(this)
    }

    /*
      Get the Conference Repository instance from the View Model
     */
    private fun initRoomRepository() {
        mAddConferenceRoomViewModel.setAddingConferenceRoomRepo(mAddRoomRepo)
    }

    /**
     * Get the Intent Daa for Update
     * (If the Flag is set to false then Add new Conference Room,
     * else Update the Conference Room)
     */
    private fun getIntentData() {
        flag = intent.getBooleanExtra(Constants.FLAG, false)

        if (flag) {

            add_conference_room.text = getString(R.string.update_button)
            mEditRoomDetails = intent.getSerializableExtra(Constants.EXTRA_INTENT_DATA) as EditRoomDetails
            roomId = mEditRoomDetails.mRoomDetail!!.roomId!!
            roomCapacity.text = mEditRoomDetails.mRoomDetail!!.capacity.toString().toEditable()
            val array = mEditRoomDetails.mRoomDetail!!.roomName!!.split(" ")
            conferenceRoomEditText.text = array[0].toEditable()

            if (array.size < 2 || array[1].trim().isEmpty())
                floorEditText.text = "0".toEditable()
            else
                floorEditText.text = array[1].toEditable()

            amenities = mEditRoomDetails.mRoomDetail!!.amenities!!
            amenitiesIdForEdit = amenities.keys.toMutableList()
            switchButton.isChecked = mEditRoomDetails.mRoomDetail!!.permission!! != false

        } else {
            add_conference_room.text = getString(R.string.ADD)
        }
    }

    /**
     * Inititlize View Model
     */
    private fun initLateInitializerVariables() {
        mAddConferenceRoomViewModel = ViewModelProviders.of(this).get(AddConferenceRoomViewModel::class.java)
    }

    /**
     * observing data for adding conference
     */
    @RequiresApi(Build.VERSION_CODES.M)
    fun observeData() {
        mAddConferenceRoomViewModel.returnSuccessForAddingRoom().observe(this, Observer {
            hideProgressDialog()
            Toasty.success(this, getString(R.string.room_add_success), Toast.LENGTH_SHORT, true).show()
            finish()
        })
        mAddConferenceRoomViewModel.returnFailureForAddingRoom().observe(this, Observer {
            hideProgressDialog()
            if (it == Constants.UNPROCESSABLE || it == Constants.INVALID_TOKEN || it == Constants.FORBIDDEN) {
                ShowDialogForSessionExpired.showAlert(this, AddingConference())
            } else {
                ShowToast.show(this, it as Int)
            }
        })

        mAddConferenceRoomViewModel.returnSuccessForUpdateRoom().observe(this, Observer {
            hideProgressDialog()
            Toasty.success(this, getString(R.string.room_details_updated), Toast.LENGTH_SHORT, true).show()
            finish()
        })
        mAddConferenceRoomViewModel.returnFailureForUpdateRoom().observe(this, Observer {
            hideProgressDialog()
            when (it) {
                Constants.UNPROCESSABLE, Constants.INVALID_TOKEN, Constants.FORBIDDEN -> ShowDialogForSessionExpired.showAlert(
                    this,
                    AddingConference()
                )
                Constants.UNAVAILABLE_SLOT -> Toasty.info(
                    this,
                    getString(R.string.room_name_conflict_message),
                    Toasty.LENGTH_SHORT,
                    true
                ).show()
                else -> ShowToast.show(this, it as Int)
            }
        })


        mAddConferenceRoomViewModel.returnSuccesForGetAmenitiesList().observe(this, Observer {
            hideProgressDialog()
            initCheckBox(it)
        })

        mAddConferenceRoomViewModel.returnFailureForGetAllAmeneties().observe(this, Observer {
            hideProgressDialog()
            if (it == Constants.UNPROCESSABLE || it == Constants.INVALID_TOKEN || it == Constants.FORBIDDEN) {
                ShowDialogForSessionExpired.showAlert(this, AddingConference())
            } else {
                ShowToast.show(this, it as Int)
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun initCheckBox(amenitiesList: List<GetAllAmenities>?) {

        for (amenity in amenitiesList!!) {
            amenityName.add(amenity.amenityName!!)
            amenityId.add(amenity.amenityId!!)
        }

        for (name in amenityName.indices) {
            checkBox = CheckBox(this)
            checkBox.text = amenityName[name]
            checkBox.setTextColor(getColor(R.color.textColorGray))
            if (flag && amenitiesIdForEdit.contains(amenityId[name])) {
                checkBox.isChecked = true
            }
            checkboxList.add(checkBox)
            amenitiesLinearLayout.addView(checkBox)

        }


    }

    /**
     * Get the List of Checked Amenities
     */
    private fun checkedCheckBoxList() {
        softKeyboard()
        for (checkbox in checkboxList.indices) {
            if (checkboxList[checkbox].isChecked) {
                selectedAmenities.add(amenityId[checkbox])
            }
        }

    }

    /**
     * function will invoke whenever the add button is clicked
     */
    @OnClick(R.id.add_conference_room)
    fun addRoomButton() {
        checkedCheckBoxList()
        HideSoftKeyboard.hideKeyboard(this)
        if (validateInputs()) {
            if (NetworkState.appIsConnectedToInternet(this)) {
                addDataToObject(mConferenceRoom)
                if (flag) {
                    mConferenceRoom.roomId = roomId
                    mConferenceRoom.newRoomName = conferenceRoomEditText.text.toString().trim()
                    updateRoomDetails()
                } else {
                    addRoom()
                }
            } else {
                val i = Intent(this@AddingConference, NoInternetConnectionActivity::class.java)
                startActivityForResult(i, Constants.RES_CODE)
            }

        }
    }

    // make api call with updated data to update room details
    private fun updateRoomDetails() {
        showProgressDialog(this)
        mAddConferenceRoomViewModel.updateConferenceDetails(mConferenceRoom)
    }

    /**
     * on Activity Result when the Network State is available
     */
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.RES_CODE && resultCode == Activity.RESULT_OK) {
            addDataToObject(mConferenceRoom)
            addRoom()
        }
    }

    /**
     *  set values to the different properties of object which is required for api call
     */
    private fun addDataToObject(mConferenceRoom: AddConferenceRoom) {
        if (flag) {
            mConferenceRoom.bId = mEditRoomDetails.mRoomDetail!!.buildingId
        } else {
            val bundle: Bundle? = intent.extras
            mConferenceRoom.bId = bundle!!.get(Constants.EXTRA_BUILDING_ID)!!.toString().toInt()
        }
        mConferenceRoom.roomName =
            conferenceRoomEditText.text.toString().trim() + " " + floorEditText.text.toString() + Floor.floorToString(
                floorEditText.text.toString().trim().toInt()
            )
        mConferenceRoom.capacity = roomCapacity.text.toString().toInt()
        mConferenceRoom.amenities = selectedAmenities.toMutableList()
        mConferenceRoom.permission = switchButton.isChecked
    }


    /**
     * validation for room employeeList
     */
    private fun validateRoomName(): Boolean {
        val input = conferenceRoomEditText.text.toString().trim()
        return validateEditText(input, room_name_layout_name)
    }

    /**
     * validation for Room Capacity
     */
    private fun validateRoomCapacity(): Boolean {
        val capacity:String = roomCapacity.text.toString().trim()
        return if (capacity.isEmpty()) {
            room_capacity_layout.error = getString(R.string.field_cant_be_empty)
            false
        } else {
            val input = capacity.toLong()
            if (input <= 0 || input > Int.MAX_VALUE) {
                room_capacity_layout.error = getString(R.string.room_capacity_must_be_more_than_0)
                false
            } else {
                room_capacity_layout.error = null
                true
            }
        }
    }

    /**
     * validate all input fields
     */
    private fun validateInputs(): Boolean {
        if (!validateRoomName() or !validateRoomCapacity() or !validateFloorInputs()) {
            return false
        }
        return true
    }

    /**
     * validate for Floor Input
     */
    private fun validateFloorInputs(): Boolean {
        return if (floorEditText.text.toString().trim().isEmpty()) {
            floor_layout.error = getString(R.string.field_cant_be_empty)
            false
        } else {
            val input = floorEditText.text.toString().toLong()
            if (input <= 0 || input > Int.MAX_VALUE) {
                floor_layout.error = getString(R.string.room_capacity_must_be_more_than_0)
                false
            } else {
                floor_layout.error = null
                true
            }
        }

    }

    /**
     * function calls the ViewModel of addingConference and data into the database
     */
    private fun addRoom() {
        showProgressDialog(this)
        mAddConferenceRoomViewModel.addConferenceDetails(mConferenceRoom)
    }

    private fun getAllAmenities() {
        showProgressDialog(this)
        mAddConferenceRoomViewModel.getAmenitiesList()
    }

    /**
     * add text change listener for the room name
     */
    private fun textChangeListenerOnRoomName() {
        conferenceRoomEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // nothing here
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // nothing here
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateRoomName()
            }
        })
    }

    /**
     * add text change listener for the room name
     */
    private fun textChangeListenerOnRoomCapacity() {
        roomCapacity.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // nothing here
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // nothing here
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateRoomCapacity()
            }
        })
    }


    private fun textChangeListenerOnRoomFloor() {
        floorEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // nothing here
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // nothing here
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateFloorInputs()
            }
        })
    }

}
