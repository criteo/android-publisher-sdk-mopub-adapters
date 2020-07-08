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
import androidx.test.core.app.ApplicationProvider
import com.criteo.mediation.mopub.MoPubHelper.localExtras
import com.criteo.mediation.mopub.MoPubHelper.serverExtras
import com.criteo.publisher.CriteoBannerView
import com.criteo.publisher.CriteoUtil
import com.criteo.publisher.CriteoUtil.clearCriteo
import com.criteo.publisher.CriteoUtil.givenInitializedCriteo
import com.criteo.publisher.TestAdUnits
import com.criteo.publisher.mock.MockedDependenciesRule
import com.mopub.mobileads.CustomEventBanner
import com.mopub.mobileads.MoPubErrorCode
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.inOrder
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import java.util.*
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

  @Before
  fun setUp() {
    MockitoAnnotations.initMocks(this)
    clearCriteo()
    adapter = CriteoBannerAdapter()
    adapterHelper = BannerAdapterHelper(adapter)
  }

  @Test
  fun requestBannerAdWithEmptyParameters() {
    val serverExtras = mapOf<String, String>()
    val localExtras = mapOf<String, Any>()

    adapter.loadBanner(context, listener, localExtras, serverExtras)

    verify(listener).onBannerFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR)
    verifyNoMoreInteractions(listener)
  }

  @Test
  fun requestBannerAdWithNullAdSize() {
    val serverExtras = serverExtras("cpId", "myAdUnit")
    val localExtras = mapOf<String, Any>()

    adapter.loadBanner(context, listener, localExtras, serverExtras)

    verify(listener).onBannerFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR)
    verifyNoMoreInteractions(listener)
  }

  @Test
  fun requestBannerAdWithNullPublisherId() {
    val serverExtras = serverExtras(null, "myAdUnit")
    val localExtras = localExtras(320, 50)

    adapter.loadBanner(context, listener, localExtras, serverExtras)
    verify(listener).onBannerFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR)
    verifyNoMoreInteractions(listener)
  }

  @Test
  fun requestBannerAdWithNullAdUnitId() {
    val serverExtras = serverExtras("cpId", null)
    val localExtras = localExtras(320, 50)

    adapter.loadBanner(context, listener, localExtras, serverExtras)

    verify(listener).onBannerFailed(MoPubErrorCode.MISSING_AD_UNIT_ID)
    verifyNoMoreInteractions(listener)
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
    adapterHelper.loadBanner(TestAdUnits.BANNER_320_50, listener)
    mockedDependenciesRule.waitForIdleState()
  }

  private fun checkMissFirstOpportunityBecauseOfBidCachingAndSucceedOnNextOne() {
    inOrder(listener) {
      verify(listener).onBannerFailed(MoPubErrorCode.NETWORK_NO_FILL)
      verify(listener).onBannerLoaded(any<CriteoBannerView>())
      verifyNoMoreInteractions()
    }
  }
}