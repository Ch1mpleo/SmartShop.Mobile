package com.example.smartshopmobile

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import org.osmdroid.config.Configuration

@HiltAndroidApp
class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize osmdroid configuration
        Configuration.getInstance().userAgentValue = packageName
    }
}