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
