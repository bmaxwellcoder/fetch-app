package com.example.fetchapp.di;

import com.example.fetchapp.network.FetchObjectRetrofitService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import retrofit2.Retrofit;

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
public final class NetworkModule_ProvideFetchObjectRetrofitServiceFactory implements Factory<FetchObjectRetrofitService> {
  private final NetworkModule module;

  private final Provider<Retrofit> retrofitProvider;

  public NetworkModule_ProvideFetchObjectRetrofitServiceFactory(NetworkModule module,
      Provider<Retrofit> retrofitProvider) {
    this.module = module;
    this.retrofitProvider = retrofitProvider;
  }

  @Override
  public FetchObjectRetrofitService get() {
    return provideFetchObjectRetrofitService(module, retrofitProvider.get());
  }

  public static NetworkModule_ProvideFetchObjectRetrofitServiceFactory create(NetworkModule module,
      Provider<Retrofit> retrofitProvider) {
    return new NetworkModule_ProvideFetchObjectRetrofitServiceFactory(module, retrofitProvider);
  }

  public static FetchObjectRetrofitService provideFetchObjectRetrofitService(NetworkModule instance,
      Retrofit retrofit) {
    return Preconditions.checkNotNullFromProvides(instance.provideFetchObjectRetrofitService(retrofit));
  }
}
