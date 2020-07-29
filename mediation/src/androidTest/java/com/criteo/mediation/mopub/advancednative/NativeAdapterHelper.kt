package com.criteo.mediation.mopub.advancednative

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.criteo.mediation.mopub.MoPubHelper.serverExtras
import com.criteo.publisher.CriteoUtil.TEST_CP_ID
import com.criteo.publisher.concurrent.ThreadingUtil.runOnMainThreadAndWait
import com.criteo.publisher.model.NativeAdUnit
import com.mopub.nativeads.CustomEventNative

class NativeAdapterHelper(private val adapter: CriteoNativeAdapter) {

  private val context = ApplicationProvider.getApplicationContext<Context>()

  fun loadNative(adUnit: NativeAdUnit, listener: CustomEventNative.CustomEventNativeListener) {
    val serverExtras = serverExtras(TEST_CP_ID, adUnit.adUnitId)

    runOnMainThreadAndWait {
      adapter.loadNativeAd(context, listener, mapOf(), serverExtras)
    }
  }

}