package com.criteo.publisher.advancednative;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import androidx.annotation.RestrictTo.Scope;

public class NativeInternal {

  /**
   * This class serves as a bridge between this adapter and the internals (package-private) of the
   * SDK.
   * <p>
   * To access the package-private methods, a Java class should be in the same package. This is why
   * this class is @Keep to not mangle the package name. Else the package name could become, {@code
   * com.criteo.a.a} and then the runtime could throw a {@link IllegalAccessError}.
   */
  @Keep
  @RestrictTo(Scope.LIBRARY)
  public NativeInternal() {
    // no-op
  }

  public static CriteoNativeRenderer decorateWithAdChoice(@NonNull CriteoNativeRenderer delegate) {
    return new AdChoiceOverlayNativeRenderer(delegate);
  }

  public static void setRenderer(
      @NonNull CriteoNativeAd nativeAd,
      @NonNull CriteoNativeRenderer renderer) {
    nativeAd.setRenderer(renderer);
  }

}
