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
import com.criteo.mediation.mopub.MoPubHelper.serverExtras
import com.criteo.publisher.CriteoUtil.clearCriteo
import com.criteo.publisher.CriteoUtil.givenInitializedCriteo
import com.criteo.publisher.TestAdUnits.INTERSTITIAL
import com.criteo.publisher.mock.MockedDependenciesRule
import com.mopub.mobileads.CustomEventInterstitial
import com.mopub.mobileads.MoPubErrorCode.*
import com.nhaarman.mockitokotlin2.inOrder
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import javax.inject.Inject

class CriteoMopubInterstitialAdapterTest {

  @Rule
  @JvmField
  val mockedDependenciesRule = MockedDependenciesRule()

  @Inject
  private lateinit var context: Context

  private val localExtras = mutableMapOf<String, Any>()

  @Mock
  private lateinit var listener: CustomEventInterstitial.CustomEventInterstitialListener

  private lateinit var adapter: CriteoInterstitialAdapter

  private lateinit var adapterHelper: InterstitialAdapterHelper

  @Before
  fun setUp() {
    MockitoAnnotations.initMocks(this)
    clearCriteo()
    adapter = CriteoInterstitialAdapter()
    adapterHelper = InterstitialAdapterHelper(adapter)
  }

  @Test
  fun requestInterstitialAdWithEmptyParameters() {
    val serverExtras = mapOf<String, String>()

    adapter.loadInterstitial(context, listener, localExtras, serverExtras)

    verify(listener).onInterstitialFailed(ADAPTER_CONFIGURATION_ERROR)
    verifyNoMoreInteractions(listener)
  }

  @Test
  fun requestInterstitialAdWithNullPublisherId() {
    val serverExtras = serverExtras(null, "myAdUnit")

    adapter.loadInterstitial(context, listener, localExtras, serverExtras)

    verify(listener).onInterstitialFailed(ADAPTER_CONFIGURATION_ERROR)
    verifyNoMoreInteractions(listener)
  }

  @Test
  fun requestBannerAdWithNullAdUnitId() {
    val serverExtras = serverExtras("cpId", null)

    adapter.loadInterstitial(context, listener, localExtras, serverExtras)

    verify(listener).onInterstitialFailed(MISSING_AD_UNIT_ID)
    verifyNoMoreInteractions(listener)
  }

  @Test
  fun givenNotInitializedCriteo_WhenLoadingInterstitialTwice_MissFirstOpportunityBecauseOfBidCachingAndSucceedOnNextOne() {
    loadValidInterstitial()
    loadValidInterstitial()

    checkMissFirstOpportunityBecauseOfBidCachingAndSucceedOnNextOne()
  }

  @Test
  fun givenInitializedCriteo_WhenLoadingInterstitialTwice_MissFirstOpportunityBecauseOfBidCachingAndSucceedOnNextOne() {
    givenInitializedCriteo()

    loadValidInterstitial()
    loadValidInterstitial()

    checkMissFirstOpportunityBecauseOfBidCachingAndSucceedOnNextOne()
  }

  private fun loadValidInterstitial() {
    adapterHelper.loadInterstitial(INTERSTITIAL, listener)
    mockedDependenciesRule.waitForIdleState()
  }

  private fun checkMissFirstOpportunityBecauseOfBidCachingAndSucceedOnNextOne() {
    inOrder(listener) {
      verify(listener).onInterstitialFailed(NETWORK_NO_FILL)
      verify(listener).onInterstitialLoaded()
      verifyNoMoreInteractions()
    }
  }
}