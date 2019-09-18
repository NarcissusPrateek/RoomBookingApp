package com.nineleaps.conferenceroombooking.booking.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.conferenceroomapp.model.InputDetailsForRoom
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nineleaps.conferenceroombooking.booking.repository.ConferenceRoomRepository
import com.nineleaps.conferenceroombooking.model.RoomDetails
import com.nineleaps.conferenceroombooking.services.ResponseListener
import com.nineleaps.conferenceroombooking.utils.Constants
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class ConferenceRoomViewModelTest{
    @Rule
    @JvmField
    var executor = InstantTaskExecutorRule()

    val listenerCaptor = argumentCaptor<ResponseListener>()
    val repoMock = mock(ConferenceRoomRepository::class.java)
    val viewModel = ConferenceRoomViewModel()
    val mInputDetailsForRoom = InputDetailsForRoom()
    val list = emptyList<RoomDetails>()


    @Test
    fun testReturnSuccess(){
        lenient().doReturn("{}").`when`(spy(viewModel.returnSuccess())).value
    }

    @Test
    fun testReturnFailure(){
        lenient().doReturn(Constants.INTERNAL_SERVER_ERROR).`when`(spy(viewModel.returnFailure())).value
    }

    @Test
    fun testGetConferenceRoom(){
        viewModel.setConferenceRoomRepo(repoMock)
        doNothing().`when`(repoMock).getConferenceRoomList(com.nhaarman.mockitokotlin2.any(),com.nhaarman.mockitokotlin2.any())
        viewModel.getConferenceRoomList(mInputDetailsForRoom)
        verify(repoMock, times(1)).getConferenceRoomList(com.nhaarman.mockitokotlin2.any(),listenerCaptor.capture())
        listenerCaptor.firstValue.onFailure(Constants.INTERNAL_SERVER_ERROR)
        listenerCaptor.firstValue.onSuccess(list)
    }

}