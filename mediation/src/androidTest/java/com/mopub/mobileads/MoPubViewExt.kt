package com.mopub.mobileads

import com.criteo.publisher.concurrent.ThreadingUtil.runOnMainThreadAndWait
import com.mopub.network.AdResponse

/**
 * Mock the job done by the [MoPubView.loadAd] method.
 *
 * Instead of calling the MoPub server, it uses a forged [AdResponse] corresponding to the desired
 * ad and invoke the success callback.
 */
fun MoPubView.loadAd(adResponse: AdResponse) {
  runOnMainThreadAndWait {
    adViewController.onAdLoadSuccess(adResponse)
  }
}