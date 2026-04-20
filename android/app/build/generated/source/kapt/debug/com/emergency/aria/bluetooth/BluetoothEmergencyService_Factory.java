package com.emergency.aria.bluetooth;

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
public final class BluetoothEmergencyService_Factory implements Factory<BluetoothEmergencyService> {
  private final Provider<Context> contextProvider;

  public BluetoothEmergencyService_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public BluetoothEmergencyService get() {
    return newInstance(contextProvider.get());
  }

  public static BluetoothEmergencyService_Factory create(Provider<Context> contextProvider) {
    return new BluetoothEmergencyService_Factory(contextProvider);
  }

  public static BluetoothEmergencyService newInstance(Context context) {
    return new BluetoothEmergencyService(context);
  }
}
