package com.nineleaps.conferenceroombooking.utils

import android.content.Context
import com.nineleaps.conferenceroombooking.R
import com.orhanobut.hawk.Hawk

class GetPreference {
    companion object {

        /**
         * get token and userId from local storage
         */
        fun getTokenFromPreference(mContext: Context): String {
             if(Hawk.get<String>(mContext.getString(R.string.token))!=null)
                 return Hawk.get<String>(mContext.getString(R.string.token))
            else
                 return "Not set"
        }

        fun getRoleIdFromPreference(): Int {
            return Hawk.get<Int>(Constants.ROLE_CODE)
        }


        fun setJWTToken(mContext: Context, mRefreshToken: String, mJWTToken: String) {
            Hawk.put(Constants.REFRESH_TOKEN,mRefreshToken)
            Hawk.put(mContext.getString(R.string.token),"Bearer $mJWTToken")
        }
    }
}