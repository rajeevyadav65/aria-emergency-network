package com.emergency.aria.sync;

import android.content.Context;
import com.emergency.aria.db.AriaDatabase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import retrofit2.Retrofit;

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
public final class SyncManager_Factory implements Factory<SyncManager> {
  private final Provider<Context> contextProvider;

  private final Provider<AriaDatabase> dbProvider;

  private final Provider<Retrofit> retrofitProvider;

  public SyncManager_Factory(Provider<Context> contextProvider, Provider<AriaDatabase> dbProvider,
      Provider<Retrofit> retrofitProvider) {
    this.contextProvider = contextProvider;
    this.dbProvider = dbProvider;
    this.retrofitProvider = retrofitProvider;
  }

  @Override
  public SyncManager get() {
    return newInstance(contextProvider.get(), dbProvider.get(), retrofitProvider.get());
  }

  public static SyncManager_Factory create(Provider<Context> contextProvider,
      Provider<AriaDatabase> dbProvider, Provider<Retrofit> retrofitProvider) {
    return new SyncManager_Factory(contextProvider, dbProvider, retrofitProvider);
  }

  public static SyncManager newInstance(Context context, AriaDatabase db, Retrofit retrofit) {
    return new SyncManager(context, db, retrofit);
  }
}
