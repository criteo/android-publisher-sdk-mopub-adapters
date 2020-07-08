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

import android.content.ComponentName
import android.content.Context
import android.view.View
import android.webkit.WebView
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.criteo.mediation.mopub.MoPubHelper.*
import com.criteo.mediation.mopub.activity.DummyActivity
import com.criteo.publisher.CriteoUtil.*
import com.criteo.publisher.STUB_CLICK_URI
import com.criteo.publisher.StubConstants.STUB_CREATIVE_IMAGE
import com.criteo.publisher.TestAdUnits.INTERSTITIAL
import com.criteo.publisher.adview.Redirection
import com.criteo.publisher.mock.MockedDependenciesRule
import com.criteo.publisher.mock.SpyBean
import com.criteo.publisher.model.InterstitialAdUnit
import com.criteo.publisher.view.WebViewLookup
import com.criteo.publisher.view.WebViewLookup.getRootView
import com.criteo.publisher.view.lookForNonEmptyHtmlContent
import com.criteo.publisher.view.simulateClickOnAd
import com.mopub.mobileads.CustomEventInterstitial
import com.mopub.mobileads.MoPubErrorCode.NETWORK_NO_FILL
import com.mopub.mobileads.MoPubInterstitial
import com.mopub.mobileads.loadAd
import com.mopub.network.AdResponse
import com.nhaarman.mockitokotlin2.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.net.URI

class CriteoMopubInterstitialAdapterTest {

  @Rule
  @JvmField
  val mockedDependenciesRule = MockedDependenciesRule()

  @Rule
  @JvmField
  var scenarioRule: ActivityScenarioRule<DummyActivity> = ActivityScenarioRule(DummyActivity::class.java)

  private val webViewLookup = WebViewLookup()

  @SpyBean
  private lateinit var context: Context

  @SpyBean
  private lateinit var redirection: Redirection

  @Mock
  private lateinit var listener: CustomEventInterstitial.CustomEventInterstitialListener

  @Mock
  private lateinit var interstitialListener: MoPubInterstitial.InterstitialAdListener

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
  fun loadInterstitialAd_GivenValidAdUnit_CallCriteoAdapterAndNotifyMoPubListenerForSuccess() {
    // Given
    val adUnit = INTERSTITIAL

    // When
    givenInitializedCriteo(adUnit)
    mockedDependenciesRule.waitForIdleState()

    lateinit var moPubInterstitial: MoPubInterstitial
    scenarioRule.scenario.onActivity {
      moPubInterstitial = MoPubInterstitial(it, "a mopub adunit")
    }
    moPubInterstitial.interstitialAdListener = interstitialListener
    moPubInterstitial.loadAd(adUnit)
    mockedDependenciesRule.waitForIdleState()

    val activity = webViewLookup.lookForResumedActivity {
      moPubInterstitial.show()
    }.get()

    // Then
    verify(interstitialListener).onInterstitialLoaded(moPubInterstitial)
    verify(interstitialListener).onInterstitialShown(moPubInterstitial)

    val rootView = getRootView(activity)
    val html = webViewLookup.lookForNonEmptyHtmlContent(rootView).get()
    assertThat(html).contains(STUB_CREATIVE_IMAGE)

    rootView.assertClickRedirectTo(STUB_CLICK_URI)
    verify(interstitialListener, atLeastOnce()).onInterstitialClicked(moPubInterstitial)
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

  private fun MoPubInterstitial.loadAd(adUnit: InterstitialAdUnit) {
    val adResponse = givenMoPubResponseForCriteoAdapter(adUnit)
    loadAd(adResponse)
  }

  private fun givenMoPubResponseForCriteoAdapter(adUnit: InterstitialAdUnit): AdResponse {
    return AdResponse.Builder()
        .setCustomEventClassName(INTERSTITIAL_ADAPTER_CLASS_NAME)
        .setServerExtras(
            mapOf(
                CRITEO_PUBLISHER_ID to TEST_CP_ID,
                ADUNIT_ID to adUnit.adUnitId
            )
        )
        .build()
  }

  private fun View.assertClickRedirectTo(expectedRedirectionUri: URI) {
    clearInvocations(redirection)

    // Deactivate the redirection
    doNothing().whenever(this@CriteoMopubInterstitialAdapterTest.context).startActivity(any())

    val webView = webViewLookup.lookForWebViews(this)[0] as WebView
    webView.simulateClickOnAd()
    mockedDependenciesRule.waitForIdleState()

    var expectedComponentName: ComponentName? = null
    scenarioRule.scenario.onActivity {
      expectedComponentName = it.componentName
    }

    verify(redirection).redirect(
        eq(expectedRedirectionUri.toString()),
        eq(expectedComponentName),
        any()
    )
  }
}