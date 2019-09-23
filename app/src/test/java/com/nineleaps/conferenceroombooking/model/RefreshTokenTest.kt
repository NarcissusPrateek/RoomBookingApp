package com.nineleaps.conferenceroombooking.model

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class RefreshTokenTest{

    @Test
    fun testRefreshToken(){
        val refereshToken = RefreshToken()
        val data = jacksonObjectMapper().writeValueAsString(refereshToken)
        assertEquals(data,"{\"jwtToken\":null,\"refreshToken\":null}")
    }
}