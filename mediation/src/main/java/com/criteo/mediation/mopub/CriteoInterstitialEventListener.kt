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
import com.criteo.publisher.CriteoInterstitialAdDisplayListener
import com.criteo.publisher.CriteoInterstitialAdListener
import com.mopub.mobileads.CustomEventInterstitial
import com.mopub.mobileads.MoPubErrorCode.NETWORK_TIMEOUT

class CriteoInterstitialEventListener(private val listener: CustomEventInterstitial.CustomEventInterstitialListener) :
    CriteoInterstitialAdListener,
    CriteoInterstitialAdDisplayListener {

  override fun onAdOpened() {
    listener.onInterstitialShown()
  }

  override fun onAdClosed() {
    listener.onInterstitialDismissed()
  }

  override fun onAdFailedToReceive(code: CriteoErrorCode) {
    listener.onInterstitialFailed(toMoPub(code))
  }

  override fun onAdLeftApplication() {
    listener.onLeaveApplication()
  }

  override fun onAdClicked() {
    listener.onInterstitialClicked()
  }

  override fun onAdReadyToDisplay() {
    listener.onInterstitialLoaded()
  }

  override fun onAdFailedToDisplay(code: CriteoErrorCode) {
    listener.onInterstitialFailed(NETWORK_TIMEOUT)
  }

  override fun onAdReceived() {}

}