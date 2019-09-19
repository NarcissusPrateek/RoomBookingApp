package com.nineleaps.conferenceroombooking.manageConferenceRoom.repository

import com.nineleaps.conferenceroombooking.ConferenceRoomDashboard.repository.ManageConferenceRoomRepository
import com.nineleaps.conferenceroombooking.Models.ConferenceList
import com.nineleaps.conferenceroombooking.services.ConferenceService
import com.nineleaps.conferenceroombooking.services.ResponseListener
import com.nineleaps.conferenceroombooking.services.RestClient
import com.nineleaps.conferenceroombooking.utils.Constants
import okhttp3.ResponseBody
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@RunWith(MockitoJUnitRunner::class)
class ManageConferenceRoomRepositoryTest {

    @InjectMocks
    lateinit var managerConferenceRoomRepository: ManageConferenceRoomRepository

    @Captor
    lateinit var argumentCaptorForResponseBody: ArgumentCaptor<Callback<ResponseBody>>

    @Captor
    lateinit var argumentCaptorForConferenceList: ArgumentCaptor<Callback<List<ConferenceList>>>

    @Mock
    lateinit var conferenceService: ConferenceService

    @Mock
    lateinit var callMockForListOfConference: Call<List<ConferenceList>>

    @Mock
    lateinit var callMockForResponseBody: Call<ResponseBody>
    @Mock
    lateinit var responseBody: ResponseBody

    val listOfConference = ArrayList<ConferenceList>()

    @Mock
    lateinit var listener: ResponseListener

    val t = Throwable()

//-----------------------------------Get Conference List----------------------------------------------------------------
    /**
     * Success For Get Conference List
     */
    @Test
    fun testSuccessGetConferenceList() {
        RestClient.setMockService(conferenceService)
        `when`(conferenceService.conferenceList(0)).thenReturn(callMockForListOfConference)
        managerConferenceRoomRepository.getConferenceRoomList(0, listener)
        verify(callMockForListOfConference).enqueue(argumentCaptorForConferenceList.capture())
        argumentCaptorForConferenceList.value.onResponse(
            callMockForListOfConference,
            Response.success(Constants.OK_RESPONSE, listOfConference)
        )
        verify(listener, times(1)).onSuccess(listOfConference)
    }

    /**
     * Success But Failure For Get Conference list
     */
    @Test
    fun testSuccessButFailureForGetConferenceList(){
        RestClient.setMockService(conferenceService)
        `when`(conferenceService.conferenceList(0)).thenReturn(callMockForListOfConference)
        managerConferenceRoomRepository.getConferenceRoomList(0, listener)
        verify(callMockForListOfConference).enqueue(argumentCaptorForConferenceList.capture())
        argumentCaptorForConferenceList.value.onResponse(
            callMockForListOfConference,
            Response.success(Constants.NO_CONTENT_FOUND, listOfConference)
        )
        verify(listener, times(1)).onFailure(Constants.NO_CONTENT_FOUND)
    }

    /**
     * Failure For Get Conference Room List
     */
    @Test
    fun testFailureForGetConferenceList(){
        RestClient.setMockService(conferenceService)
        `when`(conferenceService.conferenceList(0)).thenReturn(callMockForListOfConference)
        managerConferenceRoomRepository.getConferenceRoomList(0, listener)
        verify(callMockForListOfConference).enqueue(argumentCaptorForConferenceList.capture())
        argumentCaptorForConferenceList.value.onFailure(
            callMockForListOfConference,
            t
        )
        verify(listener, times(1)).onFailure(Constants.INTERNAL_SERVER_ERROR)
    }

//-------------------------------------Delete Building------------------------------------------------------------------
    /**
     * Success For Delete Building
     */
    @Test
    fun testSuccessForDeleteBuilding(){
        RestClient.setMockService(conferenceService)
        `when`(conferenceService.deleteRoom(0)).thenReturn(callMockForResponseBody)
        managerConferenceRoomRepository.deleteBuilding(0, listener)
        verify(callMockForResponseBody).enqueue(argumentCaptorForResponseBody.capture())
        argumentCaptorForResponseBody.value.onResponse(
            callMockForResponseBody,
            Response.success(Constants.OK_RESPONSE, responseBody)
        )
        verify(listener, times(1)).onSuccess(Constants.OK_RESPONSE)
    }

    /**
     * Success But Failure For Delete Building
     */
    @Test
    fun testSuccessButFailureForDeleteBuilding(){
        RestClient.setMockService(conferenceService)
        `when`(conferenceService.deleteRoom(0)).thenReturn(callMockForResponseBody)
        managerConferenceRoomRepository.deleteBuilding(0, listener)
        verify(callMockForResponseBody).enqueue(argumentCaptorForResponseBody.capture())
        argumentCaptorForResponseBody.value.onResponse(
            callMockForResponseBody,
            Response.success(Constants.NO_CONTENT_FOUND, responseBody)
        )
        verify(listener, times(1)).onSuccess(Constants.NO_CONTENT_FOUND)
    }

    /**
     * Failure For Delete Building
     */
    @Test
    fun testFailureForDeleteBuilding(){
        RestClient.setMockService(conferenceService)
        `when`(conferenceService.deleteRoom(0)).thenReturn(callMockForResponseBody)
        managerConferenceRoomRepository.deleteBuilding(0, listener)
        verify(callMockForResponseBody).enqueue(argumentCaptorForResponseBody.capture())
        argumentCaptorForResponseBody.value.onFailure(
            callMockForResponseBody,
            t
        )
        verify(listener, times(1)).onFailure(Constants.INTERNAL_SERVER_ERROR)
    }
}