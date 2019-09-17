package com.nineleaps.conferenceroombooking.addBuilding.repository

import com.nhaarman.mockitokotlin2.verify
import com.nineleaps.conferenceroombooking.model.AddBuilding
import com.nineleaps.conferenceroombooking.model.Location
import com.nineleaps.conferenceroombooking.services.ConferenceService
import com.nineleaps.conferenceroombooking.services.ResponseListener
import com.nineleaps.conferenceroombooking.services.RestClient
import com.nineleaps.conferenceroombooking.utils.Constants
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.*
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@RunWith(MockitoJUnitRunner::class)
class AddBuildingRepositoryTest {
    /**
     * Initialization of Mock Object
     */
    @Captor
    lateinit var argumentCaptor: ArgumentCaptor<Callback<ResponseBody>>

    @Captor
    lateinit var argumentCaptorForListOfLocation: ArgumentCaptor<Callback<List<Location>>>

    @Mock
    lateinit var callMock: Call<ResponseBody>

    @Mock
    lateinit var callMockForGetLocation: Call<List<Location>>

    @Mock
    lateinit var responseBody: ResponseBody

    @Mock
    lateinit var respone : List<Location>
    @Mock
    lateinit var conferenceService: ConferenceService

    @InjectMocks
    lateinit var mAddBuildingRepository: AddBuildingRepository

    @Mock
    lateinit var listener: ResponseListener

    val addBuilding = AddBuilding()

    lateinit var t: Throwable


    /**
     *  AddingBuilding Success Response 200 OK
     */
    @Test
    fun testSuccessResponseForAddBuilding() {
        RestClient.setMockService(conferenceService)
        Mockito.`when`(conferenceService.addBuilding(addBuilding)).thenReturn(callMock)
        mAddBuildingRepository.addBuildingDetails(addBuilding, listener)
        verify(callMock).enqueue(argumentCaptor.capture())
        argumentCaptor.value.onResponse(
            callMock,
            Response.success(ResponseBody.create(MediaType.get("text/html"), "content"))
        )
        verify(listener, Mockito.times(1)).onSuccess(200)
    }

    /**
     *  AddingBuilding Failure Response 500
     */
    @Test
    fun testFailureResponseForAddBuilding() {
        t = Throwable()
        RestClient.setMockService(conferenceService)
        Mockito.`when`(conferenceService.addBuilding(addBuilding)).thenReturn(callMock)
        mAddBuildingRepository.addBuildingDetails(addBuilding, listener)
        verify(callMock).enqueue(argumentCaptor.capture())
        argumentCaptor.value.onFailure(callMock, t)
        verify(listener, Mockito.times(1)).onFailure(500)
    }

    /**
     *  AddingBuilding Success Response 201 OK
     */
    @Test
    fun testSuccessResponseForAddingBuilding() {
        RestClient.setMockService(conferenceService)
        Mockito.`when`(conferenceService.addBuilding(addBuilding)).thenReturn(callMock)
        mAddBuildingRepository.addBuildingDetails(addBuilding, listener)
        verify(callMock).enqueue(argumentCaptor.capture())
        argumentCaptor.value.onResponse(
            callMock,
            Response.success(201, responseBody)
        )
        verify(listener, Mockito.times(1)).onSuccess(Constants.SUCCESSFULLY_CREATED)
    }

    /**
     *  AddingBuilding Success Response 204 OK
     */
    @Test
    fun testSuccessResponseButFailureForAddingBuilding() {
        RestClient.setMockService(conferenceService)
        Mockito.`when`(conferenceService.addBuilding(addBuilding)).thenReturn(callMock)
        mAddBuildingRepository.addBuildingDetails(addBuilding, listener)
        verify(callMock).enqueue(argumentCaptor.capture())
        argumentCaptor.value.onResponse(
            callMock,
            Response.success(204, responseBody)
        )
        verify(listener, Mockito.times(1)).onFailure(204)
    }

