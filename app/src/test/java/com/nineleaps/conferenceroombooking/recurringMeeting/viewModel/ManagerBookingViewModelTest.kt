package com.nineleaps.conferenceroombooking.recurringMeeting.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nineleaps.conferenceroombooking.ViewModel.ManagerBookingViewModel
import com.nineleaps.conferenceroombooking.model.ManagerBooking
import com.nineleaps.conferenceroombooking.recurringMeeting.repository.ManagerBookingRepository
import com.nineleaps.conferenceroombooking.services.ResponseListener
import com.nineleaps.conferenceroombooking.utils.Constants
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.mockito.Mockito.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class ManagerBookingViewModelTest{
    @Rule
    @JvmField
    val executor = InstantTaskExecutorRule()

    val repo = mock(ManagerBookingRepository::class.java)
    val viewModel = ManagerBookingViewModel()
    val listernerCaptor = argumentCaptor<ResponseListener>()
    val booking  = ManagerBooking()

    @Before
    fun setUp(){
        MockitoAnnotations.initMocks(this)
    }
    @Test
    fun testReturnSuccessForManagerBooking(){
        lenient().doReturn(Constants.OK_RESPONSE).`when`(spy(viewModel.returnSuccessForBooking())).value
    }

    @Test
    fun testResturnFailureForBooking(){
        lenient().doReturn(Constants.INTERNAL_SERVER_ERROR).`when`(spy(viewModel.returnFailureForBooking())).value
    }

    @Test
    fun testForAddBookingDetails(){
        viewModel.setManagerBookingRepo(repo)
        doNothing().`when`(repo).addBookingDetails(com.nhaarman.mockitokotlin2.any(),com.nhaarman.mockitokotlin2.any())
        viewModel.addBookingDetails(booking)
        verify(repo, times(1)).addBookingDetails(com.nhaarman.mockitokotlin2.any(),listernerCaptor.capture())
        listernerCaptor.firstValue.onSuccess(Constants.OK_RESPONSE)
        listernerCaptor.firstValue.onFailure(Constants.INTERNAL_SERVER_ERROR)
    }
}