package com.example.fetchapp.repository;

import android.content.Context;
import com.example.fetchapp.network.FetchObjectRetrofitService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava"
})
public final class FetchObjectRepository_Factory implements Factory<FetchObjectRepository> {
  private final Provider<FetchObjectRetrofitService> fetchObjectRetrofitServiceProvider;

  private final Provider<Context> contextProvider;

  public FetchObjectRepository_Factory(
      Provider<FetchObjectRetrofitService> fetchObjectRetrofitServiceProvider,
      Provider<Context> contextProvider) {
    this.fetchObjectRetrofitServiceProvider = fetchObjectRetrofitServiceProvider;
    this.contextProvider = contextProvider;
  }

  @Override
  public FetchObjectRepository get() {
    return newInstance(fetchObjectRetrofitServiceProvider.get(), contextProvider.get());
  }

  public static FetchObjectRepository_Factory create(
      Provider<FetchObjectRetrofitService> fetchObjectRetrofitServiceProvider,
      Provider<Context> contextProvider) {
    return new FetchObjectRepository_Factory(fetchObjectRetrofitServiceProvider, contextProvider);
  }

  public static FetchObjectRepository newInstance(
      FetchObjectRetrofitService fetchObjectRetrofitService, Context context) {
    return new FetchObjectRepository(fetchObjectRetrofitService, context);
  }
}
