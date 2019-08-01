package com.nineleaps.conferenceroombooking.utils

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager

@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class HideSoftKeyboard {

    companion object{
           fun hideKeyboard(activity: Activity){
            val inputMethodManager:InputMethodManager = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
               if (activity.currentFocus!=null)
                    inputMethodManager.hideSoftInputFromWindow(activity.currentFocus.applicationWindowToken,0)

        }
    }
}