package com.emergency.aria.service;

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
public final class EmergencyServiceManager_Factory implements Factory<EmergencyServiceManager> {
  private final Provider<OnlineService> onlineServiceProvider;

  public EmergencyServiceManager_Factory(Provider<OnlineService> onlineServiceProvider) {
    this.onlineServiceProvider = onlineServiceProvider;
  }

  @Override
  public EmergencyServiceManager get() {
    return newInstance(onlineServiceProvider.get());
  }

  public static EmergencyServiceManager_Factory create(
      Provider<OnlineService> onlineServiceProvider) {
    return new EmergencyServiceManager_Factory(onlineServiceProvider);
  }

  public static EmergencyServiceManager newInstance(OnlineService onlineService) {
    return new EmergencyServiceManager(onlineService);
  }
}
