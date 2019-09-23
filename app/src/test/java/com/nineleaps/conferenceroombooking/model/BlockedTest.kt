package com.nineleaps.conferenceroombooking.model

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.nineleaps.conferenceroombooking.Blocked
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class BlockedTest{
    val objectMapper = jacksonObjectMapper()

    @Test
    fun testBlockedModel(){
        val blockModel = Blocked()
        val blockedData = objectMapper.writeValueAsString(blockModel)
        assertEquals(blockedData,"{\"roomId\":0,\"buildingName\":null,\"roomName\":null,\"fromTime\":null,\"toTime\":null,\"purpose\":null,\"bookingId\":null}")
    }
}