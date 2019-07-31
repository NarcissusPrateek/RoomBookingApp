package com.nineleaps.conferenceroombooking

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.example.conferenceroomapp.common.di.AppModule
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.nineleaps.conferenceroombooking.common.di.AppComponent
import com.nineleaps.conferenceroombooking.common.di.DaggerAppComponent
import com.nineleaps.conferenceroombooking.utils.Constants


class BaseApplication: Application(){
    private var mAppComponent: AppComponent? = null

    private val appModule: AppModule
        get() = AppModule(this)

    override fun onCreate() {
        super.onCreate()
        init()
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    return@OnCompleteListener
                }
                // Get new Instance ID token
                getSharedPreferences(Constants.PREFERENCE, Context.MODE_PRIVATE).edit().putString(
                    Constants.DEVICE_ID, task.result!!.token).apply()
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