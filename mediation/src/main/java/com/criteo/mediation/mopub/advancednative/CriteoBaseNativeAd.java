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
