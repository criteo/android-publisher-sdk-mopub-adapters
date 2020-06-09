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
    baseNativeAd = new CriteoBaseNativeAd(nativeAd);
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
