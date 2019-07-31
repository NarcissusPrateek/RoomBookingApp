package com.nineleaps.conferenceroombooking.utils

import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.widget.NumberPicker
import android.widget.TimePicker

import java.util.ArrayList

class CustomTimePicker(
    context: Context, private val mTimeSetListener: TimePickerDialog.OnTimeSetListener?,
    hourOfDay: Int, minute: Int, is24HourView: Boolean
) : TimePickerDialog(
    context,
    TimePickerDialog.THEME_HOLO_LIGHT,
    null,
    hourOfDay,
    minute / TIME_PICKER_INTERVAL,
    is24HourView
) {
    private var mTimePicker: TimePicker? = null

    override fun updateTime(hourOfDay: Int, minuteOfHour: Int) {
        mTimePicker!!.currentHour = hourOfDay
        mTimePicker!!.currentMinute = minuteOfHour / TIME_PICKER_INTERVAL
    }

    override fun onClick(dialog: DialogInterface, which: Int) {
        when (which) {
            DialogInterface.BUTTON_POSITIVE -> mTimeSetListener?.onTimeSet(
                mTimePicker, mTimePicker!!.currentHour,
                mTimePicker!!.currentMinute * TIME_PICKER_INTERVAL
            )
            DialogInterface.BUTTON_NEGATIVE -> cancel()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        try {
            val classForid = Class.forName("com.android.internal.R\$id")
            val timePickerField = classForid.getField("timePicker")
            mTimePicker = findViewById(timePickerField.getInt(null))
            val field = classForid.getField("minute")

            val minuteSpinner = mTimePicker!!
                .findViewById<NumberPicker>(field.getInt(null))
            minuteSpinner.minValue = 0
            minuteSpinner.maxValue = 60 / TIME_PICKER_INTERVAL - 1
            val displayedValues = ArrayList<String>()
            var i = 0
            while (i < 60) {
                displayedValues.add(String.format("%02d", i))
                i += TIME_PICKER_INTERVAL
            }
            minuteSpinner.displayedValues = displayedValues
                .toTypedArray()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    companion object {

        private val TIME_PICKER_INTERVAL = 15
    }
}