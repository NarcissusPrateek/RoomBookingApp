package com.nineleaps.conferenceroombooking.manageBuildings.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.OnClick
import com.nineleaps.conferenceroombooking.BaseActivity
import com.nineleaps.conferenceroombooking.BaseApplication
import com.nineleaps.conferenceroombooking.manageConferenceRoom.ui.ConferenceDashBoard
import com.nineleaps.conferenceroombooking.Helper.NetworkState
import com.nineleaps.conferenceroombooking.R
import com.nineleaps.conferenceroombooking.addBuilding.ui.AddingBuilding
import com.nineleaps.conferenceroombooking.bookingDashboard.ui.UserBookingsDashboardActivity
import com.nineleaps.conferenceroombooking.checkConnection.NoInternetConnectionActivity
import com.nineleaps.conferenceroombooking.manageBuildings.adapter.BuildingDashboardAdapter
import com.nineleaps.conferenceroombooking.manageBuildings.repository.BuildingsRepository
import com.nineleaps.conferenceroombooking.manageBuildings.viewModel.BuildingViewModel
import com.nineleaps.conferenceroombooking.model.Building
import com.nineleaps.conferenceroombooking.utils.*
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_building_dashboard.*
import javax.inject.Inject

class BuildingDashboard : BaseActivity() {
    /**
     * Declaring Global variables and butter knife
     */
    @Inject
    lateinit var buildingRepository: BuildingsRepository

    @BindView(R.id.buidingRecyclerView)
    lateinit var recyclerView: RecyclerView

    private lateinit var buildingAdapter: BuildingDashboardAdapter

    private lateinit var mBuildingsViewModel: BuildingViewModel

    private var buildingId: Int = 0

    private var mBuildingList = ArrayList<Building>()

    /**
     * Passing the Layout Resource to the Base Activity
     */
    override fun getLayoutResource(): Int {
        return R.layout.activity_building_dashboard
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
     * onClick on this button goes to AddBuilding Activity
     */
    @OnClick(R.id.button_add_building)
    fun addBuildingFloatingButton() {
        startActivity(Intent(this, AddingBuilding::class.java).putExtra("FLAG", false))
    }

    /**
     * Restart the Activity
     */
    override fun onRestart() {
        super.onRestart()
        showProgressDialog(this)
        mBuildingsViewModel.getBuildingList()
    }

    /**
     * initialize objects
     */
    private fun init() {
        initActionBar(getString(R.string.Building_Dashboard))
        initComponent()
        initLateInitializerVariables()
        initRepository()
        initRecyclerView()
        when {
            NetworkState.appIsConnectedToInternet(this) -> getViewModel()
            else -> {
                val i = Intent(this@BuildingDashboard, NoInternetConnectionActivity::class.java)
                startActivityForResult(i, Constants.RES_CODE)
            }
        }

    }

    /**
     * Initializing the RecyclerView
     */
    private fun initRecyclerView() {
        buildingAdapter =
            BuildingDashboardAdapter(this, mBuildingList, object : BuildingDashboardAdapter.BtnClickListener {
                override fun onBtnClick(buildingId: String?, buildingName: String?) {
                    val intent = Intent(this@BuildingDashboard, ConferenceDashBoard::class.java)
                    intent.putExtra(Constants.EXTRA_BUILDING_ID, buildingId)
                    startActivity(intent)
                }
            },
                object : BuildingDashboardAdapter.EditClickListener {
                    override fun onEditBtnClick(position: Int) {
                        val intent = Intent(this@BuildingDashboard, AddingBuilding::class.java)
                        intent.putExtra(Constants.BUILDING_ID, mBuildingList[position].buildingId!!.toInt())
                        intent.putExtra(Constants.BUILDING_NAME, mBuildingList[position].buildingName)
                        intent.putExtra(Constants.BUILDING_PLACE, mBuildingList[position].buildingPlace)
                        intent.putExtra(Constants.LOCATION_ID, mBuildingList[position].locationId!!.toInt())
                        intent.putExtra(Constants.FLAG, true)
                        startActivity(intent)
                    }
                },
                object : BuildingDashboardAdapter.DeleteClickListner {
                    override fun onDeleteClick(position: Int) {
                        buildingId = mBuildingList[position].buildingId!!.toInt()
                        showDeleteDialog(buildingId)
                    }

                })
        recyclerView.adapter = buildingAdapter
    }

    /*
    Dependency Injection of Building Dashboard
     */
    private fun initComponent() {
        (application as BaseApplication).getmAppComponent()?.inject(this)
    }

    /*
        Get the Building Repository instance from the View Model
     */
    private fun initRepository() {
        mBuildingsViewModel.setBuildingRepository(buildingRepository)
    }


    /*
      Initialize View Model
    */
    private fun initLateInitializerVariables() {
        mBuildingsViewModel = ViewModelProviders.of(this).get(BuildingViewModel::class.java)
    }

    /**
     * onActivity Result when Internet is available
     */
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.RES_CODE && resultCode == Activity.RESULT_OK) {
            getViewModel()
        }
    }


    /**
     * observe data from server
     */
    private fun observeData() {
        mBuildingsViewModel.returnMBuildingSuccess().observe(this, Observer {

            if (it.isEmpty()) {
                mBuildingList.clear()
                buildingAdapter.notifyDataSetChanged()
                empty_view_blocked.visibility = View.VISIBLE
                hideProgressDialog()
                Toasty.info(this, getString(R.string.please_add_building), Toasty.LENGTH_SHORT).show()
            } else {
                mBuildingList.clear()
                mBuildingList.addAll(it)
                empty_view_blocked.visibility = View.GONE
                buildingAdapter.notifyDataSetChanged()
                hideProgressDialog()
            }
        })
        mBuildingsViewModel.returnMBuildingFailure().observe(this, Observer {
            hideProgressDialog()
            if (it == Constants.UNPROCESSABLE || it == Constants.INVALID_TOKEN || it == Constants.FORBIDDEN) {
                ShowDialogForSessionExpired.showAlert(this, BuildingDashboard())
            } else {
                ShowToast.show(this, it as Int)
                finish()
            }
        })

        mBuildingsViewModel.returnSuccessForDeleteBuilding().observe(this, Observer {
            hideProgressDialog()
            Toasty.success(this, getString(R.string.successfull_deletion)).show()
            mBuildingsViewModel.getBuildingList()
        })

        mBuildingsViewModel.returnFailureForDeleteBuilding().observe(this, Observer {
            hideProgressDialog()
            if (it == Constants.UNPROCESSABLE || it == Constants.INVALID_TOKEN || it == Constants.FORBIDDEN) {
                ShowDialogForSessionExpired.showAlert(this, UserBookingsDashboardActivity())
            } else {
                ShowToast.show(this, it as Int)
            }
        })
    }

    /**
     * Show Alert Dialog Box to confirm the building to delete
     */
    private fun showDeleteDialog(buildingId: Int) {
        val dialog = GetAleretDialog.getDialog(this, "Delete", "Are you sure you want to delete the Building")
        dialog.setPositiveButton(R.string.ok) { _, _ ->
            mBuildingsViewModel.deleteBuilding(buildingId)
            getViewModel()
        }
        dialog.setNegativeButton(R.string.cancel) { _, _ ->

        }
        val builder = GetAleretDialog.showDialog(dialog)
        ColorOfDialogButton.setColorOfDialogButton(builder)
    }


    /**
     * setting the adapter by passing the data into it and implementing a Interface BtnClickListner of BuildingAdapter class
     */
    private fun getViewModel() {
        showProgressDialog(this)
        // making API call
        mBuildingsViewModel.getBuildingList()
    }
}
