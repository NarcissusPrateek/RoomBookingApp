package com.nineleaps.conferenceroombooking.utils

import android.app.Activity
import android.app.Application
import android.os.Bundle


class ForegroundCounter: Application.ActivityLifecycleCallbacks{
    lateinit var instance: ForegroundCounter
    var startCount: Int = 0

    override fun onActivityPaused(activity: Activity?) {
        /**
         * onActivity is Paused
         */
    }

    override fun onActivityResumed(activity: Activity?) {
        /**
         * onActivity is Resumed
         */
    }

    override fun onActivityStarted(activity: Activity?) {
        startCount++
    }

    override fun onActivityDestroyed(activity: Activity?) {
        /**
         * onActivity is Destroyed
         */
    }

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
        startCount++
    }

    override fun onActivityStopped(activity: Activity?) {
        startCount--
    }

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
        /**
         * onActivity is Created
         */
    }


    fun createAndInstallCallbacks(application: Application): ForegroundCounter {
        val checker = ForegroundCounter()
        application.registerActivityLifecycleCallbacks(checker)
        return checker
    }

    fun inForeground(): Boolean {
        return startCount != 0
    }

}