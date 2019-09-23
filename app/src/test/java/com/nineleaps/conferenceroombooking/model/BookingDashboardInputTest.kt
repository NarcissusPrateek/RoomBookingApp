package com.nineleaps.conferenceroombooking.model

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class BookingDashboardInputTest{

    @Test
    fun testBookingDashboardInputModel(){
        val bookingDashboardInput = BookingDashboardInput()
        val bookindDashBoardInputData = jacksonObjectMapper().writeValueAsString(bookingDashboardInput)
        assertEquals(bookindDashBoardInputData,"{\"email\":null,\"status\":null,\"pageSize\":null,\"pageNumber\":null,\"currentDatTime\":null}")
    }
}