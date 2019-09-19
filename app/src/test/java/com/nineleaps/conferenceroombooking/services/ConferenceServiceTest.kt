package com.nineleaps.conferenceroombooking.services

import com.example.conferenceroomapp.model.InputDetailsForRoom
import com.google.gson.GsonBuilder
import com.nineleaps.conferenceroombooking.model.AddBuilding
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.Okio
import org.junit.*
import org.junit.Assert.*
import org.mockito.MockitoAnnotations
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.HttpURLConnection

class ConferenceServiceTest {

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

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun testGetAllAmenities() {
        enqueueResponseFromJsonFile("get-all-amenities.json")
        val amenities = mConferenceService.getAllAmenities().execute().body()
        val recordedRequest = mockWebServer.takeRequest()
        assertEquals("GET", recordedRequest.method)
        assertEquals("/ddd/api/v1/amenities", recordedRequest.path)
        assertNotNull(amenities)
        assertEquals(2, amenities!!.size)
        val amenity = amenities[0]
        assertEquals(amenity.amenityName, "Projector")
        assertEquals(amenity.amenityId, 1)

    }

    @Test
    fun testGetConferenceRoomList() {
        enqueueResponseFromJsonFile("get-available-rooms.json")
        val inputDetailsForRoom =
            InputDetailsForRoom("2019-09-11 09:45:00Z", "2019-09-11 10:45:00Z", 2, "sampletesting03@gmail.com")
        val availableRooms = mConferenceService.getConferenceRoomList(inputDetailsForRoom).execute().body()
        val recordedRequest = mockWebServer.takeRequest()
        assertEquals("POST", recordedRequest.method)
        assertEquals("/api/v1/availableRooms", recordedRequest.path)
        val room = availableRooms!![0]
        assertEquals(2, room.roomId)
        assertEquals("Welcome 2nd Floor", room.roomName)
    }

    @Test
    fun testAddBuilding() {
        val response = MockResponse()
        response.status = "HTTP/1.1 201 OK"
        mockWebServer.enqueue(response)
        val addBuilding = AddBuilding("Main Building",2,null)
        val addingBuilding = mConferenceService.addBuilding(addBuilding).execute().code()
        assertEquals(addingBuilding,201)

    }


    private fun enqueueResponseFromJsonFile(fileName: String) {
        val stream = javaClass.classLoader.getResourceAsStream("api-responses/$fileName")
        val source = Okio.buffer(Okio.source(stream))
        mockWebServer.enqueue(MockResponse().setBody(source.readString(Charsets.UTF_8)))
    }
}