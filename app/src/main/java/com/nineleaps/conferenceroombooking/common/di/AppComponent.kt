package com.nineleaps.conferenceroombooking.common.di


import com.example.conferenceroomapp.common.di.AppModule
import com.nineleaps.conferenceroombooking.manageConferenceRoom.ui.ConferenceDashBoard
import com.nineleaps.conferenceroombooking.signIn.ui.SignIn
import com.nineleaps.conferenceroombooking.addBuilding.ui.AddingBuilding
import com.nineleaps.conferenceroombooking.addConferenceRoom.ui.AddingConference
import com.nineleaps.conferenceroombooking.blockDashboard.ui.BlockedDashboard
import com.nineleaps.conferenceroombooking.blockRoom.ui.BlockConferenceRoomActivity
import com.nineleaps.conferenceroombooking.booking.ui.InputDetailsForBookingFragment
import com.nineleaps.conferenceroombooking.booking.ui.SelectMeetingMembersActivity
import com.nineleaps.conferenceroombooking.bookingDashboard.ui.UserBookingsDashboardActivity
import com.nineleaps.conferenceroombooking.checkConnection.NoInternetConnectionActivity
import com.nineleaps.conferenceroombooking.manageBuildings.ui.BuildingDashboard
import com.nineleaps.conferenceroombooking.recurringMeeting.ui.*
import com.nineleaps.conferenceroombooking.splashScreen.ui.SplashScreen
import com.nineleaps.conferenceroombooking.updateBooking.ui.UpdateBookingActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {

    /**
     * activity dagger injection object
     */
    fun inject(splashScreen: SplashScreen)

    fun inject(addBuilding: AddingBuilding)

    fun inject(addingConference: AddingConference)

    fun inject(buildingDashboard: BuildingDashboard)

    fun inject(blockConferenceRoomActivity: BlockConferenceRoomActivity)

    fun inject(blockedDashboard: BlockedDashboard)

    fun inject(conferenceDashBoard: ConferenceDashBoard)

    fun inject(managerBooking: ManagerSelectMeetingMembers)

    fun inject(managerBookDetails: RecurringBookingInputDetails)

    fun inject(noInternetConnectionActivity: NoInternetConnectionActivity)

    fun inject(selectMeetingMembersActivity: SelectMeetingMembersActivity)

    fun inject(signIn: SignIn)

    fun inject(updateBookingActivity: UpdateBookingActivity)

    fun inject(userBookingsDashboardActivity: UserBookingsDashboardActivity)
    /**
     * fragment dagger injection object
     */

    fun inject(cancelledBookingFragment: CancelledBookingFragment)

    fun inject(inputDetailsForBookingFragment: InputDetailsForBookingFragment)

    fun inject(previousBookingFragment: PreviousBookingFragment)

    fun inject(upcomingBookingFragment: UpcomingBookingFragment)


}