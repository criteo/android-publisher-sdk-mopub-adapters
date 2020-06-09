package com.criteo.mediation.mopub.advancednative;

import android.content.Context;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import com.criteo.mediation.mopub.CriteoInitializer;
import com.criteo.publisher.advancednative.CriteoNativeLoader;
import com.criteo.publisher.model.NativeAdUnit;
import com.mopub.nativeads.CustomEventNative;
import java.util.Map;

@Keep
public class CriteoNativeAdapter extends CustomEventNative {

  private static final String ADUNIT_ID = "adUnitId";
  private static final String CRITEO_PUBLISHER_ID = "cpId";

  @Override
  protected void loadNativeAd(
      @NonNull Context context,
      @NonNull CustomEventNativeListener customEventNativeListener,
      @NonNull Map<String, Object> localExtras,
      @NonNull Map<String, String> serverExtras
  ) {
    // TODO Handle missing arguments

    String criteoPublisherId = serverExtras.get(CRITEO_PUBLISHER_ID);
    String adUnitId = serverExtras.get(ADUNIT_ID);

    CriteoInitializer criteoInitializer = new CriteoInitializer();
    criteoInitializer.init(context, criteoPublisherId);

    CriteoNativeLoader nativeLoader = new CriteoNativeLoader(
        new NativeAdUnit(adUnitId),
        new CriteoNativeEventListener(customEventNativeListener),
        new NoOpNativeRenderer()
    );

    nativeLoader.loadAd();
  }

}
