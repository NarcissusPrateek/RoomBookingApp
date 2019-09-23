package com.nineleaps.conferenceroombooking.model

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class UpdateBookingTest{

    @Test
    fun testUpdateBooking(){
        val updateBooking = UpdateBooking()
        val data = jacksonObjectMapper().writeValueAsString(updateBooking)
        assertEquals(data,"{\"newFromTime\":null,\"newtotime\":null,\"bookingId\":null,\"purpose\":null,\"ccmail\":null}")
    }
}