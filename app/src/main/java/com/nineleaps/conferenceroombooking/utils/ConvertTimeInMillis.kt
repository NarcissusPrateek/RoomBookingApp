package com.nineleaps.conferenceroombooking.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat

class ConvertTimeInMillis {
    /**
     * it will provide a static function
     */
    companion object {

        /**
         * convert the time into milliseconds and return the difference between two time
         */
        @SuppressLint("SimpleDateFormat")
        fun calculateTimeInMilliseconds(startTime: String, endTime: String, date: String): Pair<Long, Long> {
            val simpleDateFormatForTime = SimpleDateFormat("HH:mm")
            val simpleDateFormatForDate = SimpleDateFormat("yyyy-M-dd HH:mm")
            val startTimeInDateObject = simpleDateFormatForTime.parse(startTime)
            val endTimeInDateObject = simpleDateFormatForTime.parse(endTime)
            val startTimeAndDateTimeInDateObject = simpleDateFormatForDate.parse("$date $startTime")
            val differenceOfStartAndEndTimeInMillis = endTimeInDateObject.time - startTimeInDateObject.time
            val currTime = System.currentTimeMillis()
            val differenceOfStartTimeAndCurrentTImeInMillis = startTimeAndDateTimeInDateObject.time - currTime
            return Pair(differenceOfStartAndEndTimeInMillis, differenceOfStartTimeAndCurrentTImeInMillis)
        }
        @SuppressLint("SimpleDateFormat")
        fun calculateDateInMilliseconds(fromDate: String, toDate: String): Boolean {
            val simpleDateFormatForDate = SimpleDateFormat("yyyy-M-dd")
            val fromDateInDateFormat = simpleDateFormatForDate.parse(fromDate)
            val toDateInDateFormat = simpleDateFormatForDate.parse(toDate)
            val dateDifference = toDateInDateFormat.time - fromDateInDateFormat.time
            return dateDifference > 0
        }
    }
}