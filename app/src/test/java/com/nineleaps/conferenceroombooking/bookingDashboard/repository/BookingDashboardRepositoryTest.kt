package com.nineleaps.conferenceroombooking.bookingDashboard.repository

import com.nineleaps.conferenceroombooking.model.BookingDashboardInput
import com.nineleaps.conferenceroombooking.model.DashboardDetails
import com.nineleaps.conferenceroombooking.services.ConferenceService
import com.nineleaps.conferenceroombooking.services.ResponseListener
import com.nineleaps.conferenceroombooking.services.RestClient
import com.nineleaps.conferenceroombooking.utils.Constants
import okhttp3.ResponseBody
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@RunWith(MockitoJUnitRunner::class)
class BookingDashboardRepositoryTest {

    @Captor
    lateinit var argumentCaptorForDashBoardDetails: ArgumentCaptor<Callback<DashboardDetails>>

    @Captor
    lateinit var argumentCaptorForReponseBody: ArgumentCaptor<Callback<ResponseBody>>

    @Captor
    lateinit var argumentCaptorForString: ArgumentCaptor<Callback<String>>

    @Mock
    lateinit var callMockForDashboardDetails: Call<DashboardDetails>

    @Mock
    lateinit var callMockForResponseBody: Call<ResponseBody>

    @Mock
    lateinit var callMockForString: Call<String>

    @Mock
    lateinit var responseBodyForDashboardDetails: DashboardDetails

    @Mock
    lateinit var responseBody: ResponseBody

    @InjectMocks
    lateinit var mBookingDashboardRepository: BookingDashboardRepository

    @Mock
    lateinit var conferenceService: ConferenceService

    @Mock
    lateinit var listener: ResponseListener

    val mBookingDashboardInput = BookingDashboardInput()
    val t = Throwable()


//------------------------------Get Booking List------------------------------------------------------------------------
    /**
     * Get Booking List Success for 200 Ok Response
     */
    @Test
    fun testSuccessOkResponseForGetBookingList() {
        RestClient.setMockService(conferenceService)
        `when`(conferenceService.getDashboard(mBookingDashboardInput)).thenReturn(callMockForDashboardDetails)
        mBookingDashboardRepository.getBookingList(mBookingDashboardInput, listener)
        verify(callMockForDashboardDetails).enqueue(argumentCaptorForDashBoardDetails.capture())
        argumentCaptorForDashBoardDetails.value.onResponse(
            callMockForDashboardDetails,
            Response.success(Constants.OK_RESPONSE, responseBodyForDashboardDetails)
        )
        verify(listener, times(1)).onSuccess(responseBodyForDashboardDetails)
    }

    /**
     * Get Booking List Success for 204  Response
     */
    @Test
    fun testSuccessButFailureForGetBookingList() {
        RestClient.setMockService(conferenceService)
        `when`(conferenceService.getDashboard(mBookingDashboardInput)).thenReturn(callMockForDashboardDetails)
        mBookingDashboardRepository.getBookingList(mBookingDashboardInput, listener)
        verify(callMockForDashboardDetails).enqueue(argumentCaptorForDashBoardDetails.capture())
        argumentCaptorForDashBoardDetails.value.onResponse(
            callMockForDashboardDetails,
            Response.success(Constants.NO_CONTENT_FOUND, responseBodyForDashboardDetails)
        )
        verify(listener, times(1)).onFailure(Constants.NO_CONTENT_FOUND)
    }

    /**
     * Get Booking List Success for 500 Response
     */
    @Test
    fun testFailureForGetBookingList() {
        RestClient.setMockService(conferenceService)
        `when`(conferenceService.getDashboard(mBookingDashboardInput)).thenReturn(callMockForDashboardDetails)
        mBookingDashboardRepository.getBookingList(mBookingDashboardInput, listener)
        verify(callMockForDashboardDetails).enqueue(argumentCaptorForDashBoardDetails.capture())
        argumentCaptorForDashBoardDetails.value.onFailure(
            callMockForDashboardDetails,
            t
        )
        verify(listener, times(1)).onFailure(Constants.INTERNAL_SERVER_ERROR)
    }

//---------------------------------Cancel Booking-----------------------------------------------------------------------
    /**
     *  Cancel Booking Success for 200 Ok Response
     */
    @Test
    fun testSuccessForCancelBooking(){
        RestClient.setMockService(conferenceService)
        `when`(conferenceService.cancelBookedRoom(0)).thenReturn(callMockForResponseBody)
        mBookingDashboardRepository.cancelBooking(0, listener)
        verify(callMockForResponseBody).enqueue(argumentCaptorForReponseBody.capture())
        argumentCaptorForReponseBody.value.onResponse(
            callMockForResponseBody,
            Response.success(Constants.OK_RESPONSE, responseBody)
        )
        verify(listener, times(1)).onSuccess(Constants.OK_RESPONSE)
    }

