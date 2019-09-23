package com.nineleaps.conferenceroombooking.model

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.nineleaps.conferenceroombooking.Models.ConferenceList
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class ConferenceListTest {

    @Test
    fun testConferenceList() {
        val conferenceList = ConferenceList()
        val conferenceListData = jacksonObjectMapper().writeValueAsString(conferenceList)
        assertEquals(
            conferenceListData,
            "{\"roomName\":null,\"capacity\":0,\"buildingName\":null,\"roomId\":null,\"buildingId\":null,\"amenities\":null,\"place\":null,\"permission\":null,\"mstatus\":null}"
        )
    }
}