package com.example.fetchapp.repository;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.fetchapp.model.FetchObject;
import com.example.fetchapp.network.FetchObjectRetrofitService;
import com.example.fetchapp.util.NetworkUtils;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository for fetching FetchObject data from the network.
 *
 * Responsibilities:
 * - Acts as the single source of truth for FetchObject data operations
 * - Handles network requests via the FetchObjectRetrofitService
 * - Implements intelligent retry logic with exponential backoff
 * - Applies business logic (e.g., filtering invalid items)
 * - Exposes results via a callback interface for ViewModels
 * - Checks network availability before making API calls
 *
 * Usage in MVVM:
 * - Injected into ViewModels using Hilt
 * - Keeps data and business logic out of the ViewModel and UI layers
 *
 * Provided as a singleton by Hilt (see @Singleton annotation).
 */
@Singleton
public class FetchObjectRepository {
    private static final String TAG = "FetchObjectRepository";

    // Retry configuration
    private static final int MAX_RETRY_ATTEMPTS = 3;
    private static final long INITIAL_RETRY_DELAY_MS = 1000; // 1 second
    private static final double BACKOFF_MULTIPLIER = 2.0;

    private final FetchObjectRetrofitService fetchObjectRetrofitService;
    private final Context context;
    private final Handler mainHandler;

