package com.nineleaps.conferenceroombooking.model

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class RoomDetailsTest {

    @Test
    fun testRoomDetailsModel() {
        val roomDetails = RoomDetails()
        val data = jacksonObjectMapper().writeValueAsString(roomDetails)
        assertEquals(
            data,
            "{\"roomId\":null,\"buildingId\":null,\"capacity\":null,\"roomName\":null,\"status\":null,\"buildingName\":null,\"place\":null,\"amenities\":null,\"permission\":false}"
        )
    }
}