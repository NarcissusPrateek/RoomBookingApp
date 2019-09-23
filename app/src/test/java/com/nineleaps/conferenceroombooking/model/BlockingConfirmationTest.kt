package com.nineleaps.conferenceroombooking.model

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class BlockingConfirmationTest{

    @Test
    fun testBlockConfirmationModel(){
        val blockConfirmation = BlockingConfirmation()
        val blockConfirmationData = jacksonObjectMapper().writeValueAsString(blockConfirmation)
        assertEquals(blockConfirmationData,"{\"name\":null,\"purpose\":null,\"mstatus\":0}")
    }
}