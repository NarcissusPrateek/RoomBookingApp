package com.nineleaps.conferenceroombooking.services

import com.example.conferenceroomapp.model.InputDetailsForRoom
import com.example.conferenceroomapp.model.ManagerConference
import com.nineleaps.conferenceroombooking.AddConferenceRoom
import com.nineleaps.conferenceroombooking.Blocked
import com.nineleaps.conferenceroombooking.GetAllAmenities
import com.nineleaps.conferenceroombooking.Models.ConferenceList
import com.nineleaps.conferenceroombooking.model.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*
import com.nineleaps.conferenceroombooking.model.RoomDetails

interface ConferenceService {


    //-----------------------------Ameneties Api------------------------------------------------------------------------
    /**
     * Ameneties Services
     */

    //Get All Ameneties Api
    @GET("api/v1/amenities")
    fun getAllAmenities(

    ): Call<List<GetAllAmenities>>

    //----------------------------Available Rooms for Single Booking Api------------------------------------------------
    /**
     * Get Conference Rooms for Booking Service
     */
    // Get Available Rooms for Booking, Api
    @POST("api/v1/availableRooms")
    fun getConferenceRoomList(
        @Body availableRoom: InputDetailsForRoom
    ): Call<List<RoomDetails>>

    //----------------------------Block Rooms Api's---------------------------------------------------------------------

    /**
     * Block Rooms Services
     */
    //Block Confirmation for Block Room, Api
    @POST("api/v1/blockConfirmation")
    fun blockConfirmation(
        @Body room: BlockRoom
    ): Call<BlockingConfirmation>

    //Block Room Api
    @POST("api/v1/blockRoom")
    fun blockconference(
        @Body room: BlockRoom
    ): Call<ResponseBody>

    //Get the List of Blocked Rooms Api
    @GET("api/v1/blockRoom")
    fun getBlockedConference(
    ): Call<List<Blocked>>

    //Unblock the Room Api
    @PATCH("api/v1/blockRoom/{meetId}")
    fun unBlockingConferenceRoom(
        @Path("meetId") meetId: Int
    ): Call<ResponseBody>


    //--------------------------Booking Api's---------------------------------------------------------------------------
    /**
     * Booking Functionality Services
     */

    //Update the Booking Api
    @PUT("api/v1/booking")
    fun update(
        @Body updateBooking: UpdateBooking
    ): Call<ResponseBody>

    //Book the Room Api
    @POST("api/v1/booking")
    fun addBookingDetails(
        @Body booking: Booking
    ): Call<ResponseBody>

    //Cancel the Booking Api
    @PATCH("api/v1/booking")
    fun cancelBookedRoom(
        @Query("meetId") meetId: Int?
    ): Call<ResponseBody>

    //Canccel the Recurring Booking
    @PATCH("api/v1/booking")
    fun cancelRecurringBooking(
        @Query("meetId") meetId: Int?,
        @Query("recurringMeetId") recurringMeetId: String
    ): Call<ResponseBody>


    //--------------------------Building Api's--------------------------------------------------------------------------
    /**
     * Buildings Functionality services
     */
    //Get List of Building Api
    @GET("api/v1/building")
    fun getBuildingList(
    ): Call<List<Building>>

    //Add Building Api
    @POST("api/v1/building")
    fun addBuilding(
        @Body newBuilding: AddBuilding
    ): Call<ResponseBody>

    //Update Building Api
    @PUT("api/v1/building")
    fun updateBuilding(
        @Body newBuilding: AddBuilding
    ): Call<ResponseBody>

    //Delete Building Api
    @DELETE("api/v1/building")
    fun deleteBuilding(
        @Query("buildingId") id: Int
    ): Call<ResponseBody>


    //--------------------------Conference Rooms Api's------------------------------------------------------------------
    /**
     * Conference Rooms services
     */

    //Add Conference room Api
    @POST("api/v1/room")
    fun addConference(
        @Body newConferenceRoom: AddConferenceRoom
    ): Call<ResponseBody>

    //Update Conference room Api
    @PUT("api/v1/room")
    fun updateConference(
        @Body newConferenceRoom: AddConferenceRoom
    ): Call<ResponseBody>

    //Delete Conference room Api
    @DELETE("api/v1/room")
    fun deleteRoom(
        @Query("roomId") id: Int
    ): Call<ResponseBody>

    //Get Conference Based on BuildingId Api
    @GET("api/v1/room/{buildingId}")
    fun conferenceList(
        @Path("buildingId") id: Int
    ): Call<List<ConferenceList>>


    //---------------------------DashBoard of Booking Rooms Api's------------------------------------------------------
    /**
     * Booking DashBoard Services which includes Previous,Cancelled,Upcoming Bookings
     */
    //Get Dashboard of Bookings
    @POST("api/v1/dashboard")
    fun getDashboard(
        @Body bookingDashboardInput: BookingDashboardInput
    ): Call<DashboardDetails>


    //------------------------List of Locations Api---------------------------------------------------------------------
    /**
     * List of All the Locations Servics
     */
    //Get List of All the Location
    @GET("api/v1/location")
    fun getAllLocation(

    ): Call<List<Location>>

    //-------------------------- Login Api's----------------------------------------------------------------------------
    /**
     *  Login Services
     */
    // Login Api
    @GET("api/v1/login")
    fun getRequestCode(
        @Query("deviceId") deviceId: String
    ): Call<SignIn>

    //validate ResfreshToken Api
    @POST("api/v1/validateRefreshToken")
    fun getNewToken(
        @Body mRefreshToken: RefreshToken
    ): Call<RefreshToken>

    // Login Using JWT Token Api
    @GET("api/v1/checkEmployeeRole")
    fun getRole(
        @Query("emailId") emailId: String
    ): Call<Int>

    //Get Passcode Api
    @GET("api/v1/passCode")
    fun getPasscode(
        @Query("GenerateNewPasscode") generateNewPasscode: Boolean,
        @Query("emailId") emailId: String
    ): Call<String>

    //--------------------------Recurring Booking Api's-----------------------------------------------------------------
    /**
     * Recurring Booking Services
     */
    //List of rooms available for Recurring Meeting Api's
    @POST("api/v1/roomsForRecurring")
    fun getConferenceRoomListForRecurring(
        @Body availableRoom: ManagerConference
    ): Call<List<RoomDetails>>

    //Book Rooms For Recurrring Meeting Api's
    @POST("api/v1/recurringMeeting")
    fun addManagerBookingDetails(
        @Body managerBooking: ManagerBooking
    ): Call<ResponseBody>

    //----------------------------List of Employee Api------------------------------------------------------------------
    /**
     * List of all the Employee
     */
    @GET("api/v1/employee")
    fun getEmployees(
        @Query("emailId") emailId: String
    ): Call<List<EmployeeList>>
}