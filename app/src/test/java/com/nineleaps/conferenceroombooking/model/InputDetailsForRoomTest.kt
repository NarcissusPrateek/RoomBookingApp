package com.nineleaps.conferenceroombooking.model

import com.example.conferenceroomapp.model.InputDetailsForRoom
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class InputDetailsForRoomTest{

    @Test
    fun testInputDetailsForRoom(){
        val inputDetailsForRoom = InputDetailsForRoom()
        val inputDetailsForRoomData = jacksonObjectMapper().writeValueAsString(inputDetailsForRoom)
        assertEquals(inputDetailsForRoomData,"{\"fromTime\":null,\"toTime\":null,\"capacity\":0,\"email\":null}")
    }

}