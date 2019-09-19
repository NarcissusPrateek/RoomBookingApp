package com.nineleaps.conferenceroombooking.updateBooking.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doNothing
import com.nhaarman.mockitokotlin2.verify
import com.nineleaps.conferenceroombooking.model.UpdateBooking
import com.nineleaps.conferenceroombooking.services.ResponseListener
import com.nineleaps.conferenceroombooking.updateBooking.repository.UpdateBookingRepository
import com.nineleaps.conferenceroombooking.utils.Constants
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class UpdateBookingViewModelTest{

    @Rule
    @JvmField
    val executor = InstantTaskExecutorRule()

    val repo = mock(UpdateBookingRepository::class.java)
    val captor = argumentCaptor<ResponseListener>()
    val viewModel = UpdateBookingViewModel()
    val updateBookingDetails = UpdateBooking()

    @Test
    fun testUpdateBookingDetails(){
        viewModel.setUpdateBookingRepo(repo)
        doNothing().`when`(repo).updateBookingDetails(any(), any())
        viewModel.updateBookingDetails(updateBookingDetails)
        verify(repo).updateBookingDetails(any(),captor.capture())
        captor.firstValue.onSuccess(Constants.OK_RESPONSE)
        captor.firstValue.onFailure(Constants.INTERNAL_SERVER_ERROR)
    }

    @Test
    fun testReturnBookingUpdate(){
        lenient().doReturn(Constants.OK_RESPONSE).`when`(spy(viewModel.returnBookingUpdated())).value
    }

    @Test
    fun testReturnUpdateFailed(){
        lenient().doReturn(Constants.INTERNAL_SERVER_ERROR).`when`(spy(viewModel.returnUpdateFailed())).value
    }

}