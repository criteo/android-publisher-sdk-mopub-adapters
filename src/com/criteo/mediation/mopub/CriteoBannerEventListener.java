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
