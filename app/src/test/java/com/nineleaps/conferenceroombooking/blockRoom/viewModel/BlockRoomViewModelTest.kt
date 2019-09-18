package com.nineleaps.conferenceroombooking.blockRoom.viewModel

import android.provider.SyncStateContract
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nineleaps.conferenceroombooking.blockRoom.repository.BlockRoomRepository
import com.nineleaps.conferenceroombooking.model.BlockRoom
import com.nineleaps.conferenceroombooking.model.BlockingConfirmation
import com.nineleaps.conferenceroombooking.services.ResponseListener
import com.nineleaps.conferenceroombooking.utils.Constants
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class BlockRoomViewModelTest {

    @InjectMocks
    lateinit var mBlockRoomViewModel: BlockRoomViewModel


    @Rule
    @JvmField
    var executor = InstantTaskExecutorRule()

    val blockingConfirmation = BlockingConfirmation()

    val listenerCaptor = argumentCaptor<ResponseListener>()

    val repoMock = mock(
        BlockRoomRepository::class.java
    )

    val viewModel = BlockRoomViewModel()

    val mRoom = BlockRoom()

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun testReturnSuccessForBlockRoom() {
        lenient().doReturn(Constants.OK_RESPONSE).`when`(spy(viewModel.returnSuccessForBlockRoom())).value
    }

    @Test
    fun testReturnResponseErrorForBlockRoom() {
        lenient().doReturn(Constants.INTERNAL_SERVER_ERROR).`when`(spy(viewModel.returnResponseErrorForBlockRoom()))
            .value
    }

    @Test
    fun testReturnSuccessForConfirmation() {
        lenient().doReturn("{}").`when`(spy(viewModel.returnSuccessForConfirmation())).value
    }

    @Test
    fun testReturnResponseErrorForConfirmation() {
        lenient().doReturn(Constants.INTERNAL_SERVER_ERROR).`when`(spy(viewModel.returnResponseErrorForConfirmation()))
            .value

    }

    @Test
    fun testBlockRoom(){
        viewModel.setBlockRoomRepo(repoMock)
        doNothing().`when`(repoMock).blockRoom(com.nhaarman.mockitokotlin2.any(),com.nhaarman.mockitokotlin2.any())
        viewModel.blockRoom(mRoom)
        verify(repoMock, times(1)).blockRoom(com.nhaarman.mockitokotlin2.any(),listenerCaptor.capture())
        listenerCaptor.firstValue.onSuccess(Constants.OK_RESPONSE)
        listenerCaptor.firstValue.onFailure("Failure")
    }

    @Test
    fun testBlockingStatus(){
        viewModel.setBlockRoomRepo(repoMock)
        doNothing().`when`(repoMock).blockRoom(com.nhaarman.mockitokotlin2.any(),com.nhaarman.mockitokotlin2.any())
        viewModel.blockingStatus(mRoom)
        verify(repoMock, times(1)).blockingStatus(com.nhaarman.mockitokotlin2.any(),listenerCaptor.capture())
        listenerCaptor.firstValue.onFailure(Constants.INTERNAL_SERVER_ERROR)
        listenerCaptor.firstValue.onSuccess(blockingConfirmation)

    }
}