    /**
     * Cancel Booking Success But Failure 204
     */
    @Test
    fun testSuccessButFailureForCancelBooking(){
        RestClient.setMockService(conferenceService)
        `when`(conferenceService.cancelBookedRoom(0)).thenReturn(callMockForResponseBody)
        mBookingDashboardRepository.cancelBooking(0, listener)
        verify(callMockForResponseBody).enqueue(argumentCaptorForReponseBody.capture())
        argumentCaptorForReponseBody.value.onResponse(
            callMockForResponseBody,
            Response.success(Constants.NO_CONTENT_FOUND, responseBody)
        )
        verify(listener, times(1)).onFailure(Constants.NO_CONTENT_FOUND)
    }

    /**
     * Cancel Booking  Failure 500
     */
    @Test
    fun testFailureForCancelBooking(){
        RestClient.setMockService(conferenceService)
        `when`(conferenceService.cancelBookedRoom(0)).thenReturn(callMockForResponseBody)
        mBookingDashboardRepository.cancelBooking(0, listener)
        verify(callMockForResponseBody).enqueue(argumentCaptorForReponseBody.capture())
        argumentCaptorForReponseBody.value.onFailure(
            callMockForResponseBody,
            t
        )
        verify(listener, times(1)).onFailure(Constants.INTERNAL_SERVER_ERROR)
    }

//---------------------------------Recurring Cancel Meeting--------------------------------------------------------------------
    /**
     * Recurring Cancel Meeting Success For 200
     */
    @Test
    fun testSuccessForCancelMeeting(){
        RestClient.setMockService(conferenceService)
        `when`(conferenceService.cancelRecurringBooking(0,"")).thenReturn(callMockForResponseBody)
        mBookingDashboardRepository.recurringCancelBooking(0,"",listener)
        verify(callMockForResponseBody).enqueue(argumentCaptorForReponseBody.capture())
        argumentCaptorForReponseBody.value.onResponse(
            callMockForResponseBody,
            Response.success(Constants.OK_RESPONSE, responseBody)
        )
        verify(listener, times(1)).onSuccess(Constants.OK_RESPONSE)
    }

    /**
     * Recurring Cancel Booking Success But Failure 204
     */
    @Test
    fun testSuccessButFailureForRecurringCancelBooking(){
        RestClient.setMockService(conferenceService)
        `when`(conferenceService.cancelRecurringBooking(0,"")).thenReturn(callMockForResponseBody)
        mBookingDashboardRepository.recurringCancelBooking(0,"", listener)
        verify(callMockForResponseBody).enqueue(argumentCaptorForReponseBody.capture())
        argumentCaptorForReponseBody.value.onResponse(
            callMockForResponseBody,
            Response.success(Constants.NO_CONTENT_FOUND, responseBody)
        )
        verify(listener, times(1)).onFailure(Constants.NO_CONTENT_FOUND)
    }

    /**
     * Recurring Cancel Booking Failure 500
     */
    @Test
    fun testFailureForRecurringCancelBooking(){
        RestClient.setMockService(conferenceService)
        `when`(conferenceService.cancelRecurringBooking(0,"")).thenReturn(callMockForResponseBody)
        mBookingDashboardRepository.recurringCancelBooking(0,"", listener)
        verify(callMockForResponseBody).enqueue(argumentCaptorForReponseBody.capture())
        argumentCaptorForReponseBody.value.onFailure(
            callMockForResponseBody,
            t
        )
        verify(listener, times(1)).onFailure(Constants.INTERNAL_SERVER_ERROR)
    }

//------------------------------------------Get Passcode----------------------------------------------------------------
    /**
     * Get Passcode Success FOr Ok response
     */
    @Test
    fun testSuccessForGetPasscode(){
        RestClient.setMockService(conferenceService)
        `when`(conferenceService.getPasscode(false,"")).thenReturn(callMockForString)
        mBookingDashboardRepository.getPasscode(false,"",listener)
        verify(callMockForString).enqueue(argumentCaptorForString.capture())
        argumentCaptorForString.value.onResponse(
            callMockForString,
            Response.success(Constants.OK_RESPONSE, "")
        )
        verify(listener, times(1)).onSuccess("")
    }

    /**
     * Get Passcode Failure Response
     */
    @Test
    fun testFailureForGetPasscode(){
        RestClient.setMockService(conferenceService)
        `when`(conferenceService.getPasscode(false,"")).thenReturn(callMockForString)
        mBookingDashboardRepository.getPasscode(false,"",listener)
        verify(callMockForString).enqueue(argumentCaptorForString.capture())
        argumentCaptorForString.value.onFailure(
            callMockForString,
            t
        )
        verify(listener, times(1)).onFailure(Constants.INVALID_TOKEN)
    }

    /**
     * Get Passcode Success but Failure Response
     */
    @Test
    fun testSuccessButFailureForGetPasscode(){
        RestClient.setMockService(conferenceService)
        `when`(conferenceService.getPasscode(false,"")).thenReturn(callMockForString)
        mBookingDashboardRepository.getPasscode(false,"",listener)
        verify(callMockForString).enqueue(argumentCaptorForString.capture())
        argumentCaptorForString.value.onResponse(
            callMockForString,
            Response.success(Constants.NO_CONTENT_FOUND, "")
        )
        verify(listener, times(1)).onFailure(Constants.NO_CONTENT_FOUND)
    }


}