    /**
     * Constructs the repository with the required API service and context.
     * 
     * @param fetchObjectRetrofitService the Retrofit service for network calls
     *                                   (provided by Hilt)
     * @param context                    the application context for network checks
     */
    @Inject
    public FetchObjectRepository(FetchObjectRetrofitService fetchObjectRetrofitService, Context context) {
        this.fetchObjectRetrofitService = fetchObjectRetrofitService;
        this.context = context;
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * Fetches the list of FetchObjects from the API, filtering out items with null,
     * empty, or "null" names. Includes automatic retry logic for transient
     * failures.
     *
     * @param callback callback to receive the filtered list or an error message
     */
    public void fetchObjects(RepositoryCallback<List<FetchObject>> callback) {
        fetchObjectsWithRetry(callback, 0);
    }

    /**
     * Internal method that handles the retry logic for fetching objects.
     *
     * @param callback      callback to receive the result
     * @param attemptNumber current attempt number (0-based)
     */
    private void fetchObjectsWithRetry(RepositoryCallback<List<FetchObject>> callback, int attemptNumber) {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            callback.onError("No network connection available");
            return;
        }

        Log.d(TAG, "Fetching objects, attempt " + (attemptNumber + 1) + "/" + MAX_RETRY_ATTEMPTS);

        fetchObjectRetrofitService.getFetchObjects().enqueue(new Callback<List<FetchObject>>() {
            @Override
            public void onResponse(@NonNull Call<List<FetchObject>> call,
                    @NonNull Response<List<FetchObject>> response) {
                if (response.isSuccessful()) {
                    List<FetchObject> responseBody = response.body();

                    // Handle null or empty response body
                    if (responseBody == null) {
                        Log.w(TAG, "Received null response body from API");
                        callback.onError("No data received from server. Please try again.");
                        return;
                    }

                    if (responseBody.isEmpty()) {
                        Log.w(TAG, "Received empty response body from API");
                        callback.onSuccess(new ArrayList<>()); // Return empty list instead of error
                        return;
                    }

                    // Success - filter and return data
                    List<FetchObject> filteredFetchObjects = filterValidObjects(responseBody);

                    // Check if all objects were filtered out
                    if (filteredFetchObjects.isEmpty() && !responseBody.isEmpty()) {
                        Log.w(TAG, "All " + responseBody.size() + " objects were filtered out due to invalid names");
                        callback.onError("No valid data found. All items had invalid names.");
                        return;
                    }

                    callback.onSuccess(filteredFetchObjects);
                } else {
                    // Server error - check if retryable
                    handleServerError(response.code(), callback, attemptNumber);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<FetchObject>> call, @NonNull Throwable t) {
                handleNetworkError(t, callback, attemptNumber);
            }
        });
    }

    /**
     * Filters the response to remove invalid objects according to business rules.
     *
     * @param objects the raw list from the API
     * @return filtered list of valid objects
     */
    private List<FetchObject> filterValidObjects(List<FetchObject> objects) {
        List<FetchObject> filteredFetchObjects = new ArrayList<>();
        for (FetchObject obj : objects) {
            if (obj.getName() != null &&
                    !obj.getName().trim().isEmpty() &&
                    !obj.getName().trim().equalsIgnoreCase("null")) {
                filteredFetchObjects.add(obj);
            }
        }
        Log.d(TAG, "Filtered " + objects.size() + " objects down to " + filteredFetchObjects.size() + " valid objects");
        return filteredFetchObjects;
    }

    /**
     * Handles server errors and determines if retry is appropriate.
     *
     * @param responseCode  HTTP response code
     * @param callback      callback to notify on final failure
     * @param attemptNumber current attempt number
     */
    private void handleServerError(int responseCode, RepositoryCallback<List<FetchObject>> callback,
            int attemptNumber) {
        Log.w(TAG, "Server error: HTTP " + responseCode);

        if (isRetryableServerError(responseCode) && attemptNumber < MAX_RETRY_ATTEMPTS - 1) {
            scheduleRetry(callback, attemptNumber + 1, "Server error (HTTP " + responseCode + ")");
        } else {
            String errorMessage = getServerErrorMessage(responseCode);
            callback.onError(errorMessage);
        }
    }

    /**
     * Handles network errors and determines if retry is appropriate.
     *
     * @param throwable     the network error
     * @param callback      callback to notify on final failure
     * @param attemptNumber current attempt number
     */
    private void handleNetworkError(Throwable throwable, RepositoryCallback<List<FetchObject>> callback,
            int attemptNumber) {
        Log.w(TAG, "Network error: " + throwable.getMessage(), throwable);

        if (isRetryableNetworkError(throwable) && attemptNumber < MAX_RETRY_ATTEMPTS - 1) {
            scheduleRetry(callback, attemptNumber + 1, throwable.getMessage());
        } else {
            String errorMessage = getNetworkErrorMessage(throwable);
            callback.onError(errorMessage);
        }
    }

    /**
     * Schedules a retry attempt with exponential backoff.
     *
     * @param callback    callback for the retry
     * @param nextAttempt next attempt number
     * @param reason      reason for the retry (for logging)
     */
    private void scheduleRetry(RepositoryCallback<List<FetchObject>> callback, int nextAttempt, String reason) {
        long delayMs = (long) (INITIAL_RETRY_DELAY_MS * Math.pow(BACKOFF_MULTIPLIER, nextAttempt));

        Log.i(TAG, "Scheduling retry " + (nextAttempt + 1) + "/" + MAX_RETRY_ATTEMPTS +
                " in " + delayMs + "ms due to: " + reason);

        // Notify the UI about the retry attempt
        String retryMessage = "Retrying... (" + (nextAttempt + 1) + "/" + MAX_RETRY_ATTEMPTS + ")";
        callback.onRetrying(retryMessage);

        mainHandler.postDelayed(() -> {
            fetchObjectsWithRetry(callback, nextAttempt);
        }, delayMs);
    }

    /**
     * Determines if a server error is worth retrying.
     *
     * @param responseCode HTTP response code
     * @return true if the error is likely temporary
     */
    private boolean isRetryableServerError(int responseCode) {
        return responseCode >= 500 || // Server errors (5xx)
                responseCode == 408 || // Request timeout
                responseCode == 429; // Too many requests
    }

    /**
     * Determines if a network error is worth retrying.
     *
     * @param throwable the network error
     * @return true if the error is likely temporary
     */
    private boolean isRetryableNetworkError(Throwable throwable) {
        return throwable instanceof SocketTimeoutException ||
                throwable instanceof UnknownHostException ||
                (throwable instanceof IOException && !isIrrecoverableIOException(throwable));
    }

    /**
     * Checks if an IOException is irrecoverable (like malformed URL).
     *
     * @param throwable the IOException
     * @return true if the error is permanent
     */
    private boolean isIrrecoverableIOException(Throwable throwable) {
        String message = throwable.getMessage();
        return message != null && (message.contains("malformed") ||
                message.contains("protocol") ||
                message.contains("SSL") ||
                message.contains("certificate"));
    }

    /**
     * Gets a user-friendly error message for server errors.
     *
     * @param responseCode HTTP response code
     * @return user-friendly error message
     */
    private String getServerErrorMessage(int responseCode) {
        switch (responseCode) {
            case 400:
                return "Invalid request. Please try again later.";
            case 401:
                return "Authentication failed. Please check your credentials.";
            case 403:
                return "Access denied. You don't have permission to access this resource.";
            case 404:
                return "The requested data was not found.";
            case 408:
                return "Request timed out. Please check your connection and try again.";
            case 429:
                return "Too many requests. Please wait a moment and try again.";
            case 500:
                return "Server error. Please try again later.";
            case 502:
            case 503:
            case 504:
                return "Service temporarily unavailable. Please try again later.";
            default:
                return "Server error (HTTP " + responseCode + "). Please try again later.";
        }
    }

    /**
     * Gets a user-friendly error message for network errors.
     *
     * @param throwable the network error
     * @return user-friendly error message
     */
    private String getNetworkErrorMessage(Throwable throwable) {
        if (throwable instanceof SocketTimeoutException) {
            return "Connection timed out. Please check your internet connection and try again.";
        } else if (throwable instanceof UnknownHostException) {
            return "Cannot reach server. Please check your internet connection.";
        } else if (throwable instanceof IOException) {
            return "Network error occurred. Please check your connection and try again.";
        } else {
            return "Network error: " + throwable.getMessage();
        }
    }
}