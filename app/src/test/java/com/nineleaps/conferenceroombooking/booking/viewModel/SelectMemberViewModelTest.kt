package com.nineleaps.conferenceroombooking.booking.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nineleaps.conferenceroombooking.booking.repository.EmployeeRepository
import com.nineleaps.conferenceroombooking.model.EmployeeList
import com.nineleaps.conferenceroombooking.services.ResponseListener
import com.nineleaps.conferenceroombooking.utils.Constants
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class SelectMemberViewModelTest{

    @Rule @JvmField
    var executor = InstantTaskExecutorRule()

    val listenerCaptor = argumentCaptor<ResponseListener>()
    val repoMock = mock(EmployeeRepository::class.java)
    val viewModel = SelectMemberViewModel()
    val list = emptyList<EmployeeList>()

    @Test
    fun testReturnSuccessForEmployeeList(){
        lenient().doReturn("{}").`when`(spy(viewModel.returnSuccessForEmployeeList())).value
    }

    @Test
    fun testReturnFailureForEmployeeList(){
        lenient().doReturn(Constants.INTERNAL_SERVER_ERROR).`when`(spy(viewModel.returnFailureForEmployeeList())).value
    }

    @Test
    fun testGetEmployeeList(){
        viewModel.setEmployeeListRepo(repoMock)
        doNothing().`when`(repoMock).getEmployeeList(com.nhaarman.mockitokotlin2.any(),com.nhaarman.mockitokotlin2.any())
        viewModel.getEmployeeList("")
        verify(repoMock, times(1)).getEmployeeList(com.nhaarman.mockitokotlin2.any(),listenerCaptor.capture())
        listenerCaptor.firstValue.onSuccess(list)
        listenerCaptor.firstValue.onFailure(Constants.INTERNAL_SERVER_ERROR)
    }
}