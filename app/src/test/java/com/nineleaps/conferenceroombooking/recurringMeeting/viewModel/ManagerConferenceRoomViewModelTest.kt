package com.nineleaps.conferenceroombooking.recurringMeeting.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.conferenceroomapp.model.ManagerConference
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nineleaps.conferenceroombooking.ViewModel.ManagerConferenceRoomViewModel
import com.nineleaps.conferenceroombooking.model.RoomDetails
import com.nineleaps.conferenceroombooking.recurringMeeting.repository.ManagerConferenceRoomRepository
import com.nineleaps.conferenceroombooking.services.ResponseListener
import com.nineleaps.conferenceroombooking.utils.Constants
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class ManagerConferenceRoomViewModelTest{

    @Rule
    @JvmField
    val executor = InstantTaskExecutorRule()

    val repo = mock(ManagerConferenceRoomRepository::class.java)
    val viewModel = ManagerConferenceRoomViewModel()
    val captor = argumentCaptor<ResponseListener>()
    val room = ManagerConference()

    @Test
    fun testSuccessReturn(){
        doReturn("{}").`when`(spy(viewModel.returnSuccess())).value
    }

    @Test
    fun testFailureReturn(){
        doReturn(Constants.INTERNAL_SERVER_ERROR).`when`(spy(viewModel.returnFailure())).value
    }

    @Test
    fun testGetConferenceRoomList(){
        viewModel.setManagerConferenceRoomRepo(repo)
        doNothing().`when`(repo).getConferenceRoomListForRecurringMeeting(com.nhaarman.mockitokotlin2.any(),com.nhaarman.mockitokotlin2.any())
        viewModel.getConferenceRoomList(room)
        verify(repo, times(1)).getConferenceRoomListForRecurringMeeting(com.nhaarman.mockitokotlin2.any(),captor.capture())
        captor.firstValue.onSuccess(emptyList<RoomDetails>())
        captor.firstValue.onFailure(Constants.INTERNAL_SERVER_ERROR)
    }
}