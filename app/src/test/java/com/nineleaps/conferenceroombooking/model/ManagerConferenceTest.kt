package com.nineleaps.conferenceroombooking.model

import com.example.conferenceroomapp.model.ManagerConference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class ManagerConferenceTest{

    @Test
    fun testManagerConfernece(){
        val managerConference = ManagerConference()
        val data = jacksonObjectMapper().writeValueAsString(managerConference)
        assertEquals(data,"{\"fromTime\":null,\"toTime\":null,\"capacity\":0}")
    }
}