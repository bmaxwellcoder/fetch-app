package com.example.fetchapp.repository;

/**
 * Callback interface for repository data operations.
 *
 * Provides callbacks for success, error, and retry states to give
 * the UI comprehensive feedback about ongoing operations.
 *
 * @param <T> the type of data expected on success
 */
public interface RepositoryCallback<T> {
    /**
     * Called when the data operation completes successfully.
     *
     * @param data The requested data
     */
    void onSuccess(T data);

    /**
     * Called when the data operation fails permanently.
     *
     * @param errorMsg Description of the error
     */
    void onError(String errorMsg);

    /**
     * Called when a retry attempt is being scheduled.
     * This allows the UI to provide feedback about retry attempts in progress.
     *
     * @param retryMsg Description of the retry attempt
     */
    default void onRetrying(String retryMsg) {
        // Default empty implementation for backward compatibility
    }
}