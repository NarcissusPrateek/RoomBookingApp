package com.nineleaps.conferenceroombooking.utils

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.chivorn.smartmaterialspinner.util.SoftKeyboardUtil.hideSoftKeyboard
import android.view.MotionEvent
import android.view.View.OnTouchListener
import android.view.ViewGroup


@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class HideSoftKeyboard {

    companion object {
        fun hideKeyboard(activity: Activity) {
            val inputMethodManager: InputMethodManager =
                activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            if (activity.currentFocus != null)
                inputMethodManager.hideSoftInputFromWindow(activity.currentFocus.applicationWindowToken, 0)

        }

        fun setUpUI(view:View,activity: Activity){
            if(view !is EditText){
                view.setOnTouchListener { v, event ->
                    hideSoftKeyboard(activity)
                    false
                }
            }
        }

        fun childUI(view: View,activity: Activity){
            var iCount = 0
            val count = (view as ViewGroup).childCount
            while (iCount!=count){
                val innerView = view.getChildAt(iCount)
                setUpUI(innerView,activity)
                iCount++
            }
        }

    }
}