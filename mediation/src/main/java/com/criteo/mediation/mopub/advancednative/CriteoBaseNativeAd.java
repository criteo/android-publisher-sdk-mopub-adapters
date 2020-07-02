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

package com.criteo.mediation.mopub.advancednative;

import android.view.View;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.criteo.publisher.advancednative.CriteoNativeAd;
import com.criteo.publisher.advancednative.CriteoNativeAdListener;
import com.mopub.nativeads.BaseNativeAd;

class CriteoBaseNativeAd extends BaseNativeAd {

  @NonNull
  private final CriteoNativeAd nativeAd;

  /**
   * Hold the listener until the end of life of this ad
   *
   * Normally it is the job of the native loader to hold the listener. But in case of this adapter,
   * the loader is thrown directly and nothing prevent the listener to be GC. So it is hold here.
   */
  @Keep
  @Nullable
  private CriteoNativeAdListener listener;

  CriteoBaseNativeAd(
      @NonNull CriteoNativeAd nativeAd,
      @NonNull CriteoNativeEventListener listener
  ) {
    this.nativeAd = nativeAd;
    this.listener = listener;
  }

  @NonNull
  CriteoNativeAd getNativeAd() {
    return nativeAd;
  }

  void onAdImpression() {
    notifyAdImpressed();
  }

  void onAdClicked() {
    notifyAdClicked();
  }

  @Override
  public void prepare(@NonNull View view) {
    // unused
  }

  @Override
  public void clear(@NonNull View view) {
    // unused
  }

  @Override
  public void destroy() {
    this.listener = null;
  }
}
