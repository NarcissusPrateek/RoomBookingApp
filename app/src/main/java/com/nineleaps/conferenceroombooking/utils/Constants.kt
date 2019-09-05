package com.nineleaps.conferenceroombooking.utils

class Constants {
    /**
     * it will provides some static final constants
     */
    companion object {


        /**
         * to check the status of user whether registered or not
         */
        const val EXTRA_REGISTERED = "com.example.conferencerommapp.Activity.EXTRA_REGISTERED"

        /**
         * for set and get intent data
         */
        const val EXTRA_INTENT_DATA = "com.example.conferencerommapp.Activity.EXTRA_INTENT_DATA"

        /**
         * response code for response IsSuccessfull
         */
        const val OK_RESPONSE = 200

        /**
         * building id Name for intents
         */
        const val EXTRA_BUILDING_ID = "com.example.conferencerommapp.Activity.EXTRA_BUILDING_ID"


        const val FLAG = "FLAG"
        const val DATE_FORMAAT_Y_D_M = "yyyy-MM-dd"

        const val MIN_MEETING_DURATION: Long = 900000

        const val SOME_EXCEPTION = 400

        const val FORBIDDEN = 403

        const val UNPROCESSABLE = 422

        const val REFRESH_TOKEN = "REFRESH_TOKEN"

        const val Facility_Manager = 13

        const val HR_CODE = 11

        const val MANAGER_CODE = 12

        const val EMPLOYEE_CODE = 10

        const val PAGE_SIZE = 7

        const val BOOKING_DASHBOARD_TYPE_UPCOMING = "Upcoming"

        const val BOOKING_DASHBOARD_TYPE_PREVIOUS = "Previous"

        const val BOOKING_DASHBOARD_TYPE_CANCELLED = "Cancelled"

        const val INVALID_TOKEN = 401

        const val NOT_ACCEPTABLE = 406

        const val NOT_MODIFIED = 304

        const val NO_CONTENT_FOUND = 204

        const val NOT_FOUND = 400

        const val  POOR_INTERNET_CONNECTION = 505

        const val SUCCESSFULLY_CREATED = 201

        const val INTERNAL_SERVER_ERROR = 500

        const val MATCHER = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$"

        const val UNAVAILABLE_SLOT = 409

        const val BUILDING_PRESENT = 1

        const val RES_CODE = 200

        const val RES_CODE2 = 201

        const val ROLE_CODE = "Code"

        const val BUILDING_ID = "buildingId"

        const val BUILDING_NAME = "buildingName"

        const val ROOM_NAME = "buildingName"

        const val ROOM_ID = "roomId"

        const val BUILDING_PLACE = "buildingPlace"

        const val LOCATION_ID = "locationId"
    }
}