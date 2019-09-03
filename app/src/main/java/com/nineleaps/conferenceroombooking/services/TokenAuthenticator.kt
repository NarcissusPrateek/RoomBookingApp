package com.nineleaps.conferenceroombooking.services

import android.content.Context
import android.util.Log
import com.nineleaps.conferenceroombooking.BaseApplication
import com.nineleaps.conferenceroombooking.R
import com.nineleaps.conferenceroombooking.model.RefreshToken
import com.nineleaps.conferenceroombooking.utils.Constants
import com.nineleaps.conferenceroombooking.utils.GetPreference
import com.orhanobut.hawk.Hawk
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import java.io.IOException


class TokenAuthenticator : Authenticator {
    @Throws(IOException::class)
    override fun authenticate(route: Route?, response: Response): Request? {
        val mContext = BaseApplication.appContext
        Hawk.init(mContext).build()
        val mToken = RefreshToken(
            Hawk.get<String>(mContext!!.getString(R.string.token)),
            Hawk.get<String>(Constants.REFRESH_TOKEN)

        )
        val retrofitResponse = RestClient.getWebServiceData()?.getNewToken(mToken)?.execute()
        if (retrofitResponse != null && retrofitResponse.code() == 200) {
            GetPreference.setJWTToken(
                mContext,
                retrofitResponse.body()?.refreshToken!!,
                retrofitResponse.body()?.jwtToken!!
            )
            return response.request().newBuilder()
                .header(mContext.getString(R.string.authorization), "Bearer ${retrofitResponse.body()?.jwtToken!!}")
                .build()
        }
        return null
    }
}
