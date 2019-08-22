package com.nineleaps.conferenceroombooking

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.multidex.MultiDex
import com.example.conferenceroomapp.common.di.AppModule
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.InstanceIdResult
import com.nineleaps.conferenceroombooking.common.di.AppComponent
import com.nineleaps.conferenceroombooking.common.di.DaggerAppComponent
import com.nineleaps.conferenceroombooking.utils.Constants
import com.orhanobut.hawk.Hawk


class BaseApplication: Application(){

    companion object {
        var appContext: Context? = null
    }

    private var mAppComponent: AppComponent? = null

    private var token:String? = null
    private val appModule: AppModule
        get() = AppModule(this)

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
        init()
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    return@OnCompleteListener
                }
                // Get new Instance ID token
                Hawk.init(applicationContext).build()
                val token = task.result?.token
            })

    }




    fun getmAppComponent(): AppComponent? {
        return mAppComponent
    }

    @Suppress("DEPRECATION")
    private fun init() {
        mAppComponent = DaggerAppComponent.builder().appModule(appModule).build()
    }




    override fun attachBaseContext(context: Context) {
    super.attachBaseContext(context)
    MultiDex.install(this)
}

}