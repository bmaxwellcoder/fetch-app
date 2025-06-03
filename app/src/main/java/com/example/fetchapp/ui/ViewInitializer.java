package com.example.fetchapp.ui;

/**
 * Interface for handling data refresh requests in the UI layer.
 *
 * Responsibilities:
 * - Provides a callback mechanism for UI components to request data refresh
 * - Enables communication between UI and ViewModel layers
 *
 * Usage in MVVM:
 * - Implemented by activities/fragments that need to trigger data refresh
 * - Called by UI components (e.g., retry buttons, pull-to-refresh)
 * - Delegates refresh requests to the ViewModel layer
 */
public class ViewInitializer {
    public interface DataRefreshListener {
        /**
         * Called when a UI component requests a data refresh.
         * The implementing class should trigger a refresh of its data source.
         */
        void onRefreshRequested();
    }
}