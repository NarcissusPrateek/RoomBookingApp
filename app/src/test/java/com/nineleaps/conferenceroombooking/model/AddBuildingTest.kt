package com.nineleaps.conferenceroombooking.model

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class AddBuildingTest{
    val objectMapper = jacksonObjectMapper()



    @Test
    fun testAddBuildingModel(){
        val addBuilding = AddBuilding()
        val data = objectMapper.writeValueAsString(addBuilding)
        assertEquals(data,"{\"buildingName\":null,\"place\":null,\"buildingId\":null}")
    }

    @Test
    fun testGetLocationModel(){
        val location = Location()
        val locationData = objectMapper.writeValueAsString(location)
        assertEquals(locationData,"{\"locaionName\":null,\"locationId\":null}")
    }
}