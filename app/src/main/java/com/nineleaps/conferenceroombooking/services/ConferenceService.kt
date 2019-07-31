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
import com.nineleaps.conferenceroombooking.model.RoomDetails as RoomDetails1

interface ConferenceService {

    @GET("api/Building")
    fun getBuildingList(
        @Header("Authorization") token: String
    ): Call<List<Building>>

    @POST("api/AvailableRooms")
    fun getConferenceRoomList(
        @Header("Authorization") token: String,
        @Body availableRoom: InputDetailsForRoom
    ): Call<List<RoomDetails1>>


    @POST("api/SuggestedRooms")
    fun getSuggestedRooms(
        @Header("Authorization") token: String,
        @Body availableRoom: InputDetailsForRoom
    ): Call<List<RoomDetails1>>


    @POST("api/SuggestionRecurringMeeting")
    fun getSuggestedRoomsForRecurring(
        @Header("Authorization") token: String,
        @Body availableRoom: ManagerConference
    ): Call<List<RoomDetails1>>

    @GET("api/Login")
    fun getRequestCode(
        @Header("Authorization") token: String,
        @Query("deviceId") deviceId: String
    ): Call<SignIn>

    @GET("api/CheckEmployeeRole")
    fun getRole(
        @Header("Authorization") token: String,
        @Query("emailId") emailId: String
    ): Call<Int>


    @POST("api/Dashboard")
    fun getDashboard(
        @Header("Authorization") token: String,
        @Body bookingDashboardInput: BookingDashboardInput
    ): Call<DashboardDetails>

    @POST("api/UserLogin")
    fun addEmployee(
        @Header("Authorization") token: String,
        @Header("UserId") userId: String,
        @Body newEmoployee: Employee
    ): Call<ResponseBody>

    @POST("api/BookRoom")
    fun addBookingDetails(
        @Header("Authorization") token: String,
        @Body booking: Booking
    ): Call<ResponseBody>

    @PUT("api/CancelBooking")
    fun cancelBookedRoom(
        @Header("Authorization") token: String,
        @Query("meetId") meetId: Int?
    ): Call<ResponseBody>

    @PUT("api/CancelBooking")
    fun cancelRecurringBooking(
            @Header("Authorization") token: String,
            @Query("meetId") meetId: Int?,
            @Query("recurringMeetId") recurringMeetId:String
    ): Call<ResponseBody>

    @GET("api/getPasscode")
    fun getPasscode(
        @Header("Authorization") token: String,
        @Query("GenerateNewPasscode") generateNewPasscode: Boolean,
        @Query("emailId") emailId: String
    ): Call<String>

    @GET("api/Employee")
    fun getEmployees(
        @Header("Authorization") token: String,
        @Query("emailId") emailId: String
    ): Call<List<EmployeeList>>

    @POST("api/BookRecurringMeeting")
    fun addManagerBookingDetails(
        @Header("Authorization") token: String,
        @Body managerBooking: ManagerBooking
    ): Call<ResponseBody>

    @POST("api/AvailableRoomsForRecurring")
    fun getMangerConferenceRoomList(
        @Header("Authorization") token: String,
        @Body availableRoom: ManagerConference
    ): Call<List<RoomDetails1>>
//    // Pratheek's.....

    @POST("api/AddBuilding")
    fun addBuilding(
        @Header("Authorization") token: String,
        @Body newBuilding: AddBuilding
    ): Call<ResponseBody>

    @PUT("api/UpdateBuilding")
    fun updateBuilding(
        @Header("Authorization") token: String,
        @Body newBuilding: AddBuilding
    ): Call<ResponseBody>


    @POST("api/AddRoom")
    fun addConference(
        @Header("Authorization") token: String,
        @Body newConferenceRoom: AddConferenceRoom
    ): Call<ResponseBody>

    @PUT("api/UpdateRoom")
    fun updateConference(
        @Header("Authorization") token: String,
        @Body newConferenceRoom: AddConferenceRoom
    ): Call<ResponseBody>

    @POST("api/BlockConfirmation")
    fun blockConfirmation(
        @Header("Authorization") token: String,
        @Body room: BlockRoom
    ): Call<BlockingConfirmation>

    @POST("api/BlockRoom")
    fun blockconference(
        @Header("Authorization") token: String,
        @Body room: BlockRoom
    ): Call<ResponseBody>

    @GET("api/GetBlockedRooms")
    fun getBlockedConference(
        @Header("Authorization") token: String
        ): Call<List<Blocked>>

    @PUT("api/UnblockRoom")
    fun unBlockingConferenceRoom(
        @Header("Authorization") token: String,
        @Body meetId: Int
    ): Call<ResponseBody>

    @GET("api/ConferenceRooms")
    fun conferenceList(
        @Header("Authorization") token: String,
        @Query("buildingId") id: Int
    ): Call<List<ConferenceList>>

    @PUT("api/UpdateBooking")
    fun update(
        @Header("Authorization") token: String,
        @Body updateBooking: UpdateBooking
    ): Call<ResponseBody>

    @GET("api/AccessToken")
    fun getAccessToken(
        @Body refreshToken: RefreshToken
    ): Call<RefreshToken>

    @DELETE("api/deleteBuilding")
    fun deleteBuilding(
            @Header("Authorization") token: String,
            @Query("buildingId") id: Int
    ) : Call<ResponseBody>

    @DELETE("api/deleteRoom")
    fun deleteRoom(
            @Header("Authorization") token: String,
            @Query("roomId") id: Int
    ) : Call<ResponseBody>
}