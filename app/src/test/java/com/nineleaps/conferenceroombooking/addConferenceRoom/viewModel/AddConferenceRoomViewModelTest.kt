package com.nineleaps.conferenceroombooking.addConferenceRoom.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nineleaps.conferenceroombooking.AddConferenceRoom
import com.nineleaps.conferenceroombooking.GetAllAmenities
import com.nineleaps.conferenceroombooking.addConferenceRoom.repository.AddConferenceRepository
import com.nineleaps.conferenceroombooking.services.ResponseListener
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class AddConferenceRoomViewModelTest {

    @Rule
    @JvmField
    var executor = InstantTaskExecutorRule()

    val list = emptyList<GetAllAmenities>()

    val listenerCaptor = argumentCaptor<ResponseListener>()
    val repoMock = mock(AddConferenceRepository::class.java)
    val viewModel = AddConferenceRoomViewModel()
    val conferenceRoom = AddConferenceRoom()

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun testReturnSuccessForAddingRoom() {
        lenient().doReturn(200).`when`(spy(viewModel.returnSuccessForAddingRoom())).value
    }

    @Test
    fun testReturnFailureForAddingRoom() {
        lenient().doReturn(500).`when`(spy(viewModel.returnFailureForAddingRoom())).value
    }

    @Test
    fun testReturnSuccessForUpdateRoom() {
        lenient().doReturn(200).`when`(spy(viewModel.returnSuccessForUpdateRoom())).value
    }

    @Test
    fun testReturnFailureForUpdateRoom() {
        lenient().doReturn(500).`when`(spy(viewModel.returnFailureForUpdateRoom())).value
    }

    @Test
    fun testReturnSuccessForGetAmenities() {
        lenient().doReturn("{}").`when`(spy(viewModel.returnSuccesForGetAmenitiesList())).value
    }

    @Test
    fun testReturnFailureForGetAmenities() {
        lenient().doReturn(500).`when`(spy(viewModel.returnFailureForGetAllAmeneties())).value
    }

    @Test
    fun testForAddConferenceDetails() {
        viewModel.setAddingConferenceRoomRepo(repoMock)
        doNothing().`when`(repoMock)
            .addConferenceDetails(com.nhaarman.mockitokotlin2.any(), com.nhaarman.mockitokotlin2.any())
        viewModel.addConferenceDetails(conferenceRoom)
        verify(repoMock, times(1)).addConferenceDetails(com.nhaarman.mockitokotlin2.any(), listenerCaptor.capture())
        listenerCaptor.firstValue.onSuccess(200)
        listenerCaptor.firstValue.onFailure("failure")
    }

    @Test
    fun testForUpdateConferenceDetails() {
        viewModel.setAddingConferenceRoomRepo(repoMock)
        doNothing().`when`(repoMock)
            .updateConferenceDetails(com.nhaarman.mockitokotlin2.any(), com.nhaarman.mockitokotlin2.any())
        viewModel.updateConferenceDetails(conferenceRoom)
        verify(repoMock, times(1)).updateConferenceDetails(com.nhaarman.mockitokotlin2.any(), listenerCaptor.capture())
        listenerCaptor.firstValue.onSuccess(200)
        listenerCaptor.firstValue.onFailure("failure")
    }

    @Test
    fun testForGetAllAmenities() {
        viewModel.setAddingConferenceRoomRepo(repoMock)
        doNothing().`when`(repoMock).getAmenitiesDetails(com.nhaarman.mockitokotlin2.any())
        viewModel.getAmenitiesList()
        verify(repoMock, times(1)).getAmenitiesDetails(listenerCaptor.capture())
        listenerCaptor.firstValue.onSuccess(list)
        listenerCaptor.firstValue.onFailure("failure")
    }
}