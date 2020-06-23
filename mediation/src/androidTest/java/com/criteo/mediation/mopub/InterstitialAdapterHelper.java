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
