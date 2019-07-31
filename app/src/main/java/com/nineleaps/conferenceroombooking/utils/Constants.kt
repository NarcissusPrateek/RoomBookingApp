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

        /**
         * ip address for api call
         */
        var IP_ADDRESS = "http://192.168.3.188/CRB/"


        const val FLAG = "FLAG"
        const val DATE_FORMAAT_Y_D_M = "yyyy-MM-dd"

        const val SELECT_BUILDING = "Select Building"

        const val IS_PURPOSE_VISIBLE = "Purpose Visibility"

        const val SELECT_FLOOR = "Select Floor"
        const val MIN_MEETING_DURATION: Long = 900000

        const val SOME_EXCEPTION = 400

        const val Facility_Manager = 13

        const val HR_CODE = 11

        const val MANAGER_CODE = 12

        const val EMPLOYEE_CODE = 10

        const val PAGE_SIZE = 7

        const val BOOKING_DASHBOARD_TYPE_UPCOMING = "upcoming"

        const val BOOKING_DASHBOARD_TYPE_PREVIOUS = "previous"

        const val BOOKING_DASHBOARD_TYPE_CANCELLED = "cancelled"

        const val BOOKING_DASHBOARD_PENDING = "Pending"

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

        const val RES_CODE = 200

        const val RES_CODE2 = 201

        const val RES_CODE3 = 202

        const val RES_CODE4 = 203

        const val PREFERENCE = "PREFERENCE"

        const val TOKEN = "TOKEN"

        const val DEVICE_ID = "DEVICE ID"

        const val DEFAULT_INT_PREFERENCE_VALUE = -1

        const val ROLE_CODE = "Code"

        const val PROJECTOR = "Projector"

        const val MONITOR = "Monitor"

        const val WHITEBOARD_MARKER = "WhiteBoard-Marker"

        const val SPEAKER = "Speaker"

        const val EXTENSION_BOARD = "Extension Board"

        const val BUILDING_ID = "buildingId"

        const val BUILDING_NAME = "buildingName"

        const val BUILDING_PLACE = "buildingPlace"
    }
}