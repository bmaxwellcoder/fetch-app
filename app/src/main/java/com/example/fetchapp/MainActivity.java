package com.example.fetchrewardsexercisejava;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.fetchrewardsexercisejava.databinding.ActivityMainBinding;
import com.example.fetchrewardsexercisejava.ui.RecyclerViewAdapter;
import com.example.fetchrewardsexercisejava.ui.RecyclerViewFactory;
import com.example.fetchrewardsexercisejava.ui.UIUtils;
import com.example.fetchrewardsexercisejava.ui.ViewInitializer;
import com.example.fetchrewardsexercisejava.ui.WindowUtils;
import com.example.fetchrewardsexercisejava.model.FetchObject;
import com.example.fetchrewardsexercisejava.model.FetchObjectViewModel;
import dagger.hilt.android.AndroidEntryPoint;

import java.util.List;

import androidx.core.splashscreen.SplashScreen;

/**
 * Main activity of the application that displays a list of FetchObjects.
 *
 * Responsibilities:
 * - Implements edge-to-edge display with proper insets handling
 * - Manages RecyclerView with expandable/collapsible groups
 * - Handles list selection via a colored spinner
 * - Provides pull-to-refresh functionality
 * - Manages error and empty state handling
 * - Monitors network state
 *
 * Usage in MVVM:
 * - Uses ViewModel for data management
 * - Observes LiveData for UI updates
 * - Handles configuration changes
 * - Maintains proper lifecycle management
 */
@AndroidEntryPoint
public class MainActivity extends AppCompatActivity implements ViewInitializer.DataRefreshListener {
    private ActivityMainBinding binding;
    private RecyclerViewAdapter recyclerViewAdapter;
    private FetchObjectViewModel fetchObjectViewModel;
    private boolean isFirstLoad = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set up retry button in error view to trigger data refresh
        binding.errorView.retryButton.setOnClickListener(v -> onRefreshRequested());

        setupViewModel();
        setupRecyclerView();
        setupRefreshButton();
        WindowUtils.setupEdgeToEdge(binding.main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Only refresh data if this is the first load or if we're returning to the
        // activity
        if (fetchObjectViewModel != null && (isFirstLoad || !isFinishing())) {
            fetchData();
            isFirstLoad = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Clean up any ongoing operations
        binding.recyclerView.stopScroll();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
     * Fetches data from the ViewModel.
     */
    private void fetchData() {
        fetchObjectViewModel.fetchData();
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
        binding.refreshFab.setOnClickListener(v -> fetchData());
    }

    @Override
    public void onRefreshRequested() {
        fetchData();
    }
}