package com.example.fetchapp.network;

import com.example.fetchapp.model.FetchObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Retrofit service interface for Fetch API network requests.
 *
 * Responsibilities:
 * - Defines the HTTP endpoints and request methods for the Fetch API
 * - Used by Retrofit to generate the implementation for network calls
 *
 * Usage in MVVM:
 * - Provided as a dependency via Hilt in the NetworkModule
 * - Used by the Repository to fetch data from the remote API
 * - Returns a Call object for asynchronous network operations
 */
public interface FetchObjectRetrofitService {
    /**
     * Fetches the list of FetchObjects from the API endpoint.
     *
     * @return a Call object for a list of FetchObject items
     */
    @GET("hiring.json")
    Call<List<FetchObject>> getFetchObjects();
}