    /**
     *  UpdateBuilding Success Response 201 OK
     */
    @Test
    fun testSuccessForUpdateBuilding(){
        RestClient.setMockService(conferenceService)
        Mockito.`when`(conferenceService.updateBuilding(addBuilding)).thenReturn(callMock)
        mAddBuildingRepository.updateBuildingDetails(addBuilding, listener)
        verify(callMock).enqueue(argumentCaptor.capture())
        argumentCaptor.value.onResponse(
            callMock,
            Response.success(201, responseBody)
        )
        verify(listener, Mockito.times(1)).onSuccess(Constants.SUCCESSFULLY_CREATED)
    }

    /**
     *  UpdateBuilding Failure Response 500 OK
     */
    @Test
    fun testFailureForUdapteBuilding(){
        t = Throwable()
        RestClient.setMockService(conferenceService)
        Mockito.`when`(conferenceService.updateBuilding(addBuilding)).thenReturn(callMock)
        mAddBuildingRepository.updateBuildingDetails(addBuilding, listener)
        verify(callMock).enqueue(argumentCaptor.capture())
        argumentCaptor.value.onFailure(callMock, t)
        verify(listener, Mockito.times(1)).onFailure(500)
    }

    /**
     *  UpdateBuilding Success Response 202 OK
     */
    @Test
    fun testSuccessButFailureForUpdateBuilding(){
        RestClient.setMockService(conferenceService)
        Mockito.`when`(conferenceService.updateBuilding(addBuilding)).thenReturn(callMock)
        mAddBuildingRepository.updateBuildingDetails(addBuilding,listener)
        verify(callMock).enqueue(argumentCaptor.capture())
        argumentCaptor.value.onResponse(callMock,
            Response.success(202,responseBody))
        verify(listener,Mockito.times(1)).onFailure(202)
    }

    /**
     *  UpdateBuilding Success Response 200 OK
     */
    @Test
    fun testSuccessOkForUpdateBuilding(){
        RestClient.setMockService(conferenceService)
        Mockito.`when`(conferenceService.updateBuilding(addBuilding)).thenReturn(callMock)
        mAddBuildingRepository.updateBuildingDetails(addBuilding,listener)
        verify(callMock).enqueue(argumentCaptor.capture())
        argumentCaptor.value.onResponse(callMock,
            Response.success(200,responseBody))
        verify(listener,Mockito.times(1)).onSuccess(200)
    }

    /**
     *  GetLocation Success Response 200 OK
     */
    @Test
    fun testSuccessForGetLocation(){
        RestClient.setMockService(conferenceService)
        Mockito.`when`(conferenceService.getAllLocation()).thenReturn(callMockForGetLocation)
        mAddBuildingRepository.getLocationDetails(listener)
        verify(callMockForGetLocation).enqueue(argumentCaptorForListOfLocation.capture())
        argumentCaptorForListOfLocation.value.onResponse(callMockForGetLocation, Response.success(200, respone))
        verify(listener,Mockito.times(1)).onSuccess(respone)
    }

    /**
     *  GetLocation Failure Response 500
     */
    @Test
    fun testFailureForGetLocation(){
        t = Throwable()
        RestClient.setMockService(conferenceService)
        Mockito.`when`(conferenceService.getAllLocation()).thenReturn(callMockForGetLocation)
        mAddBuildingRepository.getLocationDetails(listener)
        verify(callMockForGetLocation).enqueue(argumentCaptorForListOfLocation.capture())
        argumentCaptorForListOfLocation.value.onFailure(callMockForGetLocation,t)
        verify(listener,Mockito.times(1)).onFailure(500)
    }

    /**
     *  GetLocation Success Response 202 but failure
     */
    @Test
    fun testSuccessButFailureForGetLocation(){
        RestClient.setMockService(conferenceService)
        Mockito.`when`(conferenceService.getAllLocation()).thenReturn(callMockForGetLocation)
        mAddBuildingRepository.getLocationDetails(listener)
        verify(callMockForGetLocation).enqueue(argumentCaptorForListOfLocation.capture())
        argumentCaptorForListOfLocation.value.onResponse(callMockForGetLocation,
            Response.success(202,respone))
        verify(listener,Mockito.times(1)).onFailure(202)
    }


}