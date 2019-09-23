package com.nineleaps.conferenceroombooking.model

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class BlockRoomTest{

    @Test
    fun testBlockRoomModel(){
        val blockRoom = BlockRoom()
        val blockRoomData = jacksonObjectMapper().writeValueAsString(blockRoom)
        assertEquals(blockRoomData,"{\"email\":null,\"fromTime\":null,\"toTime\":null,\"status\":null,\"purpose\":null,\"cid\":0,\"bid\":0}")
    }
}