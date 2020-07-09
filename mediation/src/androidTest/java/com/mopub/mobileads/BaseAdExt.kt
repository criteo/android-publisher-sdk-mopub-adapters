package com.mopub.mobileads

import android.content.Context

/**
 * Wrapper method that brings visibility on the internal method [BaseAd.internalLoad] which is
 * package-private.
 */
fun BaseAd.loadAd(
    context: Context,
    loadListener: AdLifecycleListener.LoadListener,
    adData: AdData
) {
  internalLoad(context, loadListener, adData)
}