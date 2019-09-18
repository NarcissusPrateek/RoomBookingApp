package com.nineleaps.conferenceroombooking.booking.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nineleaps.conferenceroombooking.booking.repository.BookingRepository
import com.nineleaps.conferenceroombooking.model.Booking
import com.nineleaps.conferenceroombooking.services.ResponseListener
import com.nineleaps.conferenceroombooking.utils.Constants
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mockito.mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class BookingViewModelTest {

    @Rule
    @JvmField
    var executor = InstantTaskExecutorRule()


    val listenerCaptor = argumentCaptor<ResponseListener>()
    val repoMock = mock(BookingRepository::class.java)
    val viewModel = BookingViewModel()
    val booking = Booking()

    @Before
    fun setUp(){
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun testReturnSuccessForBooking(){
        lenient().doReturn(Constants.OK_RESPONSE).`when`(spy(viewModel.returnSuccessForBooking())).value
    }

    @Test
    fun testReturnFailureForBooking(){
        lenient().doReturn(Constants.INTERNAL_SERVER_ERROR).`when`(spy(viewModel.returnFailureForBooking())).value
    }

    @Test
    fun testAddBookingDetails(){
        viewModel.setBookingRepo(repoMock)
        doNothing().`when`(repoMock).addBookingDetails(com.nhaarman.mockitokotlin2.any(),com.nhaarman.mockitokotlin2.any())
        viewModel.addBookingDetails(booking)
        verify(repoMock, times(1)).addBookingDetails(com.nhaarman.mockitokotlin2.any(),listenerCaptor.capture())
        listenerCaptor.firstValue.onSuccess(Constants.OK_RESPONSE)
        listenerCaptor.firstValue.onFailure(Constants.INTERNAL_SERVER_ERROR)
    }

}