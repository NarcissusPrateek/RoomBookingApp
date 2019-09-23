package com.nineleaps.conferenceroombooking.model

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class ManagerBookingTest {

    @Test
    fun testManagerBooking() {
        val managerBooking = ManagerBooking()
        val data = jacksonObjectMapper().writeValueAsString(managerBooking)
        assertEquals(
            data,
            "{\"email\":null,\"roomId\":0,\"buildingId\":0,\"fromTime\":null,\"roomName\":null,\"toTime\":null,\"purpose\":null,\"capacity\":0,\"ccmail\":null}"
        )
    }
}