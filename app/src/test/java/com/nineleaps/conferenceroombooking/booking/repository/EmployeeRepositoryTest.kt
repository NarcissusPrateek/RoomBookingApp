package com.nineleaps.conferenceroombooking.booking.repository

import com.nineleaps.conferenceroombooking.model.EmployeeList
import com.nineleaps.conferenceroombooking.services.ConferenceService
import com.nineleaps.conferenceroombooking.services.ResponseListener
import com.nineleaps.conferenceroombooking.services.RestClient
import com.nineleaps.conferenceroombooking.utils.Constants
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.InjectMocks
import org.mockito.Mockito.*
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@RunWith(MockitoJUnitRunner::class)
class EmployeeRepositoryTest {

    /**
     * Initializing og the Mock objects
     */
    @InjectMocks
    lateinit var mEmployeeRepository: EmployeeRepository

    @Captor
    lateinit var argumentCaptorForEmployList: ArgumentCaptor<Callback<List<EmployeeList>>>

    @Mock
    lateinit var callMock: Call<List<EmployeeList>>

    @Mock
    lateinit var responseBody: List<EmployeeList>

    @Mock
    lateinit var conferenceService: ConferenceService

    @Mock
    lateinit var listener: ResponseListener

    val t = Throwable()

//-------------------------------Get Employee List----------------------------------------------------------------------
    /**
     * GetEmployeeList success response
     */
    @Test
    fun testSuccessOKResponseForGetEmployeeList() {
        RestClient.setMockService(conferenceService)
        `when`(conferenceService.getEmployees("")).thenReturn(callMock)
        mEmployeeRepository.getEmployeeList("", listener)
        verify(callMock).enqueue(argumentCaptorForEmployList.capture())
        argumentCaptorForEmployList.value.onResponse(
            callMock,
            Response.success(Constants.OK_RESPONSE, responseBody)
        )
        verify(listener, times(1)).onSuccess(responseBody)
    }

    /**
     * GetEmployeeList success response but failure for 204
     */
    @Test
    fun testSuccessButFailureForGetEmployeeList() {
        RestClient.setMockService(conferenceService)
        `when`(conferenceService.getEmployees("")).thenReturn(callMock)
        mEmployeeRepository.getEmployeeList("", listener)
        verify(callMock).enqueue(argumentCaptorForEmployList.capture())
        argumentCaptorForEmployList.value.onResponse(
            callMock,
            Response.success(Constants.NO_CONTENT_FOUND, responseBody)
        )
        verify(listener, times(1)).onFailure(Constants.NO_CONTENT_FOUND)
    }

    /**
     * GetEmployeeList failure response for 500
     */
    @Test
    fun testFailureResponseForGetEmployeeList() {
        RestClient.setMockService(conferenceService)
        `when`(conferenceService.getEmployees("")).thenReturn(callMock)
        mEmployeeRepository.getEmployeeList("", listener)
        verify(callMock).enqueue(argumentCaptorForEmployList.capture())
        argumentCaptorForEmployList.value.onFailure(
            callMock,
            t
        )
        verify(listener, times(1)).onFailure(Constants.INTERNAL_SERVER_ERROR)
    }
}