package com.nineleaps.conferenceroombooking.addConferenceRoom.repository

import com.nineleaps.conferenceroombooking.AddConferenceRoom
import com.nineleaps.conferenceroombooking.GetAllAmenities
import com.nineleaps.conferenceroombooking.services.ConferenceService
import com.nineleaps.conferenceroombooking.services.ResponseListener
import com.nineleaps.conferenceroombooking.services.RestClient
import com.nineleaps.conferenceroombooking.utils.Constants
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Call
import retrofit2.Callback
import org.mockito.Mockito.*
import retrofit2.Response

@RunWith(MockitoJUnitRunner::class)
class AddConferenceRepositoryTest {
    /**
     * Initialization of Mock Objects
     */

    @Captor
    lateinit var argumentCaptor: ArgumentCaptor<Callback<ResponseBody>>

    @Captor
    lateinit var argumentCaptorForGetAmenities: ArgumentCaptor<Callback<List<GetAllAmenities>>>

    @InjectMocks
    lateinit var mAddConferenceRepository: AddConferenceRepository

    @Mock
    lateinit var callMock: Call<ResponseBody>

    @Mock
    lateinit var callMockForAmenities: Call<List<GetAllAmenities>>

    @Mock
    lateinit var responseBody: ResponseBody

    @Mock
    lateinit var responseBodyForGetAllAmenities: List<GetAllAmenities>

    @Mock
    lateinit var conferenceService: ConferenceService

    @Mock
    lateinit var listener: ResponseListener

    lateinit var t: Throwable

    val addConference = AddConferenceRoom()

//--------------------------------Adding Conference---------------------------------------------------------------------
    /**
     * AddingConference Success Response 200 Ok
     */
    @Test
    fun testSuccessResponseForAddConference() {
        RestClient.setMockService(conferenceService)
        `when`(conferenceService.addConference(addConference)).thenReturn(callMock)
        mAddConferenceRepository.addConferenceDetails(addConference, listener)
        verify(callMock).enqueue(argumentCaptor.capture())
        argumentCaptor.value.onResponse(
            callMock, Response.success(ResponseBody.create(MediaType.get("text/html"), "content"))
        )
        verify(listener, times(1)).onSuccess(200)
    }

    /**
     * AddingConference Success Response 201 Created
     */
    @Test
    fun testSuccessCreatedResponseForAddConference() {
        RestClient.setMockService(conferenceService)
        `when`(conferenceService.addConference(addConference)).thenReturn(callMock)
        mAddConferenceRepository.addConferenceDetails(addConference, listener)
        verify(callMock).enqueue(argumentCaptor.capture())
        argumentCaptor.value.onResponse(
            callMock, Response.success(201, responseBody)
        )
        verify(listener, times(1)).onSuccess(201)
    }

    /**
     * AddingConference Failure Response 500 Server Not Found
     */
    @Test
    fun testFailureResponseForAddConference() {
        t = Throwable()
        RestClient.setMockService(conferenceService)
        `when`(conferenceService.addConference(addConference)).thenReturn(callMock)
        mAddConferenceRepository.addConferenceDetails(addConference, listener)
        verify(callMock).enqueue(argumentCaptor.capture())
        argumentCaptor.value.onFailure(
            callMock, t
        )
        verify(listener, times(1)).onFailure(500)
    }

    /**
     * AddingConference Success Response but Failure 202
     */
    @Test
    fun testSuccessButFailureResponseForAddConference() {
        RestClient.setMockService(conferenceService)
        `when`(conferenceService.addConference(addConference)).thenReturn(callMock)
        mAddConferenceRepository.addConferenceDetails(addConference, listener)
        verify(callMock).enqueue(argumentCaptor.capture())
        argumentCaptor.value.onResponse(
            callMock, Response.success(202, responseBody)
        )
        verify(listener, times(1)).onFailure(202)
    }

    //----------------------------------------------Update Conference ------------------------------------------------------

