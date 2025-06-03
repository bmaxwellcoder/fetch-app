package com.example.fetchapp.ui;

import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fetchapp.R;
import com.example.fetchapp.model.FetchObject;

import java.util.List;

/**
 * Utility class for common UI operations across the app.
 *
 * Responsibilities:
 * - Manages UI state (loading, error, data states)
 * - Handles spinner setup and interactions
 * - Provides RecyclerView scrolling utilities
 * - Centralizes common UI operations
 *
 * Usage in MVVM:
 * - Used by the UI layer to manage view states and interactions
 * - Helps maintain separation of concerns by centralizing UI logic
 * - Supports consistent UI behavior across the app
 */
public class UIUtils {

    /**
     * Shows the data view and hides loading/error views.
     * 
     * @param recyclerView The RecyclerView to show
     * @param loadingView  The loading view to hide
     * @param errorView    The error view to hide
     */
    public static void showData(RecyclerView recyclerView, View loadingView, View errorView) {
        recyclerView.setVisibility(View.VISIBLE);
        loadingView.setVisibility(View.GONE);
        errorView.setVisibility(View.GONE);
    }

    /**
     * Shows the error view with the specified message.
     * 
     * @param errorView    The error view to show
     * @param errorMessage The error message to display
     * @param recyclerView The RecyclerView to hide
     * @param loadingView  The loading view to hide
     */
    public static void showError(View errorView, String errorMessage, RecyclerView recyclerView, View loadingView) {
        TextView errorTextView = errorView.findViewById(R.id.errorMessage);
        errorTextView.setText(errorMessage);

        recyclerView.setVisibility(View.GONE);
        loadingView.setVisibility(View.GONE);
        errorView.setVisibility(View.VISIBLE);
    }

    /**
     * Shows the loading view and hides other views.
     * 
     * @param loadingView  The loading view to show
     * @param recyclerView The RecyclerView to hide
     * @param errorView    The error view to hide
     */
    public static void showLoading(View loadingView, RecyclerView recyclerView, View errorView) {
        recyclerView.setVisibility(View.GONE);
        loadingView.setVisibility(View.VISIBLE);
        errorView.setVisibility(View.GONE);
    }

    /**
     * Scrolls the RecyclerView to the specified list ID.
     * 
     * @param recyclerView The RecyclerView to scroll
     * @param position     The position to scroll to
     */
    public static void scrollToPosition(RecyclerView recyclerView, int position) {
        if (position >= 0 && recyclerView != null) {
            LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            if (layoutManager != null) {
                layoutManager.scrollToPositionWithOffset(position, 0);
            }
        }
    }

    /**
     * Sets up the spinner with the provided items.
     * 
     * @param spinner                The spinner to set up
     * @param items                  The items to display in the spinner
     * @param onItemSelectedListener The listener for item selection
     */
    public static void setupSpinner(Spinner spinner, List<FetchObject> items,
            Spinner.OnItemSelectedListener onItemSelectedListener) {
        if (items == null || items.isEmpty()) {
            return;
        }
        ColoredSpinnerAdapter.setupSpinner(spinner.getContext(), spinner, items);
        spinner.setOnItemSelectedListener(onItemSelectedListener);
    }
}