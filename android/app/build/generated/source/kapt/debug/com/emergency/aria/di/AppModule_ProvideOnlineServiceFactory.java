package com.emergency.aria.di;

import com.emergency.aria.service.OnlineService;
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
public final class AppModule_ProvideOnlineServiceFactory implements Factory<OnlineService> {
  private final Provider<Retrofit> retrofitProvider;

  public AppModule_ProvideOnlineServiceFactory(Provider<Retrofit> retrofitProvider) {
    this.retrofitProvider = retrofitProvider;
  }

  @Override
  public OnlineService get() {
    return provideOnlineService(retrofitProvider.get());
  }

  public static AppModule_ProvideOnlineServiceFactory create(Provider<Retrofit> retrofitProvider) {
    return new AppModule_ProvideOnlineServiceFactory(retrofitProvider);
  }

  public static OnlineService provideOnlineService(Retrofit retrofit) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideOnlineService(retrofit));
  }
}
