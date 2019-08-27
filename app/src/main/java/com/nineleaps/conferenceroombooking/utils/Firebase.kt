package com.nineleaps.conferenceroombooking.utils

import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.iid.FirebaseInstanceId
import com.orhanobut.hawk.Hawk

class Firebase{
    companion object{
        fun FirebaseDeviceId(){
            FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    return@OnCompleteListener
                }
                Log.i("FirebaseDevice",task.result!!.token)

            })
        }

        fun FirebaseToken(){
            FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener(OnSuccessListener {
                Log.i("FirebaseToken",it.token)
            })
        }
    }
}