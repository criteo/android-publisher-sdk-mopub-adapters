package com.criteo.mediation.mopub.advancednative;

import android.view.View;
import androidx.annotation.NonNull;
import com.criteo.publisher.advancednative.CriteoNativeAd;
import com.mopub.nativeads.BaseNativeAd;

class CriteoBaseNativeAd extends BaseNativeAd {

  @NonNull
  private final CriteoNativeAd nativeAd;

  CriteoBaseNativeAd(@NonNull CriteoNativeAd nativeAd) {
    this.nativeAd = nativeAd;
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
    // unused
  }
}
