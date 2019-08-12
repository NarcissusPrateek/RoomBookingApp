package com.nineleaps.conferenceroombooking.utils

import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics
import com.nineleaps.conferenceroombooking.R

class FirebaseAnalytic {
    companion object {
        fun firebaseAnalytics(
            mFirebaseAnalytics: FirebaseAnalytics,
            mContext: Context,
            event: String,
            email: String
        ) {
            mFirebaseAnalytics.logEvent(event,null)
            mFirebaseAnalytics.setAnalyticsCollectionEnabled(true)
            mFirebaseAnalytics.setSessionTimeoutDuration(1000000)
            mFirebaseAnalytics.setUserId(email)
            mFirebaseAnalytics.setUserProperty(mContext.getString(
                R.string.Roll_Id),GetPreference.getRoleIdFromPreference().toString())
        }

        fun firebaseAnalyticsReset(mFirebaseAnalytics: FirebaseAnalytics){
            mFirebaseAnalytics.resetAnalyticsData()
        }
    }
}