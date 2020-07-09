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
import com.criteo.publisher.CriteoBannerView
import com.criteo.publisher.model.AdSize
import com.criteo.publisher.model.BannerAdUnit
import com.mopub.common.VisibleForTesting
import com.mopub.common.logging.MoPubLog.AdapterLogEvent.LOAD_ATTEMPTED
import com.mopub.common.logging.MoPubLog.AdapterLogEvent.LOAD_FAILED
import com.mopub.mobileads.CustomEventBanner
import com.mopub.mobileads.MoPubErrorCode

class CriteoBannerAdapter @VisibleForTesting internal constructor(
    private val criteoInitializer: CriteoInitializer
) : CustomEventBanner() {

  private var bannerView: CriteoBannerView? = null

  constructor() : this(CriteoInitializer())

  public override fun loadBanner(
      context: Context,
      customEventBannerListener: CustomEventBannerListener,
      localExtras: Map<String, Any>?,
      serverExtras: Map<String, String>?
  ) {
    if (localExtras == null
        || localExtras.isEmpty()
        || serverExtras == null
        || serverExtras.isEmpty()) {
      MoPubLog.log(LOAD_FAILED, TAG, "Server parameters are empty")
      customEventBannerListener.onBannerFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR)
      return
    }

    val adSize = getAdSize(localExtras)
    val criteoPublisherId = serverExtras[CRITEO_PUBLISHER_ID]
    if (adSize == null || criteoPublisherId == null) {
      MoPubLog.log(LOAD_FAILED, TAG, "CriteoPublisherId cannot be null")
      customEventBannerListener.onBannerFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR)
      return
    }

    val adUnitId = serverExtras[ADUNIT_ID]
    if (adUnitId == null) {
      MoPubLog.log(LOAD_FAILED, TAG, "Missing adUnit Id")
      customEventBannerListener.onBannerFailed(MoPubErrorCode.MISSING_AD_UNIT_ID)
      return
    }

    criteoInitializer.init(context, criteoPublisherId)

    try {
      val bannerAdUnit = BannerAdUnit(adUnitId, adSize)
      val listener = CriteoBannerEventListener(customEventBannerListener)
      bannerView = CriteoBannerView(context, bannerAdUnit).apply {
        setCriteoBannerAdListener(listener)
        loadAd()
      }
      MoPubLog.log(LOAD_ATTEMPTED, TAG, "BannerView is loading")
    } catch (e: Exception) {
      MoPubLog.log(LOAD_FAILED, TAG, "Initialization failed")
      customEventBannerListener.onBannerFailed(MoPubErrorCode.INTERNAL_ERROR)
    }
  }

  override fun onInvalidate() {
    bannerView?.destroy()
  }

  private fun getAdSize(localExtras: Map<String, Any>): AdSize? {
    val objHeight = localExtras[MOPUB_HEIGHT]
    val objWidth = localExtras[MOPUB_WIDTH]
    if (objHeight == null || objWidth == null) {
      return null
    }
    val height = objHeight as Int
    val width = objWidth as Int
    return AdSize(width, height)
  }

  companion object {
    private val TAG = CriteoBannerAdapter::class.java.simpleName
    const val ADUNIT_ID = "adUnitId"
    const val CRITEO_PUBLISHER_ID = "cpId"
    const val MOPUB_WIDTH = "com_mopub_ad_width"
    const val MOPUB_HEIGHT = "com_mopub_ad_height"
  }

}