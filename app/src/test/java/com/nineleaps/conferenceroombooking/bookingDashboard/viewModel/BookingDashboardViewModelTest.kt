package com.nineleaps.conferenceroombooking.bookingDashboard.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nineleaps.conferenceroombooking.bookingDashboard.repository.BookingDashboardRepository
import com.nineleaps.conferenceroombooking.model.BookingDashboardInput
import com.nineleaps.conferenceroombooking.model.DashboardDetails
import com.nineleaps.conferenceroombooking.services.ResponseListener
import com.nineleaps.conferenceroombooking.utils.Constants
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class BookingDashboardViewModelTest {

    @Rule
    @JvmField
    var executor = InstantTaskExecutorRule()

    val dashboard = DashboardDetails()

    val mBookingDashboardInput= BookingDashboardInput()

    val listenerCaptor = argumentCaptor<ResponseListener>()
    val repoMock = mock(BookingDashboardRepository::class.java)
    val viewModel = BookingDashboardViewModel()

    @Before
    fun setUp(){
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun testReturnSuccessForGetBookingList(){
        doReturn("{}").`when`(spy(viewModel.returnSuccess())).value
    }

    @Test
    fun testReturnFailureForGetBookingList(){
        doReturn(Constants.INTERNAL_SERVER_ERROR).`when`(spy(viewModel.returnFailure())).value
    }

    @Test
    fun testReturnBookingCancelledSuccess(){
        doReturn(Constants.OK_RESPONSE).`when`(spy(viewModel.returnBookingCancelled())).value
    }

    @Test
    fun testReturnCancelFailed(){
        doReturn(Constants.INTERNAL_SERVER_ERROR).`when`(spy(viewModel.returnCancelFailed())).value
    }

    @Test
    fun tesReturnPasscodeSuccess(){
        doReturn("").`when`(spy(viewModel.returnPasscode())).value
    }

    @Test
    fun testReturnPasscodeFailed(){
        doReturn(Constants.INTERNAL_SERVER_ERROR).`when`(spy(viewModel.returnPasscodeFailed())).value
    }

    @Test
    fun testGetBookingList(){
        viewModel.setBookedRoomDashboardRepo(repoMock)
        doNothing().`when`(repoMock).getBookingList(com.nhaarman.mockitokotlin2.any(),com.nhaarman.mockitokotlin2.any())
        viewModel.getBookingList(mBookingDashboardInput)
        verify(repoMock, times(1)).getBookingList(com.nhaarman.mockitokotlin2.any(),listenerCaptor.capture())
        listenerCaptor.firstValue.onSuccess(dashboard)
        listenerCaptor.firstValue.onFailure(Constants.INTERNAL_SERVER_ERROR)
    }

    @Test
    fun testGetPasscode(){
        viewModel.setBookedRoomDashboardRepo(repoMock)
        doNothing().`when`(repoMock).getPasscode(com.nhaarman.mockitokotlin2.any(),com.nhaarman.mockitokotlin2.any(),com.nhaarman.mockitokotlin2.any())
        viewModel.getPasscode(false,"")
        verify(repoMock, times(1)).getPasscode(com.nhaarman.mockitokotlin2.any(),com.nhaarman.mockitokotlin2.any(),listenerCaptor.capture())
        listenerCaptor.firstValue.onSuccess("")
        listenerCaptor.firstValue.onFailure(Constants.INTERNAL_SERVER_ERROR)
    }

    @Test
    fun testCancelBooking(){
        viewModel.setBookedRoomDashboardRepo(repoMock)
        doNothing().`when`(repoMock).cancelBooking(com.nhaarman.mockitokotlin2.any(),com.nhaarman.mockitokotlin2.any())
        viewModel.cancelBooking(0)
        verify(repoMock, times(1)).cancelBooking(com.nhaarman.mockitokotlin2.any(),listenerCaptor.capture())
        listenerCaptor.firstValue.onSuccess(Constants.OK_RESPONSE)
        listenerCaptor.firstValue.onFailure(Constants.INTERNAL_SERVER_ERROR)
    }

    @Test
    fun testRecurringCancelMeeting(){
        viewModel.setBookedRoomDashboardRepo(repoMock)
        doNothing().`when`(repoMock).recurringCancelBooking(com.nhaarman.mockitokotlin2.any(),com.nhaarman.mockitokotlin2.any(),com.nhaarman.mockitokotlin2.any())
        viewModel.recurringCancelBooking(0,"")
        verify(repoMock, times(1)).recurringCancelBooking(com.nhaarman.mockitokotlin2.any(),com.nhaarman.mockitokotlin2.any(),listenerCaptor.capture())
        listenerCaptor.firstValue.onSuccess(Constants.OK_RESPONSE)
        listenerCaptor.firstValue.onFailure(Constants.INTERNAL_SERVER_ERROR)
    }
}