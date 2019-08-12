package com.nineleaps.conferenceroombooking.services

import com.example.conferenceroomapp.model.InputDetailsForRoom
import com.example.conferenceroomapp.model.ManagerConference
import com.nineleaps.conferenceroombooking.AddConferenceRoom
import com.nineleaps.conferenceroombooking.Blocked
import com.nineleaps.conferenceroombooking.Models.ConferenceList
import com.nineleaps.conferenceroombooking.model.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*
import com.nineleaps.conferenceroombooking.model.RoomDetails

interface ConferenceService {

    @GET("api/Building")
    fun getBuildingList(
    ): Call<List<Building>>

    @POST("api/availableRooms")
    fun getConferenceRoomList(
        @Body availableRoom: InputDetailsForRoom
    ): Call<List<RoomDetails>>


//    @POST("api/SuggestedRooms")
//    fun getSuggestedRooms(
//        @Body availableRoom: InputDetailsForRoom
//    ): Call<List<RoomDetails1>>


    @POST("api/SuggestionRecurringMeeting")
    fun getSuggestedRoomsForRecurring(
        @Body availableRoom: ManagerConference
    ): Call<List<RoomDetails>>

    @GET("api/Login")
    fun getRequestCode(
        @Query("deviceId") deviceId: String
    ): Call<SignIn>

    @POST("api/validateRefreshToken")
    fun getNewToken(
        @Body mRefreshToken: RefreshToken
    ): Call<RefreshToken>


    @GET("api/CheckEmployeeRole")
    fun getRole(
        @Query("emailId") emailId: String
    ): Call<Int>


    @POST("api/Dashboard")
    fun getDashboard(
        @Body bookingDashboardInput: BookingDashboardInput
    ): Call<DashboardDetails>

    @POST("api/BookRoom")
    fun addBookingDetails(
        @Body booking: Booking
    ): Call<ResponseBody>

    @PUT("api/CancelBooking")
    fun cancelBookedRoom(
        @Query("meetId") meetId: Int?
    ): Call<ResponseBody>

    @PUT("api/CancelBooking")
    fun cancelRecurringBooking(
        @Query("meetId") meetId: Int?,
        @Query("recurringMeetId") recurringMeetId: String
    ): Call<ResponseBody>

    @GET("api/getPasscode")
    fun getPasscode(
        @Query("GenerateNewPasscode") generateNewPasscode: Boolean,
        @Query("emailId") emailId: String
    ): Call<String>

    @GET("api/Employee")
    fun getEmployees(
        @Query("emailId") emailId: String
    ): Call<List<EmployeeList>>

    @POST("api/BookRecurringMeeting")
    fun addManagerBookingDetails(
        @Body managerBooking: ManagerBooking
    ): Call<ResponseBody>

    @POST("api/AvailableRoomsForRecurring")
    fun getMangerConferenceRoomList(
        @Body availableRoom: ManagerConference
    ): Call<List<RoomDetails>>
//    // Pratheek's.....

    @POST("api/AddBuilding")
    fun addBuilding(
        @Body newBuilding: AddBuilding
    ): Call<ResponseBody>

    @PUT("api/UpdateBuilding")
    fun updateBuilding(
        @Body newBuilding: AddBuilding
    ): Call<ResponseBody>


    @POST("api/AddRoom")
    fun addConference(
        @Body newConferenceRoom: AddConferenceRoom
    ): Call<ResponseBody>

    @PUT("api/UpdateRoom")
    fun updateConference(
        @Body newConferenceRoom: AddConferenceRoom
    ): Call<ResponseBody>

    @POST("api/BlockConfirmation")
    fun blockConfirmation(
        @Body room: BlockRoom
    ): Call<BlockingConfirmation>

    @POST("api/BlockRoom")
    fun blockconference(
        @Body room: BlockRoom
    ): Call<ResponseBody>

    @GET("api/GetBlockedRooms")
    fun getBlockedConference(
    ): Call<List<Blocked>>

    @PUT("api/UnblockRoom")
    fun unBlockingConferenceRoom(
        @Body meetId: Int
    ): Call<ResponseBody>

    @GET("api/roomsById")
    fun conferenceList(
        @Query("buildingId") id: Int
    ): Call<List<ConferenceList>>

    @PUT("api/UpdateBooking")
    fun update(
        @Body updateBooking: UpdateBooking
    ): Call<ResponseBody>

    @DELETE("api/deleteBuilding")
    fun deleteBuilding(
        @Query("buildingId") id: Int
    ): Call<ResponseBody>

    @DELETE("api/deleteRoom")
    fun deleteRoom(
        @Query("roomId") id: Int
    ): Call<ResponseBody>
}