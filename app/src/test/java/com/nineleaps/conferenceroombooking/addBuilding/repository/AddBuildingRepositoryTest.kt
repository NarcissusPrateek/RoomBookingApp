package com.nineleaps.conferenceroombooking.addBuilding.repository

import com.google.gson.GsonBuilder
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doNothing
import com.nhaarman.mockitokotlin2.mock
import com.nineleaps.conferenceroombooking.model.AddBuilding
import com.nineleaps.conferenceroombooking.services.ConferenceService
import com.nineleaps.conferenceroombooking.services.ResponseListener
import com.nineleaps.conferenceroombooking.services.RestClient
import okhttp3.ResponseBody
import okhttp3.mockwebserver.MockWebServer
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.*
import org.mockito.Mockito.*
import org.mockito.invocation.InvocationOnMock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.stubbing.Answer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@RunWith(MockitoJUnitRunner::class)
class AddBuildingRepositoryTest {




    @Mock
    val mRestClient = RestClient.getWebServiceData()

    @InjectMocks
    lateinit var mAddBuildingRepository: AddBuildingRepository

    private lateinit var mockWebServer: MockWebServer
    private lateinit var mConferenceService: ConferenceService

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        val gson = GsonBuilder().setLenient().create()

        mConferenceService = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/ddd/"))
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ConferenceService::class.java)

    }

    @Mock
    var listener: ResponseListener = object : ResponseListener {
        override fun onSuccess(success: Any) {

        }

        override fun onFailure(failure: Any) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }

    @Test
    fun addBuildingDetails() {
        val addBuilding = AddBuilding()
        doNothing().`when`(spy(mAddBuildingRepository)).addBuildingDetails(addBuilding, listener)
        mAddBuildingRepository.addBuildingDetails(addBuilding, listener)
    }

    @Test
    fun updateBuildingDetails() {
        val addBuilding = AddBuilding()
        doNothing().`when`(spy(mAddBuildingRepository)).updateBuildingDetails(addBuilding, listener)
        mAddBuildingRepository.updateBuildingDetails(addBuilding, listener)
    }

    @Test
    fun getLocationDetails() {
        doNothing().`when`(spy(mAddBuildingRepository)).getLocationDetails(listener)
        mAddBuildingRepository.getLocationDetails(listener)
    }

    @Test
    fun sucessREsponseForAddBuilding() {
        /* val mockedApiInterface = mRestClient

         val mockedCall = mock(Call::class.java)
         val addBuilding = AddBuilding()
         `when`(mockedApiInterface!!.addBuilding(addBuilding)).thenReturn(mockedCall as Call<ResponseBody>)
         try{
         doAnswer { invocation ->
              val callback:Callback<ResponseBody> = invocation!!.getArgument(0)
              val getResponse = 200

              callback.onResponse(mockedCall, Response.success(getResponse as ResponseBody))
              null
          }.`when`(mockedCall).enqueue(object: Callback<ResponseBody>{
              override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                  TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
              }

              override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                  TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
              }
          })
         }
         catch (e:Exception){

         }

         mAddBuildingRepository.addBuildingDetails(addBuilding,listener)*/
    }


    @Test
    fun sucessForAddingBuilding() {
        val addBuilding = AddBuilding()
        val conference = mock<ConferenceService>()
        val mockedCall = mock<Call<ResponseBody>>()

       // `when`(mockedCall).thenReturn()
        `when`(conference.addBuilding(addBuilding)).thenReturn(mockedCall)

        doAnswer { invocation ->
            val callback: Callback<ResponseBody> = invocation!!.getArgument(0)
            val getResponse = 200

            callback.onResponse(mockedCall, Response.success(getResponse as ResponseBody))
            null
        }.`when`(mockedCall).enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })




        mAddBuildingRepository.addBuildingDetails(addBuilding, listener)
    }
}