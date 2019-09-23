package com.nineleaps.conferenceroombooking.model

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.nineleaps.conferenceroombooking.AddConferenceRoom
import com.nineleaps.conferenceroombooking.GetAllAmenities
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class AddConferenceRoomTest{
    val objectMapper = jacksonObjectMapper()

    @Test
    fun testAddConferenceRoomModel(){
        val addConferenceRoom = AddConferenceRoom()
        val conferenceData = objectMapper.writeValueAsString(addConferenceRoom)
        assertEquals(conferenceData,"{\"roomId\":0,\"newRoomName\":null,\"roomName\":null,\"capacity\":0,\"amenities\":null,\"permission\":false,\"bid\":0}")
    }

    @Test
    fun testGetAllAmenitiesModel(){
        val amenities = GetAllAmenities()
        val amenitiesData = objectMapper.writeValueAsString(amenities)
        assertEquals(amenitiesData,"{\"amenityId\":0,\"amenityName\":null}")
    }
}