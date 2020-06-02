package com.criteo.mediation.mopub;

import static com.criteo.mediation.mopub.MoPubHelper.serverExtras;
import static com.criteo.publisher.CriteoUtil.TEST_CP_ID;
import static com.criteo.publisher.concurrent.ThreadingUtil.runOnMainThreadAndWait;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.test.core.app.ApplicationProvider;
import com.criteo.publisher.model.InterstitialAdUnit;
import com.mopub.mobileads.CustomEventInterstitial.CustomEventInterstitialListener;
import java.util.HashMap;
import java.util.Map;

public class InterstitialAdapterHelper {

  @NonNull
  private final CriteoInterstitialAdapter adapter;

  @NonNull
  private final Context context = ApplicationProvider.getApplicationContext();

  public InterstitialAdapterHelper(@NonNull CriteoInterstitialAdapter adapter) {
    this.adapter = adapter;
  }

  public void loadInterstitial(@NonNull InterstitialAdUnit adUnit, CustomEventInterstitialListener listener) {
    Map<String, String> serverExtras = serverExtras(TEST_CP_ID, adUnit.getAdUnitId());
    Map<String, Object> localExtras = new HashMap<>();

    runOnMainThreadAndWait(() -> {
      adapter.loadInterstitial(context, listener, localExtras, serverExtras);
    });
  }

}
