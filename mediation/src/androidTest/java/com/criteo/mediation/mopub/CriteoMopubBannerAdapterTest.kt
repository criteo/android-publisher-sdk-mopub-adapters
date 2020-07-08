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
import com.criteo.mediation.mopub.MoPubHelper.*
import com.criteo.publisher.CriteoBannerView
import com.criteo.publisher.CriteoUtil.*
import com.criteo.publisher.TestAdUnits.BANNER_320_50
import com.criteo.publisher.concurrent.ThreadingUtil.callOnMainThreadAndWait
import com.criteo.publisher.mock.MockedDependenciesRule
import com.criteo.publisher.model.BannerAdUnit
import com.mopub.mobileads.CustomEventBanner
import com.mopub.mobileads.MoPubErrorCode
import com.mopub.mobileads.MoPubView
import com.mopub.mobileads.loadAd
import com.mopub.network.AdResponse
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.inOrder
import com.nhaarman.mockitokotlin2.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import javax.inject.Inject

class CriteoMopubBannerAdapterTest {

  @Rule
  @JvmField
  val mockedDependenciesRule = MockedDependenciesRule()

  @Inject
  private lateinit var context: Context

  private lateinit var adapter: CriteoBannerAdapter

  private lateinit var adapterHelper: BannerAdapterHelper

  @Mock
  private lateinit var listener: CustomEventBanner.CustomEventBannerListener

  @Mock
  private lateinit var bannerListener: MoPubView.BannerAdListener

  @Before
  fun setUp() {
    MockitoAnnotations.initMocks(this)
    clearCriteo()
    adapter = CriteoBannerAdapter()
    adapterHelper = BannerAdapterHelper(adapter)
  }

  @Test
  fun loadBannerAd_GivenValidAdUnit_CallCriteoAdapterAndNotifyMoPubListenerForSuccess() {
    // Given
    val adUnit = BANNER_320_50

    // When
    givenInitializedCriteo(adUnit)
    mockedDependenciesRule.waitForIdleState()

    val moPubView = callOnMainThreadAndWait { MoPubView(context) }
    moPubView.bannerAdListener = bannerListener
    moPubView.loadAd(adUnit)
    mockedDependenciesRule.waitForIdleState()

    // Then
    verify(bannerListener).onBannerLoaded(moPubView)
  }

  @Test
  fun givenNotInitializedCriteo_WhenLoadingBannerTwice_MissFirstOpportunityBecauseOfBidCachingAndSucceedOnNextOne() {
    loadValidBanner()
    loadValidBanner()

    checkMissFirstOpportunityBecauseOfBidCachingAndSucceedOnNextOne()
  }

  @Test
  fun givenInitializedCriteo_WhenLoadingBannerTwice_MissFirstOpportunityBecauseOfBidCachingAndSucceedOnNextOne() {
    givenInitializedCriteo()

    loadValidBanner()
    loadValidBanner()

    checkMissFirstOpportunityBecauseOfBidCachingAndSucceedOnNextOne()
  }

  private fun loadValidBanner() {
    adapterHelper.loadBanner(BANNER_320_50, listener)
    mockedDependenciesRule.waitForIdleState()
  }

  private fun checkMissFirstOpportunityBecauseOfBidCachingAndSucceedOnNextOne() {
    inOrder(listener) {
      verify(listener).onBannerFailed(MoPubErrorCode.NETWORK_NO_FILL)
      verify(listener).onBannerLoaded(any<CriteoBannerView>())
      verifyNoMoreInteractions()
    }
  }

  private fun MoPubView.loadAd(adUnit: BannerAdUnit) {
    val adResponse = givenMoPubResponseForCriteoAdapter(adUnit)
    loadAd(adResponse)
  }

  private fun givenMoPubResponseForCriteoAdapter(adUnit: BannerAdUnit): AdResponse {
    return AdResponse.Builder()
        .setCustomEventClassName(BANNER_ADAPTER_CLASS_NAME)
        .setServerExtras(
            mapOf(
                CRITEO_PUBLISHER_ID to TEST_CP_ID,
                ADUNIT_ID to adUnit.adUnitId
            )
        )
        .setDimensions(adUnit.size.width, adUnit.size.height)
        .build()
  }
}