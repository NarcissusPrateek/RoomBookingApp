package com.nineleaps.conferenceroombooking.utils

import java.text.SimpleDateFormat

class FormatDate {
    companion object {
        val simpleDateFormatInput = SimpleDateFormat("yyyy-MM-dd")
        val simpleDateFormatOutput = SimpleDateFormat("dd MMMM yyyy")

        /**
         * format date from yyyy-MM-dd to dd-MM-yyyy
         */
        fun formatDate(date: String): String {
            return simpleDateFormatOutput.format(simpleDateFormatInput.parse(date))
        }

        /**
         * format date from dd-MM-yyyy to yyyy-MM-dd
         */
        fun reverseDateFormat(date: String): String {
            return simpleDateFormatInput.format(simpleDateFormatOutput.parse(date))
        }

        fun changeFormat(time: String): String {
            var simpleDateFormat = SimpleDateFormat("HH:mm:ss")
            var simpleDateFormat1 = SimpleDateFormat("HH:mm")
            return simpleDateFormat1.format(simpleDateFormat.parse(time))
        }

        fun changeFormateFromDateTimeToTime(dateTime: String): String {
            var simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
            var simpleTimeFormat = SimpleDateFormat("HH:mm")
            return simpleTimeFormat.format(simpleDateFormat.parse(dateTime))
        }
    }
}