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

package com.criteo.mediation.mopub;

import androidx.annotation.NonNull;
import com.criteo.publisher.CriteoErrorCode;
import com.criteo.publisher.CriteoInterstitialAdDisplayListener;
import com.criteo.publisher.CriteoInterstitialAdListener;
import com.mopub.mobileads.CustomEventInterstitial.CustomEventInterstitialListener;
import com.mopub.mobileads.MoPubErrorCode;

public class CriteoInterstitialEventListener implements CriteoInterstitialAdListener,
        CriteoInterstitialAdDisplayListener {

    @NonNull
    private final CustomEventInterstitialListener customEventInterstitialListener;

    public CriteoInterstitialEventListener(@NonNull CustomEventInterstitialListener listener) {
        customEventInterstitialListener = listener;
    }

    @Override
    public void onAdOpened() {
        customEventInterstitialListener.onInterstitialShown();
    }

    @Override
    public void onAdClosed() {
        customEventInterstitialListener.onInterstitialDismissed();
    }

    @Override
    public void onAdFailedToReceive(CriteoErrorCode code) {
        customEventInterstitialListener.onInterstitialFailed(ErrorCode.toMoPub(code));
    }

    @Override
    public void onAdLeftApplication() {
        customEventInterstitialListener.onLeaveApplication();
    }

    @Override
    public void onAdClicked() {
        customEventInterstitialListener.onInterstitialClicked();
    }

    @Override
    public void onAdReadyToDisplay() {
        customEventInterstitialListener.onInterstitialLoaded();
    }

    @Override
    public void onAdFailedToDisplay(CriteoErrorCode code) {
        customEventInterstitialListener.onInterstitialFailed(MoPubErrorCode.NETWORK_TIMEOUT);
    }

    @Override
    public void onAdReceived() {
    }

}
