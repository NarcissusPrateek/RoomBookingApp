package com.nineleaps.conferenceroombooking.addBuilding.viewModel

import com.nhaarman.mockitokotlin2.doNothing
import com.nhaarman.mockitokotlin2.whenever
import com.nineleaps.conferenceroombooking.addBuilding.repository.AddBuildingRepository
import com.nineleaps.conferenceroombooking.model.AddBuilding
import com.nineleaps.conferenceroombooking.services.ResponseListener
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class AddBuildingViewModelTest {

    @InjectMocks
    lateinit var mAddBuildingViewModel: AddBuildingViewModel

    @Mock
    lateinit var mAddBuildingRepository: AddBuildingRepository
    @Mock
    var listener : ResponseListener = object:ResponseListener {
        override fun onSuccess(success: Any) {

        }

        override fun onFailure(failure: Any) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }
//

    /**
     * Test Cases For AddingBuilding
     */

    @Test
    fun testInitializeRepository() {
        doNothing().`when`(spy(mAddBuildingViewModel)).setBuildingRepository(mAddBuildingRepository)
        mAddBuildingViewModel.setBuildingRepository(mAddBuildingRepository)
    }


    @Test
    fun testAddBuildingDetailsSuccess() {
        val addBuilding = AddBuilding()
        addBuilding.buildingName = "Main"
        addBuilding.place = 1
        whenever(mAddBuildingViewModel.addBuildingDetails(addBuilding)).thenAnswer {
            (it.arguments[1] as ResponseListener).onSuccess(200)
        }


        /*`when`(mAddBuildingViewModel.addBuildingDetails(addBuilding)).then {
            doNothing()
        }*/
    }

    @Test
    fun testAddBuildingDetailsFailure() {
        val addBuilding = AddBuilding()
        `when`(mAddBuildingViewModel.addBuildingDetails(addBuilding)).then {
            doNothing()
        }
    }

    @Test
    fun returnSuccessForAddBuilding() {
        doReturn(201).`when`(spy(mAddBuildingViewModel.returnSuccessForAddBuilding())).value
    }

    @Test
    fun returnFailureForAddBuilding() {
        doReturn(406).`when`(spy(mAddBuildingViewModel.returnFailureForAddBuilding())).value

    }

//----------------------------------------------------------------------------------------------------------------------

    /**
     * Test Cases For UpdateBuilding
     */
    @Test
    fun testUpdateBuildingDetailsForSucess() {
        val addBuilding = AddBuilding()
        addBuilding.buildingName = "Main"
        addBuilding.place = 1
        addBuilding.buildingId = 1
        `when`(mAddBuildingViewModel.updateBuildingDetails(addBuilding)).then {
            doNothing()
        }
    }

    @Test
    fun testUpdateBuildingDetailsForFailure() {
        val addBuilding = AddBuilding()
        addBuilding.buildingName = "Main"
        addBuilding.place = 1
        `when`(mAddBuildingViewModel.updateBuildingDetails(addBuilding)).then {
            doNothing()
        }
    }

    @Test
    fun returnSuccessForUpdateBuilding() {
        doReturn(200).`when`(spy(mAddBuildingViewModel.returnSuccessForUpdateBuilding())).value
    }

    @Test
    fun returnFailureForUpdateBuilding() {
        doReturn(402).`when`(spy(mAddBuildingViewModel.returnFailureForUpdateBuilding())).value

    }

//----------------------------------------------------------------------------------------------------------------------

    /**
     * Test Cases for GetLocation
     */

    @Test
    fun testGetLocationForSucess() {
        `when`(mAddBuildingViewModel.getLocation()).then{
            doNothing()
        }
    }

    @Test
    fun returnMGetLocationList() {
        doReturn("{}").`when`(spy(mAddBuildingViewModel.returnMGetLocationList())).value
    }

    @Test
    fun returnMFailureForGetLocation() {
        doReturn("{}").`when`(spy(mAddBuildingViewModel.returnMFailureForGetLocation())).value

    }


}