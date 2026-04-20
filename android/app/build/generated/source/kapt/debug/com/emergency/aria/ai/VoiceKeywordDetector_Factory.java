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
public final class VoiceKeywordDetector_Factory implements Factory<VoiceKeywordDetector> {
  private final Provider<Context> contextProvider;

  public VoiceKeywordDetector_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public VoiceKeywordDetector get() {
    return newInstance(contextProvider.get());
  }

  public static VoiceKeywordDetector_Factory create(Provider<Context> contextProvider) {
    return new VoiceKeywordDetector_Factory(contextProvider);
  }

  public static VoiceKeywordDetector newInstance(Context context) {
    return new VoiceKeywordDetector(context);
  }
}
