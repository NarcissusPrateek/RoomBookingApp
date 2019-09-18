package com.nineleaps.conferenceroombooking.blockDashboard.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nineleaps.conferenceroombooking.Blocked
import com.nineleaps.conferenceroombooking.ViewModel.BlockedDashboardViewModel
import com.nineleaps.conferenceroombooking.blockDashboard.repository.BlockDashboardRepository
import com.nineleaps.conferenceroombooking.services.ResponseListener
import com.nineleaps.conferenceroombooking.utils.Constants
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class BlockedDashboardViewModelTest {

    @InjectMocks
    lateinit var mBlockedDashboardViewModel: BlockedDashboardViewModel

    @Rule @JvmField
    var executor = InstantTaskExecutorRule()

    val listenerCaptor = argumentCaptor<ResponseListener>()
    val repoMock = mock(BlockDashboardRepository::class.java)
    val viewModel = BlockedDashboardViewModel()
    val blocked = Blocked()
    val list = emptyList<Blocked>()

    @Before
    fun setUp(){
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun testReturnBlockedRoomList(){
        lenient().doReturn("{}").`when`(spy(viewModel.returnBlockedRoomList())).value
    }

    @Test
    fun testReturnFailureCodeFromBlockedApi(){
        lenient().doReturn(500).`when`(spy(viewModel.returnFailureCodeFromBlockedApi())).value
    }

    @Test
    fun testReturnSuccessCodeForUnBlockRoom(){
        lenient().doReturn(200).`when`(spy(viewModel.returnSuccessCodeForUnBlockRoom())).value
    }

    @Test
    fun testReturnFailureCodeForUnblockRoom(){
        lenient().doReturn(500).`when`(spy(viewModel.returnFailureCodeForUnBlockRoom())).value
    }

    @Test
    fun testForGetBlockedList(){
        viewModel.setBlockedRoomDashboardRepo(repoMock)
        doNothing().`when`(repoMock).getBlockedList(com.nhaarman.mockitokotlin2.any())
        viewModel.getBlockedList()
        verify(repoMock, times(1)).getBlockedList(listenerCaptor.capture())
        listenerCaptor.firstValue.onSuccess(list)
        listenerCaptor.firstValue.onFailure("failure")
    }

    @Test
    fun testForUnBlockRoom(){
        viewModel.setBlockedRoomDashboardRepo(repoMock)
        doNothing().`when`(repoMock).unblockRoom(com.nhaarman.mockitokotlin2.any(),com.nhaarman.mockitokotlin2.any())
        viewModel.unBlockRoom(0)
        verify(repoMock, times(1)).unblockRoom(com.nhaarman.mockitokotlin2.any(),listenerCaptor.capture())
        listenerCaptor.firstValue.onSuccess(Constants.OK_RESPONSE)
        listenerCaptor.firstValue.onFailure("failure")
    }
}