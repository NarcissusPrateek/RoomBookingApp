package com.nineleaps.conferenceroombooking.addBuilding.ui

import com.nhaarman.mockitokotlin2.doReturn
import com.nineleaps.conferenceroombooking.BaseActivity
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class AddingBuildingTest{

    @Mock
    lateinit var mBaseActivity:BaseActivity

    @InjectMocks
    lateinit var mAddingBuilding: AddingBuilding


}