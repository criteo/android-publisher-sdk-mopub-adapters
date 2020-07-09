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

import android.view.View
import com.criteo.publisher.CriteoBannerAdListener
import com.criteo.publisher.CriteoErrorCode
import com.mopub.mobileads.CustomEventBanner

class CriteoBannerEventListener(private val listener: CustomEventBanner.CustomEventBannerListener) :
    CriteoBannerAdListener {

  override fun onAdReceived(view: View) {
    listener.onBannerLoaded(view)
  }

  override fun onAdFailedToReceive(code: CriteoErrorCode) {
    listener.onBannerFailed(ErrorCode.toMoPub(code))
  }

  override fun onAdLeftApplication() {
    listener.onLeaveApplication()
  }

  override fun onAdClicked() {
    listener.onBannerClicked()
  }

  override fun onAdOpened() {}
  override fun onAdClosed() {}

}