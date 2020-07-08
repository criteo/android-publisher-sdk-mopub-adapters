package com.mopub.mobileads

import com.mopub.mobileads.MoPubInterstitial.InterstitialState.LOADING
import com.mopub.network.AdResponse

/**
 * Mock the job done by the [MoPubInterstitial.load] method.
 *
 * Instead of calling the MoPub server, it uses a forged [AdResponse] corresponding to the desired
 * ad and invoke the success callback.
 */
fun MoPubInterstitial.loadAd(adResponse: AdResponse) {
  currentInterstitialState = LOADING
  moPubInterstitialView.loadAd(adResponse)
}