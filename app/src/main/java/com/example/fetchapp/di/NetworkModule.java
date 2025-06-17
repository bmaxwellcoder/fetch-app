package com.example.fetchapp.di;

import android.app.Application;
import android.content.Context;

import com.example.fetchapp.network.FetchObjectRetrofitService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Hilt module that provides network-related dependencies for the app.
 *
 * This module is installed in the SingletonComponent, meaning all provided
 * dependencies will have application-wide singleton scope.
 *
 * Responsibilities:
 * - Provide a singleton Retrofit instance configured with the base URL and Gson
 * converter
 * - Provide a singleton instance of the FetchObjectRetrofitService API interface
 * - Provide the application context for network checks
 *
 * Usage:
 * - Inject FetchObjectRetrofitService or Retrofit wherever needed in your app
 * - Follows MVVM best practices by keeping network setup in the DI layer
 */
@Module
@InstallIn(SingletonComponent.class)
public class NetworkModule {
    /**
     * Provides the application context.
     *
     * @param application the application instance
     * @return the application context
     */
    @Provides
    @Singleton
    public Context provideContext(Application application) {
        return application.getApplicationContext();
    }

    /**
     * Provides a singleton Retrofit instance for network requests.
     *
     * @return a configured Retrofit instance
     */
    @Provides
    @Singleton
    public Retrofit provideRetrofit() {
        return new Retrofit.Builder()
                .baseUrl("https://hiring.fetch.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    /**
     * Provides a singleton instance of the FetchObjectRetrofitService API
     * interface.
     *
     * @param retrofit the Retrofit instance to use for creating the service
     * @return a FetchObjectRetrofitService implementation
     */
    @Provides
    @Singleton
    public FetchObjectRetrofitService provideFetchObjectRetrofitService(Retrofit retrofit) {
        return retrofit.create(FetchObjectRetrofitService.class);
    }
}