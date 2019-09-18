package com.nineleaps.conferenceroombooking.booking.repository

import com.example.conferenceroomapp.model.InputDetailsForRoom
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nineleaps.conferenceroombooking.model.RoomDetails
import com.nineleaps.conferenceroombooking.services.ConferenceService
import com.nineleaps.conferenceroombooking.services.ResponseListener
import com.nineleaps.conferenceroombooking.services.RestClient
import com.nineleaps.conferenceroombooking.utils.Constants
import okhttp3.ResponseBody
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.*
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@RunWith(MockitoJUnitRunner::class)
class ConferenceRoomRepositoryTest {

    /**
     * Initializing og the Mock objects
     */
    @InjectMocks
    lateinit var mConferenceRoomRepository: ConferenceRoomRepository

    @Captor
    lateinit var argumentCaptorForResponseBody: ArgumentCaptor<Callback<List<RoomDetails>>>

    @Mock
    lateinit var callMock: Call<List<RoomDetails>>

    @Mock
    lateinit var responseBody: List<RoomDetails>

    @Mock
    lateinit var conferenceService: ConferenceService

    @Mock
    lateinit var listener: ResponseListener

    val t = Throwable()

    val mInputDetailsForRoom = InputDetailsForRoom()

//--------------------------------------------Get Conference Room List--------------------------------------------------
    /**
     * GetConferenceRoomList success response for 200 Ok
     */
    @Test
    fun testSuccessOKResponseForGetConferenceRoomList(){
        RestClient.setMockService(conferenceService)
        Mockito.`when`(conferenceService.getConferenceRoomList(mInputDetailsForRoom)).thenReturn(callMock)
        mConferenceRoomRepository.getConferenceRoomList(mInputDetailsForRoom,listener)
        verify(callMock).enqueue(argumentCaptorForResponseBody.capture())
        argumentCaptorForResponseBody.value.onResponse(
            callMock,
            Response.success(Constants.OK_RESPONSE,responseBody)
        )
        verify(listener, times(1)).onSuccess(responseBody)
    }

    /**
     * GetConferenceRoomList success response but failure for 204
     */
    @Test
    fun testSuccessButFailureForGetConferenceRoomList(){
        RestClient.setMockService(conferenceService)
        Mockito.`when`(conferenceService.getConferenceRoomList(mInputDetailsForRoom)).thenReturn(callMock)
        mConferenceRoomRepository.getConferenceRoomList(mInputDetailsForRoom,listener)
        verify(callMock).enqueue(argumentCaptorForResponseBody.capture())
        argumentCaptorForResponseBody.value.onResponse(
            callMock,
            Response.success(Constants.NO_CONTENT_FOUND,responseBody)
        )
        verify(listener, times(1)).onFailure(Constants.NO_CONTENT_FOUND)
    }

    /**
     * GetConferenceRoomList failure response for 500
     */
    @Test
    fun testFailureForGetConferenceRoomList(){
        RestClient.setMockService(conferenceService)
        Mockito.`when`(conferenceService.getConferenceRoomList(mInputDetailsForRoom)).thenReturn(callMock)
        mConferenceRoomRepository.getConferenceRoomList(mInputDetailsForRoom,listener)
        verify(callMock).enqueue(argumentCaptorForResponseBody.capture())
        argumentCaptorForResponseBody.value.onFailure(
            callMock,
            t
        )
        verify(listener, times(1)).onFailure(Constants.INTERNAL_SERVER_ERROR)
    }

}