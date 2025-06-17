package com.example.fetchapp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.fetchapp.databinding.ActivityMainBinding;

import com.example.fetchapp.ui.RecyclerViewAdapter;
import com.example.fetchapp.ui.RecyclerViewFactory;
import com.example.fetchapp.ui.UIUtils;
import com.example.fetchapp.ui.WindowUtils;
import com.example.fetchapp.model.FetchObject;
import com.example.viewmodel.FetchObjectViewModel;
import dagger.hilt.android.AndroidEntryPoint;

import java.util.ArrayList;
import java.util.List;

import androidx.core.splashscreen.SplashScreen;

/**
 * Main activity of the application that displays a list of FetchObjects.
 *
 * Responsibilities:
 * - Acts as the user-facing entry point to the application
 * - Implements edge-to-edge display with proper insets handling
 * - Manages RecyclerView with expandable/collapsible groups
 * - Handles list selection via a colored spinner
 * - Manages error and empty state handling
 * - Integrates splash screen for app startup
 *
 * Usage in MVVM:
 * - Uses ViewModel for data management
 * - Observes LiveData for UI updates
 * - Serves as an Android entry point with Hilt injection (@AndroidEntryPoint)
 * - Maintains proper lifecycle management
 */
@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ActivityMainBinding binding;
    private RecyclerViewAdapter recyclerViewAdapter;
    private FetchObjectViewModel fetchObjectViewModel;

    /**
     * Initial lifecycle method called when the activity is first created.
     * Responsible for one-time initialization of the activity:
     * - Installs the splash screen for app startup experience
     * - Enables edge-to-edge display
     * - Initializes view binding
     * - Sets up UI components and observers
     * - Configures window insets
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set up retry button in error view to trigger data refresh
        binding.errorView.retryButton.setOnClickListener(v -> refreshData());

        setupViewModel();
        setupRecyclerView();
        setupRefreshButton();
        WindowUtils.setupEdgeToEdge(binding.main);
    }

    /**
     * Final call in the activity lifecycle when the activity is being destroyed.
     * This is the appropriate place to:
     * - Release all resources that might cause memory leaks
     * - Unregister listeners and observers
     * - Nullify references to allow garbage collection
     *
     * Called either when the activity is finishing (via finish() call or user
     * closing it),
     * or when the system temporarily destroys the activity due to configuration
     * changes.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Cancel any ongoing operations to prevent memory leaks
        if (fetchObjectViewModel != null) {
            fetchObjectViewModel.cancelFetch();
        }
        // Clear references to prevent memory leaks
        binding = null;
        recyclerViewAdapter = null;
        fetchObjectViewModel = null;
    }

    /**
     * Sets up the RecyclerView with its adapter and layout manager.
     * Configures the RecyclerView for optimal performance by:
     * 1. Setting fixed size for better performance
     * 2. Using LinearLayoutManager for vertical scrolling
     * 3. Configuring the adapter with proper view holder pattern
     */
    private void setupRecyclerView() {
        recyclerViewAdapter = new RecyclerViewAdapter();
        RecyclerViewFactory.configureRecyclerView(binding.recyclerView, recyclerViewAdapter);
    }

    /**
     * Sets up the ViewModel and observes its LiveData for updates.
     * Handles:
     * 1. Data updates and UI state transitions
     * 2. Error state management
     * 3. Initial data loading
     * 4. Loading state management
     */
    private void setupViewModel() {
        fetchObjectViewModel = new ViewModelProvider(this).get(FetchObjectViewModel.class);

        // Observe data changes
        fetchObjectViewModel.getAllFetchObjects().observe(this, items -> {
            if (recyclerViewAdapter != null && !isFinishing()) {
                recyclerViewAdapter.setData(items);
                updateSpinnerOptions(items);
                showData(items);
            }
        });

        // Observe error states
        fetchObjectViewModel.getErrorState().observe(this, error -> {
            if (error != null && !isFinishing()) {
                showError(error);
            }
        });

        // Observe loading state
        fetchObjectViewModel.getLoading().observe(this, isLoading -> {
            if (isLoading != null && !isFinishing()) {
                if (isLoading) {
                    showLoading();
                }
            }
        });

        // Observe retry state
        fetchObjectViewModel.getRetryState().observe(this, retryMessage -> {
            if (retryMessage != null && !isFinishing()) {
                showRetrying(retryMessage);
            }
        });

        // Load data initially if not already loaded
        fetchObjectViewModel.fetchDataIfNeeded();
    }

    /**
     * Shows the data view and updates the UI accordingly.
     *
     * @param items The list of items to display
     */
    private void showData(List<FetchObject> items) {
        if (items == null || items.isEmpty()) {
            showError("No lists to display.");
        } else {
            UIUtils.showData(binding.recyclerView, binding.loadingView.getRoot(), binding.errorView.getRoot());
        }
    }

    /**
     * Shows the error view with the specified error message.
     *
     * @param errorMessage The error message to display
     */
    private void showError(String errorMessage) {
        UIUtils.showError(binding.errorView.getRoot(), errorMessage, binding.recyclerView,
                binding.loadingView.getRoot());
    }

    /**
     * Shows the loading view.
     */
    private void showLoading() {
        UIUtils.showLoading(binding.loadingView.getRoot(), binding.recyclerView, binding.errorView.getRoot());
    }

    /**
     * Shows the loading view with retry message.
     */
    private void showRetrying(String retryMessage) {
        UIUtils.showRetrying(binding.loadingView.getRoot(), retryMessage, binding.recyclerView,
                binding.errorView.getRoot());
    }

    /**
     * Refreshes data from the ViewModel (user-initiated).
     */
    private void refreshData() {
        fetchObjectViewModel.refreshData();
    }

    /**
     * Updates the spinner options based on the provided items.
     *
     * @param items The list of items to display in the spinner
     */
    private void updateSpinnerOptions(List<FetchObject> items) {
        if (items == null || items.isEmpty()) {
            return;
        }
        UIUtils.setupSpinner(binding.listSelectorSpinner, items, new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    recyclerViewAdapter.expandOnly(-1);
                } else {
                    // Extract list ID from spinner item text
                    String selectedText = (String) parent.getItemAtPosition(position);
                    int listId = Integer.parseInt(selectedText.replace("List ID: ", ""));
                    recyclerViewAdapter.expandOnly(listId);
                    scrollToListId(listId);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    /**
     * Scrolls the RecyclerView to the specified list ID.
     *
     * @param listId The list ID to scroll to
     */
    private void scrollToListId(int listId) {
        int position = recyclerViewAdapter.getPositionForListId(listId);
        UIUtils.scrollToPosition(binding.recyclerView, position);
    }

    /**
     * Sets up the refresh button.
     */
    private void setupRefreshButton() {
        binding.refreshFab.setOnClickListener(v -> refreshData());
    }

    /**
     * Public method to reset the app state.
     * Can be called from external debugging tools or during testing.
     */
    public void resetAppState() {
        Log.d(TAG, "Resetting app state - alternative to cache invalidation");
        if (fetchObjectViewModel != null) {
            fetchObjectViewModel.resetState();
        }

        // Clear adapter data
        if (recyclerViewAdapter != null) {
            recyclerViewAdapter.setData(new ArrayList<>());
        }

        // Reset UI state
        showLoading();

        // Restart data loading after a brief delay
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (fetchObjectViewModel != null) {
                fetchObjectViewModel.fetchData();
            }
        }, 500);
    }
}