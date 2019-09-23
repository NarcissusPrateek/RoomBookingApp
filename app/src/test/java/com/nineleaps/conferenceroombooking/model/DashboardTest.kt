package com.nineleaps.conferenceroombooking.model

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class DashboardTest {
    val objectMapper = jacksonObjectMapper()

    @Test
    fun testDashBoardModel() {
        val dashBoard = Dashboard()
        val dashboardData = objectMapper.writeValueAsString(dashBoard)
        assertEquals(
            dashboardData,
            "{\"roomId\":null,\"email\":null,\"fromTime\":null,\"toTime\":null,\"buildingName\":null,\"roomName\":null,\"purpose\":null,\"status\":null,\"bookingId\":null,\"name\":null,\"amenities\":null,\"organizer\":null,\"recurringmeetingId\":null,\"ccmail\":null,\"tagged\":false}"
        )
    }

    @Test
    fun testPaginationMetaData(){
        val pagination = PaginationMetaData()
        val paginationData = objectMapper.writeValueAsString(pagination)
        assertEquals(paginationData,"{\"pageCount\":null,\"pageSize\":null,\"currentPage\":null,\"totalPages\":null,\"previousPage\":null,\"nextPage\":null}")
    }

    @Test
    fun testDashBoardDetails(){
        val dashboardDetails = DashboardDetails()
        val dashboardDetailsData = objectMapper.writeValueAsString(dashboardDetails)
        assertEquals(dashboardDetailsData,"{\"dashboard\":null,\"paginationMetaData\":null}")
    }
}