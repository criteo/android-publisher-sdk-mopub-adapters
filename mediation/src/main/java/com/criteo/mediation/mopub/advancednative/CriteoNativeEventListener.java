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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import com.criteo.mediation.mopub.ErrorCode;
import com.criteo.publisher.CriteoErrorCode;
import com.criteo.publisher.advancednative.CriteoNativeAd;
import com.criteo.publisher.advancednative.CriteoNativeAdListener;
import com.mopub.nativeads.CustomEventNative.CustomEventNativeListener;

class CriteoNativeEventListener extends CriteoNativeAdListener {

  @NonNull
  private final CustomEventNativeListener customEventNativeListener;

  @VisibleForTesting
  @Nullable
  CriteoBaseNativeAd baseNativeAd;

  public CriteoNativeEventListener(@NonNull CustomEventNativeListener customEventNativeListener) {
    this.customEventNativeListener = customEventNativeListener;
  }

  @Override
  public void onAdReceived(@NonNull CriteoNativeAd nativeAd) {
    baseNativeAd = new CriteoBaseNativeAd(nativeAd, this);
    customEventNativeListener.onNativeAdLoaded(baseNativeAd);
  }

  @Override
  public void onAdFailedToReceive(@NonNull CriteoErrorCode errorCode) {
    customEventNativeListener.onNativeAdFailed(ErrorCode.toNativeMoPub(errorCode));
  }

  @Override
  public void onAdImpression() {
    if (baseNativeAd != null) {
      baseNativeAd.onAdImpression();
    }
  }

  @Override
  public void onAdClicked() {
    if (baseNativeAd != null) {
      baseNativeAd.onAdClicked();
    }
  }
}