    /**
     * Update Conference Success Response 200 Ok
     */
    @Test
    fun testSuccessResponseForUpdateConferenceRoom() {
        RestClient.setMockService(conferenceService)
        `when`(conferenceService.updateConference(addConference)).thenReturn(callMock)
        mAddConferenceRepository.updateConferenceDetails(addConference, listener)
        verify(callMock).enqueue(argumentCaptor.capture())
        argumentCaptor.value.onResponse(
            callMock, Response.success(ResponseBody.create(MediaType.get("text/html"), "content"))
        )
        verify(listener, times(1)).onSuccess(200)
    }
    /**
     * Update Conference Success Response 201 Created
     */
    @Test
    fun testSuccessCreatedResponseForUpdateConference(){
        RestClient.setMockService(conferenceService)
        `when`(conferenceService.updateConference(addConference)).thenReturn(callMock)
        mAddConferenceRepository.updateConferenceDetails(addConference, listener)
        verify(callMock).enqueue(argumentCaptor.capture())
        argumentCaptor.value.onResponse(
            callMock, Response.success(201, responseBody)
        )
        verify(listener, times(1)).onSuccess(201)
    }

    /**
     * Update Conference Success Response but Failure 202
     */
    @Test
    fun testSuccessButFailureResponseForUpdateConference() {
        RestClient.setMockService(conferenceService)
        `when`(conferenceService.updateConference(addConference)).thenReturn(callMock)
        mAddConferenceRepository.updateConferenceDetails(addConference, listener)
        verify(callMock).enqueue(argumentCaptor.capture())
        argumentCaptor.value.onResponse(
            callMock, Response.success(202, responseBody)
        )
        verify(listener, times(1)).onFailure(202)
    }

    /**
     * AddingConference Failure Response 500 Server Not Found
     */
    @Test
    fun testFailureResponseForUpdateConference() {
        t = Throwable()
        RestClient.setMockService(conferenceService)
        `when`(conferenceService.updateConference(addConference)).thenReturn(callMock)
        mAddConferenceRepository.updateConferenceDetails(addConference, listener)
        verify(callMock).enqueue(argumentCaptor.capture())
        argumentCaptor.value.onFailure(
            callMock, t
        )
        verify(listener, times(1)).onFailure(500)
    }

//------------------------------------Get Amenities---------------------------------------------------------------------
    /**
     * Get Amenities Success Response 200 Ok
     */
    @Test
    fun testSuccessResponseForGetAmenities() {
        RestClient.setMockService(conferenceService)
        `when`(conferenceService.getAllAmenities()).thenReturn(callMockForAmenities)
        mAddConferenceRepository.getAmenitiesDetails(listener)
        verify(callMockForAmenities).enqueue(argumentCaptorForGetAmenities.capture())
        argumentCaptorForGetAmenities.value.onResponse(
            callMockForAmenities, Response.success(Constants.OK_RESPONSE,responseBodyForGetAllAmenities)
        )
        verify(listener, times(1)).onSuccess(responseBodyForGetAllAmenities)
    }

    /**
     * Get Amenities Success Response 204 No content Found
     */
    @Test
    fun testSuccessNoContentFoundResponseForGetAmenities(){
        RestClient.setMockService(conferenceService)
        `when`(conferenceService.getAllAmenities()).thenReturn(callMockForAmenities)
        mAddConferenceRepository.getAmenitiesDetails(listener)
        verify(callMockForAmenities).enqueue(argumentCaptorForGetAmenities.capture())
        argumentCaptorForGetAmenities.value.onResponse(
            callMockForAmenities, Response.success(Constants.NO_CONTENT_FOUND,responseBodyForGetAllAmenities)
        )
        verify(listener, times(1)).onSuccess(responseBodyForGetAllAmenities)
    }

    /**
     * Get Amenities Failure Response 500 Server Not Found
     */
    @Test
    fun testFailureServerNotFoundResponseForGetAmenities(){
        t = Throwable()
        RestClient.setMockService(conferenceService)
        `when`(conferenceService.getAllAmenities()).thenReturn(callMockForAmenities)
        mAddConferenceRepository.getAmenitiesDetails(listener)
        verify(callMockForAmenities).enqueue(argumentCaptorForGetAmenities.capture())
        argumentCaptorForGetAmenities.value.onFailure(
            callMockForAmenities, t
        )
        verify(listener, times(1)).onFailure(500)
    }

    /**
     * Get Amenities Success Response but Failure 202
     */
    @Test
    fun testSuccessButFailureResponseForGetAmenities() {
        RestClient.setMockService(conferenceService)
        `when`(conferenceService.getAllAmenities()).thenReturn(callMockForAmenities)
        mAddConferenceRepository.getAmenitiesDetails( listener)
        verify(callMockForAmenities).enqueue(argumentCaptorForGetAmenities.capture())
        argumentCaptorForGetAmenities.value.onResponse(
            callMockForAmenities, Response.success(202, responseBodyForGetAllAmenities)
        )
        verify(listener, times(1)).onFailure(202)
    }
}