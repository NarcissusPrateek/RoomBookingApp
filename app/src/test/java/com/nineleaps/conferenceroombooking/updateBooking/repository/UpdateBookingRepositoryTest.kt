package com.nineleaps.conferenceroombooking.updateBooking.repository

import com.nhaarman.mockitokotlin2.firstValue
import com.nineleaps.conferenceroombooking.model.UpdateBooking
import com.nineleaps.conferenceroombooking.services.ConferenceService
import com.nineleaps.conferenceroombooking.services.ResponseListener
import com.nineleaps.conferenceroombooking.services.RestClient
import com.nineleaps.conferenceroombooking.utils.Constants
import okhttp3.ResponseBody
import org.junit.Assert.*
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
class UpdateBookingRepositoryTest {
    /**
     * Initialization Of Mock Objects
     */
    @InjectMocks
    lateinit var mUpdateBookingRepository: UpdateBookingRepository

    @Mock
    lateinit var conferenceService: ConferenceService

    @Mock
    lateinit var callMock: Call<ResponseBody>

    @Captor
    lateinit var captor: ArgumentCaptor<Callback<ResponseBody>>

    @Mock
    lateinit var responseBody: ResponseBody

    val updateBooking = UpdateBooking()

    @Mock
    lateinit var responseListener: ResponseListener

    val t = Throwable()
//------------------------------------------------UpdateBookingDetails--------------------------------------------------
    /**
     * Success For Update Booking
     */
    @Test
    fun testSuccessForUpdateBookingDetails() {
        RestClient.setMockService(conferenceService)
        `when`(conferenceService.update(updateBooking)).thenReturn(callMock)
        mUpdateBookingRepository.updateBookingDetails(updateBooking, responseListener)
        verify(callMock).enqueue(captor.capture())
        captor.firstValue.onResponse(
            callMock,
            Response.success(Constants.OK_RESPONSE, responseBody)
        )
        verify(responseListener, times(1)).onSuccess(Constants.OK_RESPONSE)
    }

    /**
     * Success But Failure For Update Booking
     */
    @Test
    fun testSuccessButFailureForUpdateBooking() {
        RestClient.setMockService(conferenceService)
        `when`(conferenceService.update(updateBooking)).thenReturn(callMock)
        mUpdateBookingRepository.updateBookingDetails(updateBooking, responseListener)
        verify(callMock).enqueue(captor.capture())
        captor.firstValue.onResponse(
            callMock,
            Response.success(Constants.NO_CONTENT_FOUND, responseBody)
        )
        verify(responseListener, times(1)).onFailure(Constants.NO_CONTENT_FOUND)
    }

    /**
     * Failure For Update Booking
     */
    @Test
    fun testFailureForUpdateBooking(){
        RestClient.setMockService(conferenceService)
        `when`(conferenceService.update(updateBooking)).thenReturn(callMock)
        mUpdateBookingRepository.updateBookingDetails(updateBooking, responseListener)
        verify(callMock).enqueue(captor.capture())
        captor.firstValue.onFailure(
            callMock,
            t
        )
        verify(responseListener, times(1)).onFailure(Constants.INTERNAL_SERVER_ERROR)
    }
}