package com.emergency.aria.ai;

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
public final class FallDetectionService_Factory implements Factory<FallDetectionService> {
  private final Provider<Context> contextProvider;

  private final Provider<FaceDetectionEngine> faceDetectionEngineProvider;

  public FallDetectionService_Factory(Provider<Context> contextProvider,
      Provider<FaceDetectionEngine> faceDetectionEngineProvider) {
    this.contextProvider = contextProvider;
    this.faceDetectionEngineProvider = faceDetectionEngineProvider;
  }

  @Override
  public FallDetectionService get() {
    return newInstance(contextProvider.get(), faceDetectionEngineProvider.get());
  }

  public static FallDetectionService_Factory create(Provider<Context> contextProvider,
      Provider<FaceDetectionEngine> faceDetectionEngineProvider) {
    return new FallDetectionService_Factory(contextProvider, faceDetectionEngineProvider);
  }

  public static FallDetectionService newInstance(Context context,
      FaceDetectionEngine faceDetectionEngine) {
    return new FallDetectionService(context, faceDetectionEngine);
  }
}
