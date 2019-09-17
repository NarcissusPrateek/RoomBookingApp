package com.nineleaps.conferenceroombooking.addBuilding.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doNothing
import com.nineleaps.conferenceroombooking.addBuilding.repository.AddBuildingRepository
import com.nineleaps.conferenceroombooking.model.AddBuilding
import com.nineleaps.conferenceroombooking.model.Location
import com.nineleaps.conferenceroombooking.services.ResponseListener
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class AddBuildingViewModelTest {

    @InjectMocks
    lateinit var mAddBuildingViewModel: AddBuildingViewModel

    @Mock
    lateinit var mAddBuildingRepository: AddBuildingRepository

    @Rule @JvmField
    var executor = InstantTaskExecutorRule()

    val list= emptyList<Location>()

    val listenerCaptor = argumentCaptor<ResponseListener>()
    val repoMock = mock(AddBuildingRepository::class.java)
    val viewModel = AddBuildingViewModel()
    val building = AddBuilding()


    @Before
    fun init() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun testInitializeRepository() {
        lenient().doNothing().`when`(spy(mAddBuildingViewModel)).setBuildingRepository(mAddBuildingRepository)

    }

    @Test
    fun returnSuccessForAddBuilding() {
        lenient().doReturn(201).`when`(spy(mAddBuildingViewModel.returnSuccessForAddBuilding())).value
    }

    @Test
    fun returnFailureForAddBuilding() {
        lenient().doReturn(406).`when`(spy(mAddBuildingViewModel.returnFailureForAddBuilding())).value
    }


    @Test
    fun returnSuccessForUpdateBuilding() {
        lenient().doReturn(200).`when`(spy(mAddBuildingViewModel.returnSuccessForUpdateBuilding())).value
    }

    @Test
    fun returnFailureForUpdateBuilding() {
        lenient().doReturn(402).`when`(spy(mAddBuildingViewModel.returnFailureForUpdateBuilding())).value
    }

    @Test
    fun returnMGetLocationList() {
        lenient().doReturn("{}").`when`(spy(mAddBuildingViewModel.returnMGetLocationList())).value
    }

    @Test
    fun returnMFailureForGetLocation() {
        lenient().doReturn("{}").`when`(spy(mAddBuildingViewModel.returnMFailureForGetLocation())).value
    }

    @Test
    fun testForAddingBuildingDetails() {

        viewModel.setBuildingRepository(repoMock)
        doNothing().`when`(repoMock).addBuildingDetails(com.nhaarman.mockitokotlin2.any(),com.nhaarman.mockitokotlin2.any())
        viewModel.addBuildingDetails(building)
        verify(repoMock, times(1)).addBuildingDetails(com.nhaarman.mockitokotlin2.any(),listenerCaptor.capture())

        listenerCaptor.firstValue.onSuccess(200)
        listenerCaptor.firstValue.onFailure("Failure")
    }

    @Test
    fun testForUpdateBuildingDetails(){
        viewModel.setBuildingRepository(repoMock)
        doNothing().`when`(repoMock).updateBuildingDetails(com.nhaarman.mockitokotlin2.any(),com.nhaarman.mockitokotlin2.any())
        viewModel.updateBuildingDetails(building)
        verify(repoMock, times(1)).updateBuildingDetails(com.nhaarman.mockitokotlin2.any(),listenerCaptor.capture())

        listenerCaptor.firstValue.onSuccess(200)
        listenerCaptor.firstValue.onFailure("Failure")
    }

    @Test
    fun testForGetLocation(){
        viewModel.setBuildingRepository(repoMock)
        doNothing().`when`(repoMock).getLocationDetails(com.nhaarman.mockitokotlin2.any())
        viewModel.getLocation()
        verify(repoMock, times(1)).getLocationDetails(listenerCaptor.capture())

        listenerCaptor.firstValue.onSuccess(list)
        listenerCaptor.firstValue.onFailure("Failure")
    }
}