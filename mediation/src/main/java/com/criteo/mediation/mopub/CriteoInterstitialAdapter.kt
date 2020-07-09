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

import android.app.Activity
import android.content.Context
import com.criteo.publisher.CriteoInterstitial
import com.criteo.publisher.model.InterstitialAdUnit
import com.mopub.common.LifecycleListener
import com.mopub.common.VisibleForTesting
import com.mopub.common.logging.MoPubLog.AdapterLogEvent.LOAD_ATTEMPTED
import com.mopub.common.logging.MoPubLog.AdapterLogEvent.LOAD_FAILED
import com.mopub.mobileads.AdData
import com.mopub.mobileads.BaseAd
import com.mopub.mobileads.MoPubErrorCode.*

class CriteoInterstitialAdapter @VisibleForTesting internal constructor(
    private val criteoInitializer: CriteoInitializer
) : BaseAd() {

  private var criteoInterstitial: CriteoInterstitial? = null

  constructor() : this(CriteoInitializer())

  override fun getLifecycleListener(): LifecycleListener? = null
  override fun getAdNetworkId() = ""
  override fun checkAndInitializeSdk(launcherActivity: Activity, adData: AdData) = false

  override fun load(context: Context, adData: AdData) {
    val serverExtras = adData.extras
    if (serverExtras.isEmpty()) {
      MoPubLog.log(LOAD_FAILED, TAG, "Server parameters are empty")
      mLoadListener.onAdLoadFailed(ADAPTER_CONFIGURATION_ERROR)
      return
    }

    val criteoPublisherId = serverExtras[CRITEO_PUBLISHER_ID]
    if (criteoPublisherId == null) {
      MoPubLog.log(LOAD_FAILED, TAG, "CriteoPublisherId cannot be null")
      mLoadListener.onAdLoadFailed(ADAPTER_CONFIGURATION_ERROR)
      return
    }

    val adUnitId = serverExtras[ADUNIT_ID]
    if (adUnitId == null) {
      MoPubLog.log(LOAD_FAILED, TAG, "Missing adunit Id")
      mLoadListener.onAdLoadFailed(MISSING_AD_UNIT_ID)
      return
    }

    criteoInitializer.init(context, criteoPublisherId)

    try {
      val interstitialAdUnit = InterstitialAdUnit(adUnitId)
      val listener = CriteoInterstitialEventListener(mLoadListener) { mInteractionListener }
      criteoInterstitial = CriteoInterstitial(context, interstitialAdUnit).apply {
        setCriteoInterstitialAdListener(listener)
        setCriteoInterstitialAdDisplayListener(listener)
        loadAd()
      }
      MoPubLog.log(LOAD_ATTEMPTED, TAG, "Criteo Interstitial is loading")
    } catch (e: Exception) {
      MoPubLog.log(LOAD_FAILED, TAG, "Initialization failed")
      mLoadListener.onAdLoadFailed(INTERNAL_ERROR)
    }
  }

  override fun show() {
    criteoInterstitial?.show()
  }

  override fun onInvalidate() {}

  companion object {
    private val TAG = CriteoInterstitialAdapter::class.java.simpleName
    private const val ADUNIT_ID = "adUnitId"
    private const val CRITEO_PUBLISHER_ID = "cpId"
  }

}