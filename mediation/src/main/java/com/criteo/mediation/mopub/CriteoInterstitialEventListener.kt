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

import com.criteo.mediation.mopub.ErrorCode.toMoPub
import com.criteo.publisher.CriteoErrorCode
import com.criteo.publisher.CriteoInterstitial
import com.criteo.publisher.CriteoInterstitialAdListener
import com.mopub.mobileads.AdLifecycleListener
import com.mopub.mobileads.MoPubErrorCode.NETWORK_TIMEOUT

class CriteoInterstitialEventListener(
    private val loadListener: AdLifecycleListener.LoadListener?,
    private val interactionListenerRef: () -> AdLifecycleListener.InteractionListener?
) : CriteoInterstitialAdListener {

  override fun onAdReceived(interstitial: CriteoInterstitial) {
    loadListener?.onAdLoaded()
  }

  override fun onAdFailedToReceive(code: CriteoErrorCode) {
    loadListener?.onAdLoadFailed(toMoPub(code))
  }

  override fun onAdOpened() {
    interactionListenerRef()?.onAdShown()
  }

  override fun onAdClosed() {
    interactionListenerRef()?.onAdDismissed()
  }

  override fun onAdClicked() {
    interactionListenerRef()?.onAdClicked()
  }

  override fun onAdLeftApplication() {}

}