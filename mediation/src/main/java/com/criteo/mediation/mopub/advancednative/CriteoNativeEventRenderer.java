package com.criteo.mediation.mopub.advancednative;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.criteo.publisher.advancednative.CriteoNativeAd;
import com.criteo.publisher.advancednative.CriteoNativeRenderer;
import com.criteo.publisher.advancednative.NativeInternalForMoPub;
import com.mopub.nativeads.BaseNativeAd;
import com.mopub.nativeads.MoPubAdRenderer;

@Keep
public class CriteoNativeEventRenderer implements MoPubAdRenderer<CriteoBaseNativeAd> {

  @NonNull
  private final CriteoNativeRenderer renderer;

  public CriteoNativeEventRenderer(@NonNull CriteoNativeRenderer renderer) {
    this.renderer = NativeInternalForMoPub.decorateWithAdChoice(renderer);
  }

  @NonNull
  @Override
  public View createAdView(@NonNull Context context, @Nullable ViewGroup parent) {
    return renderer.createNativeView(context, parent);
  }

  @Override
  public void renderAdView(@NonNull View view, @NonNull CriteoBaseNativeAd baseNativeAd) {
    CriteoNativeAd criteoNativeAd = baseNativeAd.getNativeAd();
    NativeInternalForMoPub.setRenderer(criteoNativeAd, renderer);
    criteoNativeAd.renderNativeView(view);
  }

  @Override
  public boolean supports(@NonNull BaseNativeAd baseNativeAd) {
    return baseNativeAd instanceof CriteoBaseNativeAd;
  }
}
