package com.nineleaps.conferenceroombooking.blockRoom.repository

import android.provider.SyncStateContract
import com.nineleaps.conferenceroombooking.model.BlockRoom
import com.nineleaps.conferenceroombooking.model.BlockingConfirmation
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
class BlockRoomRepositoryTest {
    /**
     * Initialization of Mock Objects
     */
    @InjectMocks
    lateinit var mBlockRoomRepository: BlockRoomRepository

    @Captor
    lateinit var argumentCaptor: ArgumentCaptor<Callback<ResponseBody>>

    @Captor
    lateinit var argumentCaptorForBlockingConfirmation: ArgumentCaptor<Callback<BlockingConfirmation>>

    @Mock
    lateinit var mConferenceService: ConferenceService

    @Mock
    lateinit var callMockForResponseBody: Call<ResponseBody>

    @Mock
    lateinit var callMockForBlockConfirmation: Call<BlockingConfirmation>

    @Mock
    lateinit var listener: ResponseListener

    @Mock
    lateinit var responseBody: ResponseBody

    @Mock
    lateinit var responseBodyForBlockingConfirmation: BlockingConfirmation

    val t: Throwable = Throwable()

    val mBlockingConfirmation = BlockingConfirmation()

    val mBlockRoom = BlockRoom()


//----------------------------------------Block Room--------------------------------------------------------------------

    /**
     * Block Room Test For 200 Ok response
     */
    @Test
    fun testSuccessOKResponseForBlockRoom() {
        RestClient.setMockService(mConferenceService)
        `when`(mConferenceService.blockconference(mBlockRoom)).thenReturn(callMockForResponseBody)
        mBlockRoomRepository.blockRoom(mBlockRoom, listener)
        verify(callMockForResponseBody).enqueue(argumentCaptor.capture())
        argumentCaptor.value.onResponse(
            callMockForResponseBody,
            Response.success(Constants.OK_RESPONSE, responseBody)
        )
        verify(listener, times(1)).onSuccess(Constants.OK_RESPONSE)
    }

    /**
     * Block Room Test For 201 SuccessFully Created
     */
    @Test
    fun testSuccessForSuccessfullyCreatedForBlockRoom() {
        RestClient.setMockService(mConferenceService)
        `when`(mConferenceService.blockconference(mBlockRoom)).thenReturn(callMockForResponseBody)
        mBlockRoomRepository.blockRoom(mBlockRoom, listener)
        verify(callMockForResponseBody).enqueue(argumentCaptor.capture())
        argumentCaptor.value.onResponse(
            callMockForResponseBody,
            Response.success(Constants.SUCCESSFULLY_CREATED, responseBody)
        )
        verify(listener, times(1)).onSuccess(Constants.SUCCESSFULLY_CREATED)
    }

    /**
     * Block Room Test For Success But Failure For Block Room
     */
    @Test
    fun testSuccessButFailureForBlockRoom() {
        RestClient.setMockService(mConferenceService)
        `when`(mConferenceService.blockconference(mBlockRoom)).thenReturn(callMockForResponseBody)
        mBlockRoomRepository.blockRoom(mBlockRoom, listener)
        verify(callMockForResponseBody).enqueue(argumentCaptor.capture())
        argumentCaptor.value.onResponse(
            callMockForResponseBody,
            Response.success(Constants.NO_CONTENT_FOUND, responseBody)
        )
        verify(listener, times(1)).onFailure(Constants.NO_CONTENT_FOUND)
    }

    /**
     * Block Room Test For Failure Response of 500 Server Not Found
     */
    @Test
    fun testFailureOfServerNotFoundForBlockRoom() {
        RestClient.setMockService(mConferenceService)
        `when`(mConferenceService.blockconference(mBlockRoom)).thenReturn(callMockForResponseBody)
        mBlockRoomRepository.blockRoom(mBlockRoom, listener)
        verify(callMockForResponseBody).enqueue(argumentCaptor.capture())
        argumentCaptor.value.onFailure(
            callMockForResponseBody,
            t
        )
        verify(listener, times(1)).onFailure(Constants.INTERNAL_SERVER_ERROR)
    }

//------------------------------------ Blocking Status------------------------------------------------------------------
    /**
     * Blocking Status Test For Success For Ok Response
     */
    @Test
    fun testSuccessOKResponseForBlockingStatus() {
        RestClient.setMockService(mConferenceService)
        `when`(mConferenceService.blockConfirmation(mBlockRoom)).thenReturn(callMockForBlockConfirmation)
        mBlockRoomRepository.blockingStatus(mBlockRoom, listener)
        verify(callMockForBlockConfirmation).enqueue(argumentCaptorForBlockingConfirmation.capture())
        argumentCaptorForBlockingConfirmation.value.onResponse(
            callMockForBlockConfirmation,
            Response.success(Constants.OK_RESPONSE, responseBodyForBlockingConfirmation)
        )
        verify(listener, times(1)).onSuccess(responseBodyForBlockingConfirmation)
    }
    /**
     * Blocking Status Test For Success For No Content Found
     */
    @Test
    fun testSuccessNoContentFoundForBlockingStatus(){
        RestClient.setMockService(mConferenceService)
        `when`(mConferenceService.blockConfirmation(mBlockRoom)).thenReturn(callMockForBlockConfirmation)
        mBlockRoomRepository.blockingStatus(mBlockRoom, listener)
        verify(callMockForBlockConfirmation).enqueue(argumentCaptorForBlockingConfirmation.capture())
        argumentCaptorForBlockingConfirmation.value.onResponse(
            callMockForBlockConfirmation,
            Response.success(Constants.NO_CONTENT_FOUND, mBlockingConfirmation)
        )
        verify(listener, times(1)).onSuccess(mBlockingConfirmation)
    }
    /**
     * Blocking Status Test For Success But Failure For Blocking Status
     */
    @Test
    fun testSuccessButFailureForBlockingStatus(){
        RestClient.setMockService(mConferenceService)
        `when`(mConferenceService.blockConfirmation(mBlockRoom)).thenReturn(callMockForBlockConfirmation)
        mBlockRoomRepository.blockingStatus(mBlockRoom, listener)
        verify(callMockForBlockConfirmation).enqueue(argumentCaptorForBlockingConfirmation.capture())
        argumentCaptorForBlockingConfirmation.value.onResponse(
            callMockForBlockConfirmation,
            Response.success(Constants.SUCCESSFULLY_CREATED, mBlockingConfirmation)
        )
        verify(listener, times(1)).onFailure(Constants.SUCCESSFULLY_CREATED)
    }

    /**
     * Blocking Status Test For Failure of Blocking Status
     */
    @Test
    fun testFailureOfServerNotFoundForBlockingStatus(){
        RestClient.setMockService(mConferenceService)
        `when`(mConferenceService.blockConfirmation(mBlockRoom)).thenReturn(callMockForBlockConfirmation)
        mBlockRoomRepository.blockingStatus(mBlockRoom, listener)
        verify(callMockForBlockConfirmation).enqueue(argumentCaptorForBlockingConfirmation.capture())
        argumentCaptorForBlockingConfirmation.value.onFailure(
            callMockForBlockConfirmation,
            t
        )
        verify(listener, times(1)).onFailure(Constants.INTERNAL_SERVER_ERROR)
    }

}