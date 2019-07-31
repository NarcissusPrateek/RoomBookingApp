@file:Suppress("DEPRECATION")

package com.nineleaps.conferenceroombooking.utils

import android.app.ProgressDialog
import android.content.Context


class GetProgress {
    companion object {
        /**
         * it will set a progress Dialog within the provided context with a message
         * and return the object of ProgressDialog
         */
        fun getProgressDialog(msg: String, context: Context) : ProgressDialog {
            val progressDialog = ProgressDialog(context)
            progressDialog.setMessage(msg)
            progressDialog.setCancelable(false)
            return progressDialog
        }
    }
}