/*
 *    Copyright 2020 Criteo
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.criteo.publisher.advancednative;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import androidx.annotation.RestrictTo.Scope;

public class NativeInternalForMoPub {

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
  public NativeInternalForMoPub() {
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
