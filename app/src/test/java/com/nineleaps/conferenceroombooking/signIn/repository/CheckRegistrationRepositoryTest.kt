package com.nineleaps.conferenceroombooking.signIn.repository

import com.nineleaps.conferenceroombooking.model.SignIn
import com.nineleaps.conferenceroombooking.services.ConferenceService
import com.nineleaps.conferenceroombooking.services.ResponseListener
import com.nineleaps.conferenceroombooking.services.RestClient
import com.nineleaps.conferenceroombooking.utils.Constants
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
class CheckRegistrationRepositoryTest{
    /**
     * Initailization of Mock Objects
     */
    @InjectMocks
    lateinit var checkRegistrationRepository: CheckRegistrationRepository

    @Captor
    lateinit var callBackMock: ArgumentCaptor<Callback<SignIn>>

    @Mock
    lateinit var callMock: Call<SignIn>

    @Mock
    lateinit var service: ConferenceService

    @Mock
    lateinit var listener: ResponseListener
    val t = Throwable()
    val signIn = SignIn()

//------------------------------------Check Registration----------------------------------------------------------------
    /**
     * Success For checkregistration
     */
    @Test
    fun testSuccessForCheckRegistration(){
        RestClient.setMockService(service)
        `when`(service.getRequestCode("")).thenReturn(callMock)
        checkRegistrationRepository.checkRegistration("",listener)
        verify(callMock).enqueue(callBackMock.capture())
        callBackMock.value.onResponse(
            callMock,
            Response.success(Constants.OK_RESPONSE,signIn)
        )
        verify(listener, times(1)).onSuccess(signIn)
    }

    /**
     * Success But Failure For check Registration
     */
    @Test
    fun testSuccessButFailureForCheckRegistration(){
        RestClient.setMockService(service)
        `when`(service.getRequestCode("")).thenReturn(callMock)
        checkRegistrationRepository.checkRegistration("",listener)
        verify(callMock).enqueue(callBackMock.capture())
        callBackMock.value.onResponse(
            callMock,
            Response.success(Constants.NO_CONTENT_FOUND,signIn)
        )
        verify(listener, times(1)).onFailure(Constants.NO_CONTENT_FOUND)
    }

    /**
     * Failure For CheckRegistration
     */
    @Test
    fun testFailureForCheckRegistration(){
        RestClient.setMockService(service)
        `when`(service.getRequestCode("")).thenReturn(callMock)
        checkRegistrationRepository.checkRegistration("",listener)
        verify(callMock).enqueue(callBackMock.capture())
        callBackMock.value.onFailure(
            callMock,
            t
        )
        verify(listener, times(1)).onFailure(Constants.INTERNAL_SERVER_ERROR)
    }
}