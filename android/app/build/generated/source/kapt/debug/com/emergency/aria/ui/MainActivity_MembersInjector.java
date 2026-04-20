package com.emergency.aria.ui;

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
public final class MainActivity_MembersInjector implements MembersInjector<MainActivity> {
  private final Provider<CommunicationManager> commManagerProvider;

  public MainActivity_MembersInjector(Provider<CommunicationManager> commManagerProvider) {
    this.commManagerProvider = commManagerProvider;
  }

  public static MembersInjector<MainActivity> create(
      Provider<CommunicationManager> commManagerProvider) {
    return new MainActivity_MembersInjector(commManagerProvider);
  }

  @Override
  public void injectMembers(MainActivity instance) {
    injectCommManager(instance, commManagerProvider.get());
  }

  @InjectedFieldSignature("com.emergency.aria.ui.MainActivity.commManager")
  public static void injectCommManager(MainActivity instance, CommunicationManager commManager) {
    instance.commManager = commManager;
  }
}
