package com.example.viewmodel;

import android.app.Application;
import com.example.fetchapp.repository.FetchObjectRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class FetchObjectViewModel_Factory implements Factory<FetchObjectViewModel> {
  private final Provider<Application> applicationProvider;

  private final Provider<FetchObjectRepository> repositoryProvider;

  public FetchObjectViewModel_Factory(Provider<Application> applicationProvider,
      Provider<FetchObjectRepository> repositoryProvider) {
    this.applicationProvider = applicationProvider;
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public FetchObjectViewModel get() {
    return newInstance(applicationProvider.get(), repositoryProvider.get());
  }

  public static FetchObjectViewModel_Factory create(Provider<Application> applicationProvider,
      Provider<FetchObjectRepository> repositoryProvider) {
    return new FetchObjectViewModel_Factory(applicationProvider, repositoryProvider);
  }

  public static FetchObjectViewModel newInstance(Application application,
      FetchObjectRepository repository) {
    return new FetchObjectViewModel(application, repository);
  }
}
