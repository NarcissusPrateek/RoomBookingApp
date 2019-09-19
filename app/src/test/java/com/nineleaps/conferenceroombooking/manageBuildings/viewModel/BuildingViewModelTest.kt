package com.nineleaps.conferenceroombooking.manageBuildings.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nineleaps.conferenceroombooking.manageBuildings.repository.BuildingsRepository
import com.nineleaps.conferenceroombooking.model.Building
import com.nineleaps.conferenceroombooking.services.ResponseListener
import com.nineleaps.conferenceroombooking.utils.Constants
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class BuildingViewModelTest{

    @Rule
    @JvmField
    var executor = InstantTaskExecutorRule()

    val listenerCaptor = argumentCaptor<ResponseListener>()
    val repoMock = mock(BuildingsRepository::class.java)
    val viewModel = BuildingViewModel()
    val listBuilding = ArrayList<Building>()
    @Before
    fun setUp(){
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun testReturnMBuildingSuccess(){
        doReturn(listBuilding).`when`(spy(viewModel.returnMBuildingSuccess())).value
    }

    @Test
    fun testReturnMBuildingFailure(){
        doReturn(Constants.INTERNAL_SERVER_ERROR).`when`(spy(viewModel.returnMBuildingFailure())).value
    }

    @Test
    fun testReturnSuccessForDeleteBuilding(){
        doReturn(Constants.OK_RESPONSE).`when`(spy(viewModel.returnSuccessForDeleteBuilding())).value
    }

    @Test
    fun testReturnFailureForDeleteBuilding(){
        doReturn(Constants.INTERNAL_SERVER_ERROR).`when`(spy(viewModel.returnFailureForDeleteBuilding())).value
    }

    @Test
    fun testGetBuildingList(){
        viewModel.setBuildingRepository(repoMock)
        doNothing().`when`(repoMock).getBuildingList(com.nhaarman.mockitokotlin2.any())
        viewModel.getBuildingList()
        verify(repoMock, times(1)).getBuildingList(listenerCaptor.capture())
        listenerCaptor.firstValue.onSuccess(listBuilding)
        listenerCaptor.firstValue.onFailure(Constants.INTERNAL_SERVER_ERROR)
    }

    @Test
    fun testDeleteBuilding(){
        viewModel.setBuildingRepository(repoMock)
        doNothing().`when`(repoMock).deleteBuilding(com.nhaarman.mockitokotlin2.any(),com.nhaarman.mockitokotlin2.any())
        viewModel.deleteBuilding(0)
        verify(repoMock, times(1)).deleteBuilding(com.nhaarman.mockitokotlin2.any(),listenerCaptor.capture())
        listenerCaptor.firstValue.onSuccess(Constants.OK_RESPONSE)
        listenerCaptor.firstValue.onFailure(Constants.INTERNAL_SERVER_ERROR)
    }
}