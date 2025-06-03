package com.example.fetchrewardsexercisejava.model;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.fetchrewardsexercisejava.repository.FetchObjectRepository;

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
    private final MutableLiveData<List<FetchObject>> fetchObjects = new MutableLiveData<>();
    private final MutableLiveData<String> errorState = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final FetchObjectRepository repository;

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
     * Initiates fetching of FetchObjects from the repository and updates LiveData.
     * Handles loading and error state transitions.
     */
    public void fetchFetchObjects() {
        loading.postValue(true);
        errorState.postValue(null);
        repository.fetchObjects(new FetchObjectRepository.RepositoryCallback<List<FetchObject>>() {
            @Override
            public void onSuccess(List<FetchObject> data) {
                loading.postValue(false);
                fetchObjects.postValue(data);
            }

            @Override
            public void onError(String errorMsg) {
                loading.postValue(false);
                errorState.postValue(errorMsg);
                fetchObjects.postValue(null);
            }
        });
    }

    /**
     * Alias for fetchFetchObjects() to match MainActivity usage.
     */
    public void fetchData() {
        fetchFetchObjects();
    }
}
