package com.nineleaps.conferenceroombooking.model

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class BuildingTest{

    @Test
    fun testBuildingModel(){
        val buildingModel = Building()
        val buildingData = jacksonObjectMapper().writeValueAsString(buildingModel)
        assertEquals(buildingData,"{\"buildingId\":null,\"buildingName\":null,\"buildingPlace\":null,\"locationId\":null}")
    }
}