package com.emergency.aria.wifi;

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
public final class WiFiDirectService_Factory implements Factory<WiFiDirectService> {
  private final Provider<Context> contextProvider;

  public WiFiDirectService_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public WiFiDirectService get() {
    return newInstance(contextProvider.get());
  }

  public static WiFiDirectService_Factory create(Provider<Context> contextProvider) {
    return new WiFiDirectService_Factory(contextProvider);
  }

  public static WiFiDirectService newInstance(Context context) {
    return new WiFiDirectService(context);
  }
}
