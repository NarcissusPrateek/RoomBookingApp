package com.nineleaps.conferenceroombooking.Helper

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

class GoogleGSO {
    companion object {
        /**
         * this function configure GoogleSignInOption and returns a object of GoogleSignIn
         */
        fun getGoogleSignInClient(context: Context): GoogleSignInClient {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
            return GoogleSignIn.getClient(context, gso)
        }
    }
}