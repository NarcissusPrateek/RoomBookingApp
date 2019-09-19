package com.nineleaps.conferenceroombooking.signIn.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nineleaps.conferenceroombooking.model.SignIn
import com.nineleaps.conferenceroombooking.services.ResponseListener
import com.nineleaps.conferenceroombooking.signIn.repository.CheckRegistrationRepository
import com.nineleaps.conferenceroombooking.utils.Constants
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class CheckRegistrationViewModelTest{

    @Rule
    @JvmField
    val executor = InstantTaskExecutorRule()

    val repo = mock(CheckRegistrationRepository::class.java)
    val captor = argumentCaptor<ResponseListener>()
    val viewModel = CheckRegistrationViewModel()
    val sigIn = SignIn()

    @Test
    fun testCheckRegistration(){
        viewModel.setCheckRegistrationRepo(repo)
        doNothing().`when`(repo).checkRegistration(com.nhaarman.mockitokotlin2.any(),com.nhaarman.mockitokotlin2.any())
        viewModel.checkRegistration("")
        verify(repo, times(1)).checkRegistration(com.nhaarman.mockitokotlin2.any(),captor.capture())
        captor.firstValue.onSuccess(sigIn)
        captor.firstValue.onFailure(Constants.INTERNAL_SERVER_ERROR)
    }

    @Test
    fun testReturnSuccessCode(){
        lenient().doReturn(sigIn).`when`(spy(viewModel.returnSuccessCode())).value
    }

    @Test
    fun testReturnFailureCode(){
        lenient().doReturn(Constants.INTERNAL_SERVER_ERROR).`when`(spy(viewModel.returnFailureCode())).value
    }
}