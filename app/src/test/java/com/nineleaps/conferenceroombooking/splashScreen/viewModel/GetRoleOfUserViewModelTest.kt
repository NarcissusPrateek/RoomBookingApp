package com.nineleaps.conferenceroombooking.splashScreen.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doNothing
import com.nhaarman.mockitokotlin2.verify
import com.nineleaps.conferenceroombooking.services.ResponseListener
import com.nineleaps.conferenceroombooking.splashScreen.repository.GetRoleOfUser
import com.nineleaps.conferenceroombooking.utils.Constants
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class GetRoleOfUserViewModelTest{

    @Rule
    @JvmField
    val executor = InstantTaskExecutorRule()

    val repo = mock(GetRoleOfUser::class.java)
    val captor = argumentCaptor<ResponseListener>()
    val viewModel = GetRoleOfUserViewModel()

    @Test
    fun testGetUserRoleForUser(){
        viewModel.setGetRoleOfUserRepo(repo)
        doNothing().`when`(repo).getRole(any(), any())
        viewModel.getUserRole("")
        verify(repo).getRole(any(),captor.capture())
        captor.firstValue.onSuccess(0)
        captor.firstValue.onFailure(Constants.INTERNAL_SERVER_ERROR)
    }

    @Test
    fun testReturnSuccessCodeForUserRole(){
        lenient().doReturn(0).`when`(spy(viewModel.returnSuccessCodeForUserROle())).value
    }

    @Test
    fun testReturnFailureCodeForUserRole(){
        lenient().doReturn(Constants.INTERNAL_SERVER_ERROR).`when`(spy(viewModel.returnFailureCodeForUserRole())).value
    }
}