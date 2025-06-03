package com.example.fetchapp;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import android.app.Application;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;
import com.example.fetchapp.model.FetchObject;
import com.example.fetchapp.model.FetchObjectViewModel;
import com.example.fetchapp.repository.FetchObjectRepository;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.InOrder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Unit tests for the FetchObjectViewModel class.
 * 
 * These tests verify:
 * 1. Data fetching and processing
 * 2. LiveData observation and updates
 * 3. Error handling
 * 4. Null response handling
 * 5. Proper lifecycle management
 */
@RunWith(MockitoJUnitRunner.class)
public class FetchObjectViewModelTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private FetchObjectViewModel viewModel;

    @Mock
    private Application mockApplication;

    @Mock
    private FetchObjectRepository mockRepository;

    @Mock
    private Observer<List<FetchObject>> fetchObjectsObserver;

    @Mock
    private Observer<String> errorStateObserver;

    @Mock
    private Observer<Boolean> loadingObserver;

    /**
     * Sets up the test environment before each test.
     * Initializes the ViewModel and sets up observers for LiveData.
     */
    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        viewModel = new FetchObjectViewModel(mockApplication, mockRepository);
        viewModel.getAllFetchObjects().observeForever(fetchObjectsObserver);
        viewModel.getErrorState().observeForever(errorStateObserver);
        viewModel.getLoading().observeForever(loadingObserver);
    }

    /**
     * Tests successful data fetching.
     * Verifies that:
     * - Loading state is properly managed
     * - Data is passed to observer
     * - No errors are triggered
     */
    @Test
    public void testSuccessfulFetch() {
        List<FetchObject> mockData = new ArrayList<>();
        mockData.add(new FetchObject(1, 1, "Test Item"));

        doAnswer(invocation -> {
            FetchObjectRepository.RepositoryCallback<List<FetchObject>> callback = invocation.getArgument(0);
            callback.onSuccess(mockData);
            return null;
        }).when(mockRepository).fetchObjects(any());

        viewModel.fetchFetchObjects();

        verify(loadingObserver).onChanged(true);
        verify(fetchObjectsObserver).onChanged(mockData);
        verify(errorStateObserver, never()).onChanged(argThat(Objects::nonNull));
        verify(loadingObserver, atLeastOnce()).onChanged(false);
    }

    /**
     * Tests error handling during data fetching.
     * Verifies that:
     * - Loading state is properly managed
     * - Error message is passed to observer
     * - No data is passed to observer
     */
    @Test
    public void testErrorHandling() {
        String errorMessage = "Test error";

        doAnswer(invocation -> {
            FetchObjectRepository.RepositoryCallback<List<FetchObject>> callback = invocation.getArgument(0);
            callback.onError(errorMessage);
            return null;
        }).when(mockRepository).fetchObjects(any());

        viewModel.fetchFetchObjects();

        verify(loadingObserver).onChanged(true);
        // Allow initial null emission, then the error message
        InOrder inOrder = inOrder(errorStateObserver);
        inOrder.verify(errorStateObserver).onChanged(null);
        inOrder.verify(errorStateObserver).onChanged(errorMessage);

        verify(fetchObjectsObserver).onChanged(null);
        verify(loadingObserver, atLeastOnce()).onChanged(false);
    }

    /**
     * Tests handling of empty response.
     * Verifies that:
     * - Empty list is passed to observer
     * - No errors are triggered
     * - Loading state is properly managed
     */
    @Test
    public void testEmptyResponse() {
        doAnswer(invocation -> {
            FetchObjectRepository.RepositoryCallback<List<FetchObject>> callback = invocation.getArgument(0);
            callback.onSuccess(new ArrayList<>());
            return null;
        }).when(mockRepository).fetchObjects(any());

        viewModel.fetchFetchObjects();

        verify(loadingObserver).onChanged(true);
        verify(fetchObjectsObserver).onChanged(anyList());
        verify(errorStateObserver, never()).onChanged(argThat(Objects::nonNull));
        verify(loadingObserver, atLeastOnce()).onChanged(false);
    }
}