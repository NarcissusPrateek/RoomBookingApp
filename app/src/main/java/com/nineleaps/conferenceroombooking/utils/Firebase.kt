package com.nineleaps.conferenceroombooking.utils

import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId

class Firebase{
    companion object{
        fun FirebaseDeviceId(){
            FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    return@OnCompleteListener
                }


            })
        }
    }
}