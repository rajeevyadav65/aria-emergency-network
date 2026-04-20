package com.emergency.aria.di;

import android.content.Context;
import com.emergency.aria.bluetooth.BluetoothService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class AppModule_ProvideBluetoothServiceFactory implements Factory<BluetoothService> {
  private final Provider<Context> ctxProvider;

  public AppModule_ProvideBluetoothServiceFactory(Provider<Context> ctxProvider) {
    this.ctxProvider = ctxProvider;
  }

  @Override
  public BluetoothService get() {
    return provideBluetoothService(ctxProvider.get());
  }

  public static AppModule_ProvideBluetoothServiceFactory create(Provider<Context> ctxProvider) {
    return new AppModule_ProvideBluetoothServiceFactory(ctxProvider);
  }

  public static BluetoothService provideBluetoothService(Context ctx) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideBluetoothService(ctx));
  }
}
