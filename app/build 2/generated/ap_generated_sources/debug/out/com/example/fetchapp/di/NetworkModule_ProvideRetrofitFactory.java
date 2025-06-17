package com.example.fetchapp.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
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
public final class NetworkModule_ProvideRetrofitFactory implements Factory<Retrofit> {
  private final NetworkModule module;

  public NetworkModule_ProvideRetrofitFactory(NetworkModule module) {
    this.module = module;
  }

  @Override
  public Retrofit get() {
    return provideRetrofit(module);
  }

  public static NetworkModule_ProvideRetrofitFactory create(NetworkModule module) {
    return new NetworkModule_ProvideRetrofitFactory(module);
  }

  public static Retrofit provideRetrofit(NetworkModule instance) {
    return Preconditions.checkNotNullFromProvides(instance.provideRetrofit());
  }
}
