package com.criteo.mediation.mopub.advancednative

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.criteo.mediation.mopub.MoPubHelper.ADUNIT_ID
import com.criteo.mediation.mopub.MoPubHelper.CRITEO_PUBLISHER_ID
import com.criteo.mediation.mopub.advancednative.TestNativeRenderer.Companion.ADVERTISER_DESCRIPTION_TAG
import com.criteo.mediation.mopub.advancednative.TestNativeRenderer.Companion.ADVERTISER_DOMAIN_TAG
import com.criteo.mediation.mopub.advancednative.TestNativeRenderer.Companion.ADVERTISER_LOGO_TAG
import com.criteo.mediation.mopub.advancednative.TestNativeRenderer.Companion.CALL_TO_ACTION_TAG
import com.criteo.mediation.mopub.advancednative.TestNativeRenderer.Companion.DESCRIPTION_TAG
import com.criteo.mediation.mopub.advancednative.TestNativeRenderer.Companion.PRICE_TAG
import com.criteo.mediation.mopub.advancednative.TestNativeRenderer.Companion.PRODUCT_IMAGE_TAG
import com.criteo.mediation.mopub.advancednative.TestNativeRenderer.Companion.TITLE_TAG
import com.criteo.publisher.CriteoUtil.TEST_CP_ID
import com.criteo.publisher.CriteoUtil.givenInitializedCriteo
import com.criteo.publisher.StubConstants
import com.criteo.publisher.TestAdUnits
import com.criteo.publisher.advancednative.CriteoMediaView
import com.criteo.publisher.advancednative.drawable
import com.criteo.publisher.concurrent.ThreadingUtil.runOnMainThreadAndWait
import com.criteo.publisher.mock.MockedDependenciesRule
import com.mopub.nativeads.AdapterHelper
import com.mopub.nativeads.MoPubNative
import com.mopub.nativeads.MoPubNative.MoPubNativeNetworkListener
import com.mopub.nativeads.NativeAd
import com.mopub.network.AdResponse
import com.nhaarman.mockitokotlin2.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

class CriteoNativeAdapterTest {

  @Rule
  @JvmField
  val mockedDependenciesRule = MockedDependenciesRule()

  @Inject
  private lateinit var context: Context

  @Test
  fun loadNativeAd_GivenValidBid_RenderAllNativePayload() {
    // Given
    val expectedAssets = StubConstants.STUB_NATIVE_ASSETS
    val expectedProduct = expectedAssets.product
    val adUnit = TestAdUnits.NATIVE
    val parentView = mock<ViewGroup>()
    val placeholder = context.getDrawable(android.R.drawable.ic_delete)!!
    val nativeRenderer = spy(TestNativeRenderer(placeholder))
    val nativeNetworkListener = mock<MoPubNativeNetworkListener>()

    val adResponse = AdResponse.Builder()
        .setCustomEventClassName("com.criteo.mediation.mopub.advancednative.CriteoNativeAdapter")
        .setServerExtras(mapOf(
            CRITEO_PUBLISHER_ID to TEST_CP_ID,
            ADUNIT_ID to adUnit.adUnitId
        ))
        .build()

    // When
    givenInitializedCriteo(adUnit)
    mockedDependenciesRule.waitForIdleState()

    val nativeEventRenderer = CriteoNativeEventRenderer(nativeRenderer)
    val moPubNative = MoPubNative(context, adUnit.adUnitId, nativeNetworkListener)
    moPubNative.registerAdRenderer(nativeEventRenderer)
    moPubNative.loadAd(adResponse)
    mockedDependenciesRule.waitForIdleState()

    // Then
    val nativeAd = nativeNetworkListener.lastNativeAd()
    val adView = nativeAd.getAdView(parentView)

    verify(nativeRenderer).createNativeView(context, parentView)
    verify(nativeRenderer).renderNativeView(any(), any(), any())

    assertThat(adView.findTextWithTag(TITLE_TAG)).isEqualTo(expectedProduct.title)
    assertThat(adView.findTextWithTag(DESCRIPTION_TAG)).isEqualTo(expectedProduct.description)
    assertThat(adView.findTextWithTag(PRICE_TAG)).isEqualTo(expectedProduct.price)
    assertThat(adView.findTextWithTag(CALL_TO_ACTION_TAG)).isEqualTo(expectedProduct.callToAction)
    assertThat(adView.findTextWithTag(ADVERTISER_DOMAIN_TAG)).isEqualTo(expectedAssets.advertiserDomain)
    assertThat(adView.findTextWithTag(ADVERTISER_DESCRIPTION_TAG)).isEqualTo(expectedAssets.advertiserDescription)

    // Image
    assertThat(adView.findDrawableWithTag(PRODUCT_IMAGE_TAG)).isNotNull.isNotEqualTo(placeholder)
    assertThat(adView.findDrawableWithTag(ADVERTISER_LOGO_TAG)).isEqualTo(placeholder)

    // TODO AdChoice
  }

  // TODO Click
  // TODO Impression

  private fun MoPubNative.loadAd(adResponse: AdResponse) {
    val method = javaClass.getDeclaredMethod("onAdLoad", AdResponse::class.java)
    method.isAccessible = true
    runOnMainThreadAndWait {
      method.invoke(this, adResponse)
    }
  }

  private fun MoPubNativeNetworkListener.lastNativeAd(): NativeAd {
    val argumentCaptor = argumentCaptor<NativeAd>()
    verify(this).onNativeLoad(argumentCaptor.capture())
    return argumentCaptor.lastValue
  }

  private fun NativeAd.getAdView(parent: ViewGroup? = null): View {
    val adapterHelper = AdapterHelper(context, 0, 2)
    return adapterHelper.getAdView(null, parent, this)
  }

  private fun View.findTextWithTag(tag: Any): CharSequence {
    return findViewWithTag<TextView>(tag).text
  }

  private fun View.findDrawableWithTag(tag: Any): Drawable {
    return findViewWithTag<CriteoMediaView>(tag).drawable
  }

}