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
import android.os.Handler
import android.os.Looper
import com.criteo.publisher.CriteoBannerView
import com.criteo.publisher.model.AdSize
import com.criteo.publisher.model.BannerAdUnit
import com.mopub.common.LifecycleListener
import com.mopub.common.VisibleForTesting
import com.mopub.common.logging.MoPubLog.AdapterLogEvent.LOAD_ATTEMPTED
import com.mopub.common.logging.MoPubLog.AdapterLogEvent.LOAD_FAILED
import com.mopub.mobileads.AdData
import com.mopub.mobileads.BaseAd
import com.mopub.mobileads.MoPubErrorCode.*

class CriteoBannerAdapter @VisibleForTesting internal constructor(
    private val criteoInitializer: CriteoInitializer
) : BaseAd() {

  private var bannerView: CriteoBannerView? = null

  constructor() : this(CriteoInitializer())

  override fun getLifecycleListener(): LifecycleListener? = null
  override fun getAdNetworkId() = ""
  override fun checkAndInitializeSdk(launcherActivity: Activity, adData: AdData) = false
  override fun getAdView() = bannerView

  override fun load(context: Context, adData: AdData) {
    val serverExtras = adData.extras
    if (serverExtras.isEmpty()) {
      MoPubLog.log(LOAD_FAILED, TAG, "Server parameters are empty")
      mLoadListener.onAdLoadFailed(ADAPTER_CONFIGURATION_ERROR)
      return
    }

    val adSize = getAdSize(adData)
    val criteoPublisherId = serverExtras[CRITEO_PUBLISHER_ID]
    if (adSize == null || criteoPublisherId == null) {
      MoPubLog.log(LOAD_FAILED, TAG, "CriteoPublisherId cannot be null")
      mLoadListener.onAdLoadFailed(ADAPTER_CONFIGURATION_ERROR)
      return
    }

    val adUnitId = serverExtras[ADUNIT_ID]
    if (adUnitId == null) {
      MoPubLog.log(LOAD_FAILED, TAG, "Missing adUnit Id")
      mLoadListener.onAdLoadFailed(MISSING_AD_UNIT_ID)
      return
    }

    criteoInitializer.init(context, criteoPublisherId)

    try {
      val bannerAdUnit = BannerAdUnit(adUnitId, adSize)
      val listener = CriteoBannerEventListener(mLoadListener) { mInteractionListener }

      ThreadingUtil.instance.runOnUiThread {
        bannerView = CriteoBannerView(context, bannerAdUnit).apply {
          setCriteoBannerAdListener(listener)
          loadAd()
        }

        MoPubLog.log(LOAD_ATTEMPTED, TAG, "BannerView is loading")
      }
    } catch (e: Exception) {
      MoPubLog.log(LOAD_FAILED, TAG, "Initialization failed")
      mLoadListener.onAdLoadFailed(INTERNAL_ERROR)
    }
  }

  override fun onInvalidate() {
    bannerView?.destroy()
  }

  private fun getAdSize(adData: AdData): AdSize? {
    val width = adData.adWidth
    val height = adData.adHeight
    if (width == null || height == null) {
      return null
    }
    return AdSize(width, height)
  }

  companion object {
    private val TAG = CriteoBannerAdapter::class.java.simpleName
    const val ADUNIT_ID = "adUnitId"
    const val CRITEO_PUBLISHER_ID = "cpId"
  }

}