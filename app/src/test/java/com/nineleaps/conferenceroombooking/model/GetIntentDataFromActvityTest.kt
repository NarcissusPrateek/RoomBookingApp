package com.nineleaps.conferenceroombooking.model

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class GetIntentDataFromActvityTest {

    @Test
    fun testGetIntentDataFromActivivty() {
        val getIntentData = GetIntentDataFromActvity()
        val data = jacksonObjectMapper().writeValueAsString(getIntentData)
        assertEquals(
            data,
            "{\"fromTime\":null,\"toTime\":null,\"date\":null,\"capacity\":null,\"buildingName\":null,\"roomName\":null,\"roomId\":null,\"buildingId\":null,\"listOfDays\":[],\"toDate\":null,\"fromTimeList\":[],\"toTimeList\":[],\"purpose\":null,\"bookingId\":null,\"name\":null,\"purposeVisible\":null,\"ccmail\":null}"
        )
    }
}