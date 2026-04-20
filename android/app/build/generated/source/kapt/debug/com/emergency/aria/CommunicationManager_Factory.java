package com.emergency.aria;

import com.emergency.aria.bluetooth.BluetoothService;
import com.emergency.aria.service.OnlineService;
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
public final class CommunicationManager_Factory implements Factory<CommunicationManager> {
  private final Provider<OnlineService> onlineServiceProvider;

  private final Provider<BluetoothService> bluetoothServiceProvider;

  public CommunicationManager_Factory(Provider<OnlineService> onlineServiceProvider,
      Provider<BluetoothService> bluetoothServiceProvider) {
    this.onlineServiceProvider = onlineServiceProvider;
    this.bluetoothServiceProvider = bluetoothServiceProvider;
  }

  @Override
  public CommunicationManager get() {
    return newInstance(onlineServiceProvider.get(), bluetoothServiceProvider.get());
  }

  public static CommunicationManager_Factory create(Provider<OnlineService> onlineServiceProvider,
      Provider<BluetoothService> bluetoothServiceProvider) {
    return new CommunicationManager_Factory(onlineServiceProvider, bluetoothServiceProvider);
  }

  public static CommunicationManager newInstance(OnlineService onlineService,
      BluetoothService bluetoothService) {
    return new CommunicationManager(onlineService, bluetoothService);
  }
}
