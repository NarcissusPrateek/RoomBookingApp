package com.nineleaps.conferenceroombooking.model

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class EmployeeListTest{

    @Test
    fun testEmployeeList(){
        val employeeList = EmployeeList()
        val employeeListData = jacksonObjectMapper().writeValueAsString(employeeList)
        assertEquals(employeeListData,"{\"email\":null,\"name\":null}")
    }
}