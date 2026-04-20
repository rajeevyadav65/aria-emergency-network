package com.emergency.aria.di;

import com.emergency.aria.CommunicationManager;
import com.emergency.aria.bluetooth.BluetoothService;
import com.emergency.aria.service.OnlineService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class AppModule_ProvideCommunicationManagerFactory implements Factory<CommunicationManager> {
  private final Provider<OnlineService> onlineServiceProvider;

  private final Provider<BluetoothService> bluetoothServiceProvider;

  public AppModule_ProvideCommunicationManagerFactory(Provider<OnlineService> onlineServiceProvider,
      Provider<BluetoothService> bluetoothServiceProvider) {
    this.onlineServiceProvider = onlineServiceProvider;
    this.bluetoothServiceProvider = bluetoothServiceProvider;
  }

  @Override
  public CommunicationManager get() {
    return provideCommunicationManager(onlineServiceProvider.get(), bluetoothServiceProvider.get());
  }

  public static AppModule_ProvideCommunicationManagerFactory create(
      Provider<OnlineService> onlineServiceProvider,
      Provider<BluetoothService> bluetoothServiceProvider) {
    return new AppModule_ProvideCommunicationManagerFactory(onlineServiceProvider, bluetoothServiceProvider);
  }

  public static CommunicationManager provideCommunicationManager(OnlineService onlineService,
      BluetoothService bluetoothService) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideCommunicationManager(onlineService, bluetoothService));
  }
}
