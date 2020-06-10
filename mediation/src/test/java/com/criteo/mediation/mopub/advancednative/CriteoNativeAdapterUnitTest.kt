package com.criteo.mediation.mopub.advancednative

import com.criteo.mediation.mopub.CriteoInitializer
import com.mopub.nativeads.CustomEventNative.CustomEventNativeListener
import com.mopub.nativeads.NativeErrorCode
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class CriteoNativeAdapterUnitTest {

  private companion object {
    const val AD_UNIT_ID = "adUnitId"
    const val CP_ID = "cpId"
  }

  @Mock
  private lateinit var criteoInitializer: CriteoInitializer

  @Mock
  private lateinit var listener: CustomEventNativeListener

  private lateinit var adapter: CriteoNativeAdapter

  @Before
  fun setUp() {
    MockitoAnnotations.initMocks(this)
    adapter = CriteoNativeAdapter(criteoInitializer)
  }

  @Test
  fun loadNativeAd_GivenMissingCpId_NotifyListenerForFailureAndStop() {
    val serverExtras = mapOf(AD_UNIT_ID to "myAdUnit")

    adapter.loadNativeAd(mock(), listener, mapOf(), serverExtras)

    verify(listener).onNativeAdFailed(NativeErrorCode.NATIVE_ADAPTER_CONFIGURATION_ERROR)
    verify(criteoInitializer, never()).init(any(), any())
  }

  @Test
  fun loadNativeAd_GivenMissingAdUnitId_NotifyListenerForFailureAndStop() {
    val serverExtras = mapOf(CP_ID to "myCpId")

    adapter.loadNativeAd(mock(), listener, mapOf(), serverExtras)

    verify(listener).onNativeAdFailed(NativeErrorCode.NATIVE_ADAPTER_CONFIGURATION_ERROR)
    verify(criteoInitializer, never()).init(any(), any())
  }

}