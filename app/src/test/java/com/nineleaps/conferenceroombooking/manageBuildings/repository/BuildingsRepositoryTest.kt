package com.nineleaps.conferenceroombooking.manageBuildings.repository

import com.nineleaps.conferenceroombooking.model.Building
import com.nineleaps.conferenceroombooking.services.ConferenceService
import com.nineleaps.conferenceroombooking.services.ResponseListener
import com.nineleaps.conferenceroombooking.services.RestClient
import com.nineleaps.conferenceroombooking.utils.Constants
import okhttp3.ResponseBody
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
class BuildingsRepositoryTest {

    /**
     * Initialization of Mock Objects
     */

    @InjectMocks
    lateinit var mBuildingsRepository: BuildingsRepository

    @Captor
    lateinit var argumentCaptorForBuilding: ArgumentCaptor<Callback<List<Building>>>

    @Captor
    lateinit var argumentCaptorForResponseBody: ArgumentCaptor<Callback<ResponseBody>>

    @Mock
    lateinit var callMockForBuilding: Call<List<Building>>

    @Mock
    lateinit var callMockForResponseBody: Call<ResponseBody>

    @Mock
    lateinit var responseBody: ResponseBody

    @Mock
    lateinit var conferenceService: ConferenceService

    @Mock
    lateinit var responseListener: ResponseListener

    val t = Throwable()

    val buildingList = ArrayList<Building>()

    val emptyBuilding = emptyList<Building>()

//-------------------------------------------------Get Building List----------------------------------------------------

    /**
     * Success For Get Building List
     */
    @Test
    fun testSuccessOkResponseForGetBuildingList(){
        val building = Building()
        buildingList.add(building)
        RestClient.setMockService(conferenceService)
        `when`(conferenceService.getBuildingList()).thenReturn(callMockForBuilding)
        mBuildingsRepository.getBuildingList(responseListener)
        verify(callMockForBuilding).enqueue(argumentCaptorForBuilding.capture())
        argumentCaptorForBuilding.value.onResponse(
            callMockForBuilding,
            Response.success(Constants.OK_RESPONSE,buildingList)
        )
        verify(responseListener, times(1)).onSuccess(buildingList)
    }

    /**
     * Success Empty List For GetBuilding
     */
    @Test
    fun testSuccessEmptyListForGetBuildingList(){
        RestClient.setMockService(conferenceService)
        `when`(conferenceService.getBuildingList()).thenReturn(callMockForBuilding)
        mBuildingsRepository.getBuildingList(responseListener)
        verify(callMockForBuilding).enqueue(argumentCaptorForBuilding.capture())
        argumentCaptorForBuilding.value.onResponse(
            callMockForBuilding,
            Response.success(Constants.OK_RESPONSE,emptyBuilding)
        )
        verify(responseListener, times(1)).onSuccess(emptyBuilding)
    }

    /**
     * Success But Failure For Get Building List
     */
    @Test
    fun testSuccessButFailureForGetBuildingList(){
        RestClient.setMockService(conferenceService)
        `when`(conferenceService.getBuildingList()).thenReturn(callMockForBuilding)
        mBuildingsRepository.getBuildingList(responseListener)
        verify(callMockForBuilding).enqueue(argumentCaptorForBuilding.capture())
        argumentCaptorForBuilding.value.onResponse(
            callMockForBuilding,
            Response.success(Constants.SUCCESSFULLY_CREATED,emptyBuilding)
        )
        verify(responseListener, times(1)).onFailure(201)
    }

    /**
     * Failure For Get Building List
     */
    @Test
    fun testFailureForGetBuildingList(){
        RestClient.setMockService(conferenceService)
        `when`(conferenceService.getBuildingList()).thenReturn(callMockForBuilding)
        mBuildingsRepository.getBuildingList(responseListener)
        verify(callMockForBuilding).enqueue(argumentCaptorForBuilding.capture())
        argumentCaptorForBuilding.value.onFailure(
            callMockForBuilding,
            t
        )
        verify(responseListener, times(1)).onFailure(Constants.INTERNAL_SERVER_ERROR)
    }

//------------------------------------------Delete Building-------------------------------------------------------------
    /**
     * Success For Delete Building
     */
    @Test
    fun testSuccessForDeleteBuilding(){
        RestClient.setMockService(conferenceService)
        `when`(conferenceService.deleteBuilding(0)).thenReturn(callMockForResponseBody)
        mBuildingsRepository.deleteBuilding(0,responseListener)
        verify(callMockForResponseBody).enqueue(argumentCaptorForResponseBody.capture())
        argumentCaptorForResponseBody.value.onResponse(
            callMockForResponseBody,
            Response.success(Constants.OK_RESPONSE,responseBody)
        )
        verify(responseListener, times(1)).onSuccess(Constants.OK_RESPONSE)
    }

    /**
     * Success But Failure For Delete Building
     */
    @Test
    fun testSuccessButFailureForDeleteBuilding(){
        RestClient.setMockService(conferenceService)
        `when`(conferenceService.deleteBuilding(0)).thenReturn(callMockForResponseBody)
        mBuildingsRepository.deleteBuilding(0,responseListener)
        verify(callMockForResponseBody).enqueue(argumentCaptorForResponseBody.capture())
        argumentCaptorForResponseBody.value.onResponse(
            callMockForResponseBody,
            Response.success(Constants.SUCCESSFULLY_CREATED,responseBody)
        )
        verify(responseListener, times(1)).onFailure(Constants.SUCCESSFULLY_CREATED)
    }

    /**
     * Failure For Delete Building
     */
    @Test
    fun testFailureForDeleteBuilding(){
        RestClient.setMockService(conferenceService)
        `when`(conferenceService.deleteBuilding(0)).thenReturn(callMockForResponseBody)
        mBuildingsRepository.deleteBuilding(0,responseListener)
        verify(callMockForResponseBody).enqueue(argumentCaptorForResponseBody.capture())
        argumentCaptorForResponseBody.value.onFailure(
            callMockForResponseBody,
           t
        )
        verify(responseListener, times(1)).onFailure(Constants.INTERNAL_SERVER_ERROR)
    }
}