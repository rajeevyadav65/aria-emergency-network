package com.emergency.aria.ui;

import com.emergency.aria.CommunicationManager;
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
public final class EmergencyViewModel_Factory implements Factory<EmergencyViewModel> {
  private final Provider<CommunicationManager> communicationManagerProvider;

  public EmergencyViewModel_Factory(Provider<CommunicationManager> communicationManagerProvider) {
    this.communicationManagerProvider = communicationManagerProvider;
  }

  @Override
  public EmergencyViewModel get() {
    return newInstance(communicationManagerProvider.get());
  }

  public static EmergencyViewModel_Factory create(
      Provider<CommunicationManager> communicationManagerProvider) {
    return new EmergencyViewModel_Factory(communicationManagerProvider);
  }

  public static EmergencyViewModel newInstance(CommunicationManager communicationManager) {
    return new EmergencyViewModel(communicationManager);
  }
}
