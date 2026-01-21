package com.barbermanagerpro.app

import android.app.Application
import com.barbermanagerpro.BuildConfig
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class BarberManagerApp : Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(
                object : Timber.DebugTree() {
                    override fun createStackElementTag(e: StackTraceElement): String =
                        "BarberLog: (${e.fileName}:${e.lineNumber}) #${e.methodName}"
                },
            )
        }
    }
}
