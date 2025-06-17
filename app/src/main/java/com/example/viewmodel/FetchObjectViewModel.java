package com.example.viewmodel;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.fetchapp.model.FetchObject;
import com.example.fetchapp.repository.FetchObjectRepository;
import com.example.fetchapp.repository.RepositoryCallback;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

/**
 * ViewModel for managing FetchObject data and UI state in the MVVM
 * architecture.
 *
 * Responsibilities:
 * - Acts as a bridge between the Repository (Model layer) and the UI (View
 * layer)
 * - Exposes LiveData for the list of FetchObjects, error state, and loading
 * state
 * - Delegates all data operations and business logic to the
 * FetchObjectRepository
 * - Handles lifecycle awareness for data and state
 *
 * Usage in MVVM:
 * - Observed by Activities/Fragments to update the UI reactively
 * - Calls repository methods to fetch and filter data
 * - Keeps UI logic out of the repository and model classes
 *
 * Dependency Injection:
 * - Uses Hilt to inject the FetchObjectRepository
 * - Application context is passed for AndroidViewModel base class
 */
@HiltViewModel
public class FetchObjectViewModel extends AndroidViewModel {
    private static final String TAG = "FetchObjectViewModel";
    private static final long FETCH_TIMEOUT_MS = 30000; // 30 seconds total timeout

    private final MutableLiveData<List<FetchObject>> fetchObjects = new MutableLiveData<>();
    private final MutableLiveData<String> errorState = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> retryState = new MutableLiveData<>();
    private final FetchObjectRepository repository;
    private final Handler timeoutHandler = new Handler(Looper.getMainLooper());

    private boolean hasDataBeenLoaded = false;
    private Runnable timeoutRunnable;
    private boolean isFetchInProgress = false;

    /**
     * Constructs a FetchObjectViewModel with injected repository and application
     * context.
     *
     * @param application The application instance for AndroidViewModel
     * @param repository  The repository for data operations and business logic
     */
    @Inject
    public FetchObjectViewModel(@NonNull Application application, FetchObjectRepository repository) {
        super(application);
        this.repository = repository;
    }

    /**
     * Returns LiveData containing the list of FetchObjects for the UI to observe.
     *
     * @return LiveData of filtered FetchObjects
     */
    public LiveData<List<FetchObject>> getAllFetchObjects() {
        return fetchObjects;
    }

    /**
     * Returns LiveData containing error state information for the UI to observe.
     *
     * @return LiveData of error messages
     */
    public LiveData<String> getErrorState() {
        return errorState;
    }

    /**
     * Returns LiveData containing loading state information for the UI to observe.
     *
     * @return LiveData of loading state (true if loading, false otherwise)
     */
    public LiveData<Boolean> getLoading() {
        return loading;
    }

    /**
     * Returns LiveData containing retry state information for the UI to observe.
     * This provides feedback about retry attempts in progress.
     *
     * @return LiveData of retry messages (null when not retrying)
     */
    public LiveData<String> getRetryState() {
        return retryState;
    }

    /**
     * Initiates fetching of FetchObjects from the repository and updates LiveData.
     * Handles loading and error state transitions with timeout protection.
     */
    public void fetchData() {
        // Prevent multiple concurrent requests
        if (isFetchInProgress) {
            Log.d(TAG, "Fetch already in progress, ignoring duplicate request");
            return;
        }

        isFetchInProgress = true;
        loading.postValue(true);
        errorState.postValue(null);
        retryState.postValue(null);

        // Set up timeout protection
        setupFetchTimeout();

        repository.fetchObjects(new RepositoryCallback<List<FetchObject>>() {
            @Override
            public void onSuccess(List<FetchObject> data) {
                clearTimeout();
                isFetchInProgress = false;
                loading.postValue(false);
                retryState.postValue(null);
                fetchObjects.postValue(data);
                hasDataBeenLoaded = true;
                Log.d(TAG, "Fetch completed successfully with " + (data != null ? data.size() : 0) + " items");
            }

            @Override
            public void onError(String errorMsg) {
                clearTimeout();
                isFetchInProgress = false;
                loading.postValue(false);
                retryState.postValue(null);
                errorState.postValue(errorMsg);
                fetchObjects.postValue(null);
                Log.d(TAG, "Fetch completed with error: " + errorMsg);
            }

            @Override
            public void onRetrying(String retryMsg) {
                // Keep loading state true during retries but reset timeout
                resetTimeout();
                retryState.postValue(retryMsg);
                Log.d(TAG, "Fetch retry: " + retryMsg);
            }
        });
    }

    /**
     * Sets up a timeout to prevent infinite loading states.
     */
    private void setupFetchTimeout() {
        timeoutRunnable = () -> {
            if (isFetchInProgress) {
                Log.w(TAG, "Fetch operation timed out after " + FETCH_TIMEOUT_MS + "ms");
                isFetchInProgress = false;
                loading.postValue(false);
                retryState.postValue(null);
                errorState.postValue("Request timed out. Please try again.");
            }
        };
        timeoutHandler.postDelayed(timeoutRunnable, FETCH_TIMEOUT_MS);
    }

    /**
     * Resets the timeout for retry operations.
     */
    private void resetTimeout() {
        clearTimeout();
        setupFetchTimeout();
    }

    /**
     * Clears the timeout handler.
     */
    private void clearTimeout() {
        if (timeoutRunnable != null) {
            timeoutHandler.removeCallbacks(timeoutRunnable);
            timeoutRunnable = null;
        }
    }

    /**
     * Cancels any ongoing fetch operation and resets state.
     */
    public void cancelFetch() {
        Log.d(TAG, "Cancelling fetch operation");
        clearTimeout();
        isFetchInProgress = false;
        loading.postValue(false);
        retryState.postValue(null);
    }

    /**
     * Fetches data only if it hasn't been loaded before.
     * Use this for initial loading to avoid unnecessary network calls.
     */
    public void fetchDataIfNeeded() {
        if (!hasDataBeenLoaded && !isFetchInProgress) {
            fetchData();
        }
    }

    /**
     * Forces a refresh of data regardless of current state.
     * Use this for user-initiated refreshes.
     */
    public void refreshData() {
        fetchData();
    }

    /**
     * Resets the ViewModel state completely.
     * Useful for debugging or when you suspect state corruption.
     */
    public void resetState() {
        Log.d(TAG, "Resetting ViewModel state");
        cancelFetch();
        hasDataBeenLoaded = false;
        fetchObjects.postValue(null);
        errorState.postValue(null);
        retryState.postValue(null);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        clearTimeout();
        Log.d(TAG, "ViewModel cleared");
    }
}
