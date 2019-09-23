package com.nineleaps.conferenceroombooking.model

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class BookingTest {

    @Test
    fun testBookingModel() {
        val booking = Booking()
        val bookingData = jacksonObjectMapper().writeValueAsString(booking)
        assertEquals(
            bookingData,
            "{\"email\":null,\"roomId\":0,\"buildingId\":0,\"fromTime\":null,\"toTime\":null,\"purpose\":null,\"purposeVisible\":null,\"ccmail\":null}"
        )
    }
}