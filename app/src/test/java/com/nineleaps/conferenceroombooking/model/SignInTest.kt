package com.nineleaps.conferenceroombooking.model

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class SignInTest{

    @Test
    fun testSignIn(){
        val signIn = SignIn()
        val data = jacksonObjectMapper().writeValueAsString(signIn)
        assertEquals(data,"{\"token\":null,\"statusCode\":null,\"refreshToken\":null}")
    }
}