package com.nineleaps.conferenceroombooking.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.nineleaps.conferenceroombooking.Helper.GoogleGSO
import com.nineleaps.conferenceroombooking.R
import com.nineleaps.conferenceroombooking.SignIn
import com.orhanobut.hawk.Hawk

class ShowDialogForSessionExpired {
    companion object {
        /**
         * show dialog when session expired
         */
        fun showAlert(mConext: Context, activity: Activity) {
            val dialog = GetAleretDialog.getDialog(
                mConext,
                mConext.getString(R.string.session_expired), "Your session is expired!\n" +
                        mConext.getString(R.string.session_expired_messgae)
            )
            dialog.setPositiveButton(R.string.ok) { _, _ ->
                    signOut(mConext, activity)
            }
            val builder = GetAleretDialog.showDialog(dialog)
            ColorOfDialogButton.setColorOfDialogButton(builder)
        }

        /**
         * sign out from application
         */
        fun signOut(mContext: Context, activity: Activity) {
            Hawk.init(mContext).build()
           val mGoogleSignInClient = GoogleGSO.getGoogleSignInClient(mContext)
            mGoogleSignInClient.signOut()
                .addOnCompleteListener(activity) {
                    mContext.startActivity(Intent(mContext, SignIn::class.java))
                    (mContext as Activity).finish()
                }
            Hawk.deleteAll()
        }
    }
}