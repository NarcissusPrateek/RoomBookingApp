package com.nineleaps.conferenceroombooking.utils

import android.app.Activity
import android.view.inputmethod.InputMethodManager

@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class HideSoftKeyboard {
    companion object{
         fun hideSoftKeyboard(activity: Activity) {
            val inputMethodManager:InputMethodManager = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(activity.currentFocus.windowToken,0)
        }
    }
}