package com.nineleaps.conferenceroombooking.utils

import android.app.Activity
import android.view.WindowManager

class WindowManager {
    companion object {
        fun disableInteraction(context: Activity) {
            context.window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
        }
        fun enableInteraction(activity: Activity){
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
    }
}