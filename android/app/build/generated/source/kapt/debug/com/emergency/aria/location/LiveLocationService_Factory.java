package com.emergency.aria.location;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class LiveLocationService_Factory implements Factory<LiveLocationService> {
  private final Provider<Context> contextProvider;

  public LiveLocationService_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public LiveLocationService get() {
    return newInstance(contextProvider.get());
  }

  public static LiveLocationService_Factory create(Provider<Context> contextProvider) {
    return new LiveLocationService_Factory(contextProvider);
  }

  public static LiveLocationService newInstance(Context context) {
    return new LiveLocationService(context);
  }
}
