package com.criteo.mediation.mopub;

import static com.criteo.mediation.mopub.MoPubHelper.localExtras;
import static com.criteo.mediation.mopub.MoPubHelper.serverExtras;
import static com.criteo.publisher.CriteoUtil.TEST_CP_ID;
import static com.criteo.publisher.concurrent.ThreadingUtil.runOnMainThreadAndWait;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import com.criteo.publisher.model.BannerAdUnit;
import com.mopub.mobileads.CustomEventBanner.CustomEventBannerListener;
import java.util.Map;

public class BannerAdapterHelper {

  @NonNull
  private final CriteoBannerAdapter adapter;

  @NonNull
  private final Context context = InstrumentationRegistry.getContext();

  public BannerAdapterHelper(@NonNull CriteoBannerAdapter adapter) {
    this.adapter = adapter;
  }

  public void loadBanner(@NonNull BannerAdUnit adUnit, CustomEventBannerListener listener) {
    Map<String, String> serverExtras = serverExtras(TEST_CP_ID, adUnit.getAdUnitId());
    Map<String, Object> localExtras = localExtras(
        adUnit.getSize().getWidth(),
        adUnit.getSize().getHeight()
    );

    runOnMainThreadAndWait(() -> {
      adapter.loadBanner(context, listener, localExtras, serverExtras);
    });
  }

}
