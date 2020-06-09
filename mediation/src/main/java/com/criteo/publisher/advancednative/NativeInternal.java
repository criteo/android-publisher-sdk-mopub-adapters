package com.criteo.publisher.advancednative;

import androidx.annotation.NonNull;
import java.lang.reflect.Field;

public class NativeInternal {

  public static CriteoNativeRenderer decorateWithAdChoice(@NonNull CriteoNativeRenderer delegate) {
    // TODO Encapsulate in AdChoiceOverlayNativeRenderer
    return delegate;
  }

  public static void setRenderer(
      @NonNull CriteoNativeAd nativeAd,
      @NonNull CriteoNativeRenderer renderer) {
    // FIXME This works but it is ugly. It may be preferable to just provide a setRenderer method
    try {
      Field field = nativeAd.getClass().getDeclaredField("renderer");
      field.setAccessible(true);
      field.set(nativeAd, renderer);
    } catch (Exception ignored) {
    }
  }

}
