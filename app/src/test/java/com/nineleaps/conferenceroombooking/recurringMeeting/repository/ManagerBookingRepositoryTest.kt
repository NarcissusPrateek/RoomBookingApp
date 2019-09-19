package com.nineleaps.conferenceroombooking.recurringMeeting.repository

import com.nineleaps.conferenceroombooking.model.ManagerBooking
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
class ManagerBookingRepositoryTest {
    /**
     * Initailization of Mock Objects
     */
    @InjectMocks
    lateinit var managerBookingRepository: ManagerBookingRepository

    @Captor
    lateinit var argumentCaptorForResponseBody: ArgumentCaptor<Callback<ResponseBody>>

    @Mock
    lateinit var conferenceService: ConferenceService

    @Mock
    lateinit var responseBody: ResponseBody

    @Mock
    lateinit var responseListener: ResponseListener

    @Mock
    lateinit var callMockResponseBody: Call<ResponseBody>

    val t = Throwable()

    val booking = ManagerBooking()

//---------------------------Add Booking Details------------------------------------------------------------------------
    /**
     * Success For AddBooking Details
     */
    @Test
    fun testSuccessForAddbookingDetails() {
        RestClient.setMockService(conferenceService)
        `when`(conferenceService.addManagerBookingDetails(booking)).thenReturn(callMockResponseBody)
        managerBookingRepository.addBookingDetails(booking, responseListener)
        verify(callMockResponseBody).enqueue(argumentCaptorForResponseBody.capture())
        argumentCaptorForResponseBody.value.onResponse(
            callMockResponseBody,
            Response.success(Constants.OK_RESPONSE, responseBody)
        )
        verify(responseListener, times(1)).onSuccess(Constants.OK_RESPONSE) }

    /**
     * Success But Failure for AddBooking Details
     */
    @Test
    fun testSuccessButFailureForAddBookingDetails(){
        RestClient.setMockService(conferenceService)
        `when`(conferenceService.addManagerBookingDetails(booking)).thenReturn(callMockResponseBody)
        managerBookingRepository.addBookingDetails(booking, responseListener)
        verify(callMockResponseBody).enqueue(argumentCaptorForResponseBody.capture())
        argumentCaptorForResponseBody.value.onResponse(
            callMockResponseBody,
            Response.success(Constants.NO_CONTENT_FOUND, responseBody)
        )
        verify(responseListener, times(1)).onFailure(Constants.NO_CONTENT_FOUND)
    }

    /**
     * Failure For Add Booking Details
     */
    @Test
    fun testFailureForAddBooking(){
        RestClient.setMockService(conferenceService)
        `when`(conferenceService.addManagerBookingDetails(booking)).thenReturn(callMockResponseBody)
        managerBookingRepository.addBookingDetails(booking, responseListener)
        verify(callMockResponseBody).enqueue(argumentCaptorForResponseBody.capture())
        argumentCaptorForResponseBody.value.onFailure(
            callMockResponseBody,
            t
        )
        verify(responseListener, times(1)).onFailure(Constants.INTERNAL_SERVER_ERROR)
    }
}