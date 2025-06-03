package com.example.fetchapp;

import android.app.Application;
import dagger.hilt.android.HiltAndroidApp;

/**
 * Application class for the Fetch Rewards Exercise app.
 *
 * Responsibilities:
 * - Initializes Hilt for dependency injection
 * - Serves as the application entry point
 * - Manages application-wide resources and configurations
 *
 * Usage in MVVM:
 * - Provides a centralized place for app-wide setup
 * - Supports dependency injection via Hilt
 * - Ensures consistent app behavior across components
 */
@HiltAndroidApp
public class FetchApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
    }
}