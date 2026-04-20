package com.emergency.aria.service;

import com.emergency.aria.CommunicationManager;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class EmergencyBackgroundService_MembersInjector implements MembersInjector<EmergencyBackgroundService> {
  private final Provider<CommunicationManager> commManagerProvider;

  public EmergencyBackgroundService_MembersInjector(
      Provider<CommunicationManager> commManagerProvider) {
    this.commManagerProvider = commManagerProvider;
  }

  public static MembersInjector<EmergencyBackgroundService> create(
      Provider<CommunicationManager> commManagerProvider) {
    return new EmergencyBackgroundService_MembersInjector(commManagerProvider);
  }

  @Override
  public void injectMembers(EmergencyBackgroundService instance) {
    injectCommManager(instance, commManagerProvider.get());
  }

  @InjectedFieldSignature("com.emergency.aria.service.EmergencyBackgroundService.commManager")
  public static void injectCommManager(EmergencyBackgroundService instance,
      CommunicationManager commManager) {
    instance.commManager = commManager;
  }
}
