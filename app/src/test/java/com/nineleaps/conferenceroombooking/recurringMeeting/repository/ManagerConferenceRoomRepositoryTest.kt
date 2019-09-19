package com.nineleaps.conferenceroombooking.recurringMeeting.repository

import com.example.conferenceroomapp.model.ManagerConference
import com.nineleaps.conferenceroombooking.model.RoomDetails
import com.nineleaps.conferenceroombooking.services.ConferenceService
import com.nineleaps.conferenceroombooking.services.ResponseListener
import com.nineleaps.conferenceroombooking.services.RestClient
import com.nineleaps.conferenceroombooking.utils.Constants
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mockito.*
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@RunWith(MockitoJUnitRunner::class)
class ManagerConferenceRoomRepositoryTest{
    /**
     * Initailization of Mock Objects
     */
    @InjectMocks
    lateinit var managerConferenceRoomRepository: ManagerConferenceRoomRepository

    @Captor
    lateinit var argumentCaptorForListOfRoom: ArgumentCaptor<Callback<List<RoomDetails>>>

    @Mock
    lateinit var conferenceService: ConferenceService

    @Mock
    lateinit var callMockForRoomDetails:Call<List<RoomDetails>>

    @Mock
    lateinit var responseListener: ResponseListener

    val t = Throwable()

    val emptylist = emptyList<RoomDetails>()

    val room = ManagerConference()

//----------------------------Get Conference ROom List For Recurring Meeting--------------------------------------------
    /**
     * Success For room list for recurring meeting
     */
    @Test
    fun testSuccessForRoomList(){
        RestClient.setMockService(conferenceService)
        `when`(conferenceService.getConferenceRoomListForRecurring(room)).thenReturn(callMockForRoomDetails)
        managerConferenceRoomRepository.getConferenceRoomListForRecurringMeeting(room,responseListener)
        verify(callMockForRoomDetails).enqueue(argumentCaptorForListOfRoom.capture())
        argumentCaptorForListOfRoom.value.onResponse(
            callMockForRoomDetails,
            Response.success(Constants.OK_RESPONSE,emptylist)
        )
        verify(responseListener, times(1)).onSuccess(emptylist)
    }

    /**
     * Success but Failure For room list of Recurring Meeting
     */
    @Test
    fun testSuccessButFailureForRoomList(){
        RestClient.setMockService(conferenceService)
        `when`(conferenceService.getConferenceRoomListForRecurring(room)).thenReturn(callMockForRoomDetails)
        managerConferenceRoomRepository.getConferenceRoomListForRecurringMeeting(room,responseListener)
        verify(callMockForRoomDetails).enqueue(argumentCaptorForListOfRoom.capture())
        argumentCaptorForListOfRoom.value.onResponse(
            callMockForRoomDetails,
            Response.success(Constants.NO_CONTENT_FOUND,emptylist)
        )
        verify(responseListener, times(1)).onFailure(Constants.NO_CONTENT_FOUND)
    }

    /**
     * Failure For RoomList of Recurring Meeting
     */
    @Test
    fun testFailureForRoomList(){
        RestClient.setMockService(conferenceService)
        `when`(conferenceService.getConferenceRoomListForRecurring(room)).thenReturn(callMockForRoomDetails)
        managerConferenceRoomRepository.getConferenceRoomListForRecurringMeeting(room,responseListener)
        verify(callMockForRoomDetails).enqueue(argumentCaptorForListOfRoom.capture())
        argumentCaptorForListOfRoom.value.onFailure(
            callMockForRoomDetails,
            t
        )
        verify(responseListener, times(1)).onFailure(Constants.INTERNAL_SERVER_ERROR)
    }
}