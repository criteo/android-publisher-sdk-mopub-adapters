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
import android.webkit.WebView
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.criteo.mediation.mopub.MoPubHelper.*
import com.criteo.mediation.mopub.activity.DummyActivity
import com.criteo.publisher.CriteoBannerView
import com.criteo.publisher.CriteoUtil.*
import com.criteo.publisher.StubConstants.STUB_DISPLAY_URL
import com.criteo.publisher.adview.Redirection
import com.criteo.publisher.TestAdUnits.BANNER_320_50
import com.criteo.publisher.TestAdUnits.BANNER_UNKNOWN
import com.criteo.publisher.concurrent.ThreadingUtil.callOnMainThreadAndWait
import com.criteo.publisher.concurrent.ThreadingUtil.runOnMainThreadAndWait
import com.criteo.publisher.mock.MockedDependenciesRule
import com.criteo.publisher.mock.SpyBean
import com.criteo.publisher.model.BannerAdUnit
import com.criteo.publisher.util.CompletableFuture
import com.criteo.publisher.view.WebViewLookup
import com.criteo.publisher.view.lookForNonEmptyHtmlContent
import com.criteo.publisher.view.waitUntilLoaded
import com.mopub.mobileads.CustomEventBanner
import com.mopub.mobileads.MoPubErrorCode.NETWORK_NO_FILL
import com.mopub.mobileads.MoPubErrorCode.NO_FILL
import com.mopub.mobileads.MoPubView
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

class CriteoMopubBannerAdapterTest {

  @Rule
  @JvmField
  val mockedDependenciesRule = MockedDependenciesRule()

  @Rule
  @JvmField
  var scenarioRule: ActivityScenarioRule<DummyActivity> = ActivityScenarioRule(DummyActivity::class.java)

  private val webViewLookup = WebViewLookup()

  @SpyBean
  private lateinit var context: Context

  private lateinit var adapter: CriteoBannerAdapter

  private lateinit var adapterHelper: BannerAdapterHelper

  @SpyBean
  private lateinit var redirection: Redirection

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
    val expectedRedirection = URI.create("https://criteo.com/") // Returned by the Casper stub

    // When
    givenInitializedCriteo(adUnit)
    mockedDependenciesRule.waitForIdleState()

    val moPubView = callOnMainThreadAndWait { MoPubView(context) }
    moPubView.bannerAdListener = bannerListener
    moPubView.loadAd(adUnit)
    mockedDependenciesRule.waitForIdleState()

    // Then
    verify(bannerListener).onBannerLoaded(moPubView)

    val html = webViewLookup.lookForNonEmptyHtmlContent(moPubView).get()
    assertThat(html).containsPattern(STUB_DISPLAY_URL)

    moPubView.assertClickRedirectTo(expectedRedirection)
  }

  @Test
  fun loadBannerAd_GivenInvalidAdUnit_NotifyMoPubListenerForFailure() {
    // Given
    val adUnit = BANNER_UNKNOWN

    // When
    givenInitializedCriteo(adUnit)
    mockedDependenciesRule.waitForIdleState()

    val moPubView = callOnMainThreadAndWait { MoPubView(context) }
    moPubView.bannerAdListener = bannerListener
    moPubView.loadAd(adUnit)
    mockedDependenciesRule.waitForIdleState()

    // Then
    verify(bannerListener).onBannerFailed(moPubView, NO_FILL)
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
      verify(listener).onBannerFailed(NETWORK_NO_FILL)
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

  private fun MoPubView.assertClickRedirectTo(expectedRedirectionUri: URI) {
    clearInvocations(redirection)
    clearInvocations(bannerListener)

    // Deactivate the redirection
    doNothing().whenever(context).startActivity(any())

    val bannerView = webViewLookup.lookForWebViews(this)[0] as CriteoBannerView
    bannerView.simulateClickOnAd()
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

    verify(bannerListener, atLeastOnce()).onBannerClicked(this)
  }

  private fun WebView.simulateClickOnAd() {
    // TODO kind of duplication of [WebViewClicker#simulateClickOnAd]
    waitUntilLoaded()
    val isClickDone = CompletableFuture<Void>()

    // Simulate click via JavaScript
    runOnMainThreadAndWait {
      val javascript = """
          (function() {
            var elements = document.getElementsByTagName('a');
            if (elements.length != 1) {
              return false;
            }
            elements[0].click();
            return true;
          })();""".trimIndent()

      evaluateJavascript(javascript) { value: String ->
        if ("true" != value) {
          isClickDone.completeExceptionally(IllegalStateException("Clickable element was not found in the WebView"))
        } else {
          isClickDone.complete(null)
        }
      }
    }

    isClickDone.get()
  }
}