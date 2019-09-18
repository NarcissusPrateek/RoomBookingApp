package com.nineleaps.conferenceroombooking.booking.repository

import com.nineleaps.conferenceroombooking.model.Booking
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
class BookingRepositoryTest{

    /**
     * Initializing og the Mock objects
     */
    @InjectMocks
    lateinit var mBookingRepository: BookingRepository
    @Captor
    lateinit var argumentCaptor: ArgumentCaptor<Callback<ResponseBody>>

    @Mock
    lateinit var callMock: Call<ResponseBody>

    @Mock
    lateinit var responseBody: ResponseBody

    @Mock
    lateinit var conferenceService: ConferenceService

    @Mock
    lateinit var listener: ResponseListener

    val t = Throwable()

    val mBooking = Booking()

//----------------------------------------------Add Booking Details-----------------------------------------------------

    /**
     * AddBookingDetails success response for 200 ok
     */
    @Test
    fun testSuccessOKResponseForAddBookingDetails(){
        RestClient.setMockService(conferenceService)
        `when`(conferenceService.addBookingDetails(mBooking)).thenReturn(callMock)
        mBookingRepository.addBookingDetails(mBooking,listener)
        verify(callMock).enqueue(argumentCaptor.capture())
        argumentCaptor.value.onResponse(
            callMock,
            Response.success(Constants.OK_RESPONSE,responseBody)
        )
        verify(listener, times(1)).onSuccess(Constants.OK_RESPONSE)
    }

    /**
     * AddBookingDetails success response but failure for 204
     */
    @Test
    fun testSuccessButFailureResponseForAddBookingDetails(){
        RestClient.setMockService(conferenceService)
        `when`(conferenceService.addBookingDetails(mBooking)).thenReturn(callMock)
        mBookingRepository.addBookingDetails(mBooking,listener)
        verify(callMock).enqueue(argumentCaptor.capture())
        argumentCaptor.value.onResponse(
            callMock,
            Response.success(Constants.NO_CONTENT_FOUND,responseBody)
        )
        verify(listener, times(1)).onFailure(Constants.NO_CONTENT_FOUND)
    }

    /**
     * AddBookingDetails failure response for 500
     */
    @Test
    fun testFailureResponseOfServerNotFoundOfAddBookingDetails(){
        RestClient.setMockService(conferenceService)
        `when`(conferenceService.addBookingDetails(mBooking)).thenReturn(callMock)
        mBookingRepository.addBookingDetails(mBooking,listener)
        verify(callMock).enqueue(argumentCaptor.capture())
        argumentCaptor.value.onFailure(
            callMock,
            t
        )
        verify(listener, times(1)).onFailure(Constants.INTERNAL_SERVER_ERROR)
    }
}