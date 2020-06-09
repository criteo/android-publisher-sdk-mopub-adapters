package com.criteo.mediation.mopub.advancednative

import com.criteo.publisher.CriteoErrorCode
import com.criteo.publisher.advancednative.CriteoNativeAd
import com.mopub.nativeads.CustomEventNative.CustomEventNativeListener
import com.mopub.nativeads.NativeErrorCode
import com.nhaarman.mockitokotlin2.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class CriteoNativeEventListenerTest {

  @Mock
  private lateinit var moPubListener: CustomEventNativeListener

  private lateinit var listener: CriteoNativeEventListener

  @Before
  fun setUp() {
    MockitoAnnotations.initMocks(this)

    listener = CriteoNativeEventListener(moPubListener)
  }

  @Test
  fun onAdReceived_GivenAd_DelegateDecoratedAdToMoPubListener() {
    val nativeAd = mock<CriteoNativeAd>()

    listener.onAdReceived(nativeAd)

    verify(moPubListener).onNativeAdLoaded(check {
      assertThat(it).isInstanceOf(CriteoBaseNativeAd::class.java)
      assertThat((it as CriteoBaseNativeAd).nativeAd).isSameAs(nativeAd)
    })
  }

  @Test
  fun onAdFailedToReceive_GivenError_MapItAndDelegateToMoPubListener() {
    listener.onAdFailedToReceive(CriteoErrorCode.ERROR_CODE_NO_FILL)

    verify(moPubListener).onNativeAdFailed(NativeErrorCode.NETWORK_NO_FILL);
  }

  @Test
  fun onAdImpression_GivenPreviouslyLoadedNativeAd_NotifyIt() {
    val baseNativeAd = givenPreviouslyLoadedNativeAd()

    listener.onAdImpression()

    verify(baseNativeAd).onAdImpression()
  }

  @Test
  fun onAdImpression_GivenNoPreviouslyLoadedNativeAd_DoNothing() {
    listener.onAdImpression()

    verifyZeroInteractions(moPubListener)
  }

  @Test
  fun onAdClicked_GivenPreviouslyLoadedNativeAd_NotifyIt() {
    val baseNativeAd = givenPreviouslyLoadedNativeAd()

    listener.onAdClicked()

    verify(baseNativeAd).onAdClicked()
  }

  @Test
  fun onAdClicked_GivenNoPreviouslyLoadedNativeAd_DoNothing() {
    listener.onAdImpression()

    verifyZeroInteractions(moPubListener)
  }

  private fun givenPreviouslyLoadedNativeAd(): CriteoBaseNativeAd {
    listener.onAdReceived(mock())
    listener.baseNativeAd  = spy(listener.baseNativeAd)
    return listener.baseNativeAd!!
  }

}