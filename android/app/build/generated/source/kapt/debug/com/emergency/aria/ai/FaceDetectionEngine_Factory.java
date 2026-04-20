package com.emergency.aria.ai;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class FaceDetectionEngine_Factory implements Factory<FaceDetectionEngine> {
  @Override
  public FaceDetectionEngine get() {
    return newInstance();
  }

  public static FaceDetectionEngine_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static FaceDetectionEngine newInstance() {
    return new FaceDetectionEngine();
  }

  private static final class InstanceHolder {
    private static final FaceDetectionEngine_Factory INSTANCE = new FaceDetectionEngine_Factory();
  }
}
