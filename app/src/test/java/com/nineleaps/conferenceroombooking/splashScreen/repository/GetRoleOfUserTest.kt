package com.nineleaps.conferenceroombooking.splashScreen.repository

import com.nineleaps.conferenceroombooking.services.ConferenceService
import com.nineleaps.conferenceroombooking.services.ResponseListener
import com.nineleaps.conferenceroombooking.services.RestClient
import com.nineleaps.conferenceroombooking.utils.Constants
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
class GetRoleOfUserTest {

    /**
     * Initialization of Mock Objects
     */
    @InjectMocks
    lateinit var mGetRoleOfUser: GetRoleOfUser

    @Mock
    lateinit var conferenceService: ConferenceService

    @Mock
    lateinit var callMock: Call<Int>

    @Captor
    lateinit var callBack: ArgumentCaptor<Callback<Int>>

    @Mock
    lateinit var responseListener: ResponseListener

    var response: Int = 0

    val t = Throwable()
//------------------------------------Get Role--------------------------------------------------------------------------
    /**
     * Success For get role
     */
    @Test
    fun testSuccessForGetRole() {
        RestClient.setMockService(conferenceService)
        `when`(conferenceService.getRole("")).thenReturn(callMock)
        mGetRoleOfUser.getRole("",responseListener)
        verify(callMock).enqueue(callBack.capture())
        callBack.value.onResponse(
            callMock,
            Response.success(Constants.OK_RESPONSE,response)
        )
        verify(responseListener, times(1)).onSuccess(response)
    }

    /**
     * Success But Failure For get Role
     */
    @Test
    fun testSuccessButFailureForGetRole(){
        RestClient.setMockService(conferenceService)
        `when`(conferenceService.getRole("")).thenReturn(callMock)
        mGetRoleOfUser.getRole("",responseListener)
        verify(callMock).enqueue(callBack.capture())
        callBack.value.onResponse(
            callMock,
            Response.success(Constants.NO_CONTENT_FOUND,response)
        )
        verify(responseListener, times(1)).onFailure(Constants.NO_CONTENT_FOUND)
    }

    /**
     * Failure For Get Role
     */
    @Test
    fun testFailureForGetRole(){
        RestClient.setMockService(conferenceService)
        `when`(conferenceService.getRole("")).thenReturn(callMock)
        mGetRoleOfUser.getRole("",responseListener)
        verify(callMock).enqueue(callBack.capture())
        callBack.value.onFailure(
            callMock,
            t
        )
        verify(responseListener, times(1)).onFailure(Constants.INTERNAL_SERVER_ERROR)
    }
}