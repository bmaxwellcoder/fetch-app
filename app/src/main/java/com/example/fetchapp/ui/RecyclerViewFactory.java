package com.example.fetchapp.ui;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Utility class for configuring RecyclerViews.
 *
 * Responsibilities:
 * - Provides common RecyclerView configuration
 * - Handles layout manager setup
 *
 * Usage in MVVM:
 * - Used by the UI layer to configure RecyclerViews
 * - Supports consistent RecyclerView behavior across the app
 */
public class RecyclerViewFactory {

    /**
     * Configures an existing RecyclerView with standard settings.
     *
     * @param recyclerView The RecyclerView to configure
     * @param adapter      The adapter to use with the RecyclerView
     */
    public static void configureRecyclerView(RecyclerView recyclerView, RecyclerView.Adapter<?> adapter) {
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setHasFixedSize(true);
    }
}