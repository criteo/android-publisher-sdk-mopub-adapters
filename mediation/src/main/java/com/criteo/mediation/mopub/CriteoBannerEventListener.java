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

import android.view.View;
import androidx.annotation.NonNull;
import com.criteo.publisher.CriteoBannerAdListener;
import com.criteo.publisher.CriteoErrorCode;
import com.mopub.mobileads.CustomEventBanner.CustomEventBannerListener;

public class CriteoBannerEventListener implements CriteoBannerAdListener {

    @NonNull
    private final CustomEventBannerListener customEventBannerListener;

    public CriteoBannerEventListener(@NonNull CustomEventBannerListener listener) {
        customEventBannerListener = listener;
    }

    @Override
    public void onAdReceived(View view) {
        customEventBannerListener.onBannerLoaded(view);
    }

    @Override
    public void onAdFailedToReceive(CriteoErrorCode code) {
        customEventBannerListener.onBannerFailed(ErrorCode.toMoPub(code));
    }

    @Override
    public void onAdLeftApplication() {
        customEventBannerListener.onLeaveApplication();
    }

    @Override
    public void onAdClicked() {
        customEventBannerListener.onBannerClicked();
    }

    @Override
    public void onAdOpened() {
    }

    @Override
    public void onAdClosed() {
    }
}
