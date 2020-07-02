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