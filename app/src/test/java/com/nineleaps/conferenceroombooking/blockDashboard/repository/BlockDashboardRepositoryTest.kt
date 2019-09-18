package com.nineleaps.conferenceroombooking.blockDashboard.repository

import com.nineleaps.conferenceroombooking.Blocked
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
class BlockDashboardRepositoryTest{
    /**
     * Initialization of Mock Object
     */
    @Captor
    lateinit var argumentCaptor: ArgumentCaptor<Callback<ResponseBody>>

    @Captor
    lateinit var argumentCaptorForListofBlockedDashboard: ArgumentCaptor<Callback<List<Blocked>>>

    @Mock
    lateinit var callMock: Call<ResponseBody>

    @Mock
    lateinit var callMockBlockedList: Call<List<Blocked>>

    @Mock
    lateinit var responseBody: ResponseBody

    @Mock
    lateinit var conferenceService: ConferenceService

    @Mock
    lateinit var listener: ResponseListener

    @Mock
    lateinit var response: List<Blocked>
    @InjectMocks
    lateinit var mBlockDashboardRepository: BlockDashboardRepository

    lateinit var t: Throwable

//------------------------------Get Blocked List------------------------------------------------------------------------
    /**
     * Get Blocked List Success Response 200 Ok
     */
    @Test
    fun testSuccessForGetBlockedList(){
        RestClient.setMockService(conferenceService)
        `when`(conferenceService.getBlockedConference()).thenReturn(callMockBlockedList)
        mBlockDashboardRepository.getBlockedList(listener)
        verify(callMockBlockedList).enqueue(argumentCaptorForListofBlockedDashboard.capture())
        argumentCaptorForListofBlockedDashboard.value.onResponse(
            callMockBlockedList,
            Response.success(Constants.OK_RESPONSE,response)
        )
        verify(listener, times(1)).onSuccess(response)

    }

    /**
     * Get Blocked List For Success Response For 201 Created
     */
    @Test
    fun testSuccessForCreatedGetBlockedList(){
        RestClient.setMockService(conferenceService)
        `when`(conferenceService.getBlockedConference()).thenReturn(callMockBlockedList)
        mBlockDashboardRepository.getBlockedList(listener)
        verify(callMockBlockedList).enqueue(argumentCaptorForListofBlockedDashboard.capture())
        argumentCaptorForListofBlockedDashboard.value.onResponse(
            callMockBlockedList,
            Response.success(Constants.SUCCESSFULLY_CREATED,response)
        )
        verify(listener, times(1)).onSuccess(response)
    }

    /**
     * Get Blocked List For Failure Response For 500
     */
    @Test
    fun testFailureForResponseForGetBlockedList(){
        t = Throwable()
        RestClient.setMockService(conferenceService)
        `when`(conferenceService.getBlockedConference()).thenReturn(callMockBlockedList)
        mBlockDashboardRepository.getBlockedList(listener)
        verify(callMockBlockedList).enqueue(argumentCaptorForListofBlockedDashboard.capture())
        argumentCaptorForListofBlockedDashboard.value.onFailure(
            callMockBlockedList,
            t
        )
        verify(listener, times(1)).onFailure(Constants.INTERNAL_SERVER_ERROR)
    }
    /**
     * Get Blocked List For Success But Failure
     */
    @Test
    fun testSuccessButFailureForGetBlockedList(){
        RestClient.setMockService(conferenceService)
        `when`(conferenceService.getBlockedConference()).thenReturn(callMockBlockedList)
        mBlockDashboardRepository.getBlockedList(listener)
        verify(callMockBlockedList).enqueue(argumentCaptorForListofBlockedDashboard.capture())
        argumentCaptorForListofBlockedDashboard.value.onResponse(
            callMockBlockedList,
            Response.success(Constants.NO_CONTENT_FOUND,response)
        )
        verify(listener, times(1)).onFailure(Constants.NO_CONTENT_FOUND)
    }

//-----------------------------------Unblock Room-----------------------------------------------------------------------
    /**
     * Unblock Room For Success Response of 200
     */
    @Test
    fun testSuccessForResponseForUnblockRoom(){
        RestClient.setMockService(conferenceService)
        `when`(conferenceService.unBlockingConferenceRoom(0)).thenReturn(callMock)
        mBlockDashboardRepository.unblockRoom(0,listener)
        verify(callMock).enqueue(argumentCaptor.capture())
        argumentCaptor.value.onResponse(
            callMock,
            Response.success(Constants.OK_RESPONSE,responseBody)
        )
        verify(listener, times(1)).onSuccess(Constants.OK_RESPONSE)
    }

    /**
     * Unblock Room For Success Response of 201
     */
    @Test
    fun testSuccessForCreatedResponseForUnblockRoom(){
        RestClient.setMockService(conferenceService)
        `when`(conferenceService.unBlockingConferenceRoom(0)).thenReturn(callMock)
        mBlockDashboardRepository.unblockRoom(0,listener)
        verify(callMock).enqueue(argumentCaptor.capture())
        argumentCaptor.value.onResponse(
            callMock,
            Response.success(Constants.SUCCESSFULLY_CREATED,responseBody)
        )
        verify(listener, times(1)).onSuccess(Constants.SUCCESSFULLY_CREATED)
    }

    /**
     * Unblock Room For Failure Response of 500
     */
    @Test
    fun testFailureResponseForUnblockRoom(){
        t = Throwable()
        RestClient.setMockService(conferenceService)
        `when`(conferenceService.unBlockingConferenceRoom(0)).thenReturn(callMock)
        mBlockDashboardRepository.unblockRoom(0,listener)
        verify(callMock).enqueue(argumentCaptor.capture())
        argumentCaptor.value.onFailure(
            callMock,
            t
        )
        verify(listener, times(1)).onFailure(Constants.INTERNAL_SERVER_ERROR)
    }

    /**
     * Unblock Room For Failure Response of 500
     */
    @Test
    fun testSuccessButFailureResponseForUnblockRoom(){
        RestClient.setMockService(conferenceService)
        `when`(conferenceService.unBlockingConferenceRoom(0)).thenReturn(callMock)
        mBlockDashboardRepository.unblockRoom(0,listener)
        verify(callMock).enqueue(argumentCaptor.capture())
        argumentCaptor.value.onResponse(
            callMock,
            Response.success(Constants.NO_CONTENT_FOUND,responseBody)
        )
        verify(listener, times(1)).onFailure(Constants.NO_CONTENT_FOUND)
    }
}