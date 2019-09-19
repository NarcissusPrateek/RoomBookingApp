package com.nineleaps.conferenceroombooking.manageConferenceRoom.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nineleaps.conferenceroombooking.ConferenceRoomDashboard.repository.ManageConferenceRoomRepository
import com.nineleaps.conferenceroombooking.ConferenceRoomDashboard.viewModel.ManageConferenceRoomViewModel
import com.nineleaps.conferenceroombooking.Models.ConferenceList
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
class ManageConferenceRoomViewModelTest{
    @Rule
    @JvmField
    val executor = InstantTaskExecutorRule()

    val repoMock = mock(ManageConferenceRoomRepository::class.java)
    val viewModel = ManageConferenceRoomViewModel()
    val listenerCaptor = argumentCaptor<ResponseListener>()
    val listCOnference = ArrayList<ConferenceList>()

    @Before
    fun setUp(){
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun testReturnConferenceList(){
        doReturn("{}").`when`(spy(viewModel.returnConferenceRoomList())).value
    }

    @Test
    fun testReturnFailureForConferenceRoom(){
        doReturn(Constants.INTERNAL_SERVER_ERROR).`when`(spy(viewModel.returnFailureForConferenceRoom())).value
    }

    @Test
    fun testReturnSuccessForDeleteRoom(){
        doReturn(Constants.OK_RESPONSE).`when`(spy(viewModel.returnSuccessForDeleteRoom())).value
    }

    @Test
    fun testReturnFailureForDeleteRoom(){
        doReturn(Constants.INTERNAL_SERVER_ERROR).`when`(spy(viewModel.returnFailureForDeleteRoom())).value
    }

    @Test
    fun testgetConferenceRoomList(){
        viewModel.setManageRoomRepo(repoMock)
        doNothing().`when`(repoMock).getConferenceRoomList(com.nhaarman.mockitokotlin2.any(),com.nhaarman.mockitokotlin2.any())
        viewModel.getConferenceRoomList(0)
        verify(repoMock, times(1)).getConferenceRoomList(com.nhaarman.mockitokotlin2.any(),listenerCaptor.capture())
        listenerCaptor.firstValue.onSuccess(listCOnference)
        listenerCaptor.firstValue.onFailure(Constants.INTERNAL_SERVER_ERROR)
    }
    @Test
    fun testDeleteConferenceRoom(){
        viewModel.setManageRoomRepo(repoMock)
        doNothing().`when`(repoMock).deleteBuilding(com.nhaarman.mockitokotlin2.any(),com.nhaarman.mockitokotlin2.any())
        viewModel.deleteConferenceRoom(0)
        verify(repoMock, times(1)).deleteBuilding(com.nhaarman.mockitokotlin2.any(),listenerCaptor.capture())
        listenerCaptor.firstValue.onSuccess(Constants.OK_RESPONSE)
        listenerCaptor.firstValue.onFailure(Constants.INTERNAL_SERVER_ERROR)
    }
}