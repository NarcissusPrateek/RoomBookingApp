package com.nineleaps.conferenceroombooking.utils

import android.content.Context
import com.nineleaps.conferenceroombooking.R

class GetPreference {
    companion object {

        /**
         * get token and userId from local storage
         */
        fun getTokenFromPreference(mContext: Context): String {
            return mContext.getSharedPreferences(Constants.PREFERENCE, Context.MODE_PRIVATE).getString(mContext.getString(R.string.token), mContext.getString(
                R.string.not_set))!!
        }

        fun getDeviceIdFromPreference(mContext: Context): String {
            return mContext.getSharedPreferences(Constants.PREFERENCE, Context.MODE_PRIVATE).getString(
                Constants.DEVICE_ID, mContext.getString(R.string.not_set))!!
        }

        fun getRoleIdFromPreference(mContext: Context): Int {
            return mContext.getSharedPreferences(Constants.PREFERENCE, Context.MODE_PRIVATE).getInt(Constants.ROLE_CODE,Constants.DEFAULT_INT_PREFERENCE_VALUE)
        }
    }
}