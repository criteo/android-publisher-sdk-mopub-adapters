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
package com.criteo.mediation.mopub

import android.content.Context
import com.criteo.publisher.CriteoInterstitial
import com.criteo.publisher.model.InterstitialAdUnit
import com.mopub.common.VisibleForTesting
import com.mopub.common.logging.MoPubLog.AdapterLogEvent.LOAD_ATTEMPTED
import com.mopub.common.logging.MoPubLog.AdapterLogEvent.LOAD_FAILED
import com.mopub.mobileads.CustomEventInterstitial
import com.mopub.mobileads.MoPubErrorCode

class CriteoInterstitialAdapter @VisibleForTesting internal constructor(
    private val criteoInitializer: CriteoInitializer
) : CustomEventInterstitial() {

  private var criteoInterstitial: CriteoInterstitial? = null

  constructor() : this(CriteoInitializer())

  public override fun loadInterstitial(
      context: Context,
      customEventInterstitialListener: CustomEventInterstitialListener,
      localExtras: Map<String, Any>,
      serverExtras: Map<String, String>?
  ) {
    if (serverExtras == null || serverExtras.isEmpty()) {
      MoPubLog.log(LOAD_FAILED, TAG, "Server parameters are empty")
      customEventInterstitialListener.onInterstitialFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR)
      return
    }

    val criteoPublisherId = serverExtras[CRITEO_PUBLISHER_ID]
    if (criteoPublisherId == null) {
      MoPubLog.log(LOAD_FAILED, TAG, "CriteoPublisherId cannot be null")
      customEventInterstitialListener.onInterstitialFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR)
      return
    }

    val adUnitId = serverExtras[ADUNIT_ID]
    if (adUnitId == null) {
      MoPubLog.log(LOAD_FAILED, TAG, "Missing adunit Id")
      customEventInterstitialListener.onInterstitialFailed(MoPubErrorCode.MISSING_AD_UNIT_ID)
      return
    }

    criteoInitializer.init(context, criteoPublisherId)

    try {
      val interstitialAdUnit = InterstitialAdUnit(adUnitId)
      val listener = CriteoInterstitialEventListener(customEventInterstitialListener)
      criteoInterstitial = CriteoInterstitial(context, interstitialAdUnit).apply {
        setCriteoInterstitialAdListener(listener)
        setCriteoInterstitialAdDisplayListener(listener)
        loadAd()
      }
      MoPubLog.log(LOAD_ATTEMPTED, TAG, "Criteo Interstitial is loading")
    } catch (e: Exception) {
      MoPubLog.log(LOAD_FAILED, TAG, "Initialization failed")
      customEventInterstitialListener.onInterstitialFailed(MoPubErrorCode.INTERNAL_ERROR)
    }
  }

  override fun showInterstitial() {
    criteoInterstitial?.show()
  }

  override fun onInvalidate() {

  }

  companion object {
    private val TAG = CriteoInterstitialAdapter::class.java.simpleName
    private const val ADUNIT_ID = "adUnitId"
    private const val CRITEO_PUBLISHER_ID = "cpId"
  }

}