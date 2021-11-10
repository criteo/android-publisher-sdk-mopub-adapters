package com.mopub.mobileads

import android.os.Looper
import com.mopub.mobileads.MoPubInterstitial.InterstitialState.LOADING
import com.mopub.network.AdResponse

/**
 * Mock the job done by the [MoPubInterstitial.load] method.
 *
 * Instead of calling the MoPub server, it uses a forged [AdResponse] corresponding to the desired
 * ad and invoke the success callback.
 */
fun MoPubInterstitial.loadAd(adResponse: AdResponse) {
  // MoPub needs a looper to be prepared because it is creating an handler used for timeout. We
  // don't need to actually loop it as we don't mind about the timeout.
  if (Looper.myLooper() == null) {
    Looper.prepare()
  }

  currentInterstitialState = LOADING
  getAdViewController()?.onAdLoadSuccess(adResponse)
}