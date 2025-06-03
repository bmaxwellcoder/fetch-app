package com.example.fetchapp.repository;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.fetchapp.model.FetchObject;
import com.example.fetchapp.network.FetchObjectRetrofitService;
import com.example.fetchapp.util.NetworkUtils;

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
    private final FetchObjectRetrofitService apiService;
    private final Context context;

    /**
     * Constructs the repository with the required API service and context.
     * 
     * @param apiService the Retrofit service for network calls (provided by Hilt)
     * @param context    the application context for network checks
     */
    @Inject
    public FetchObjectRepository(FetchObjectRetrofitService apiService, Context context) {
        this.apiService = apiService;
        this.context = context;
    }

    /**
     * Fetches the list of FetchObjects from the API, filtering out items with null,
     * empty, or "null" names.
     *
     * @param callback callback to receive the filtered list or an error message
     */
    public void fetchObjects(RepositoryCallback<List<FetchObject>> callback) {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            callback.onError("No network connection available");
            return;
        }

        apiService.getFetchObjects().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<FetchObject>> call,
                    @NonNull Response<List<FetchObject>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Filter out items with null, empty, or "null" names as per business
                    // requirements
                    List<FetchObject> filtered = new ArrayList<>();
                    for (FetchObject obj : response.body()) {
                        if (obj.getName() != null &&
                                !obj.getName().trim().isEmpty() &&
                                !obj.getName().trim().equalsIgnoreCase("null")) {
                            filtered.add(obj);
                        }
                    }
                    callback.onSuccess(filtered);
                } else {
                    callback.onError("API error: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<FetchObject>> call, @NonNull Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    /**
     * Callback interface for repository data operations.
     * 
     * @param <T> the type of data expected on success
     */
    public interface RepositoryCallback<T> {
        void onSuccess(T data);

        void onError(String errorMsg);
    }
}