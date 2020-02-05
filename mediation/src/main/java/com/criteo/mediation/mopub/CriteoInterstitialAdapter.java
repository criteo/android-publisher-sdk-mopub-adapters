package com.criteo.mediation.mopub;

import static com.mopub.common.logging.MoPubLog.AdapterLogEvent.LOAD_ATTEMPTED;
import static com.mopub.common.logging.MoPubLog.AdapterLogEvent.LOAD_FAILED;

import android.content.Context;
import android.support.annotation.NonNull;
import com.criteo.publisher.CriteoInterstitial;
import com.criteo.publisher.model.InterstitialAdUnit;
import com.mopub.common.VisibleForTesting;
import com.mopub.common.logging.MoPubLog;
import com.mopub.mobileads.CustomEventInterstitial;
import com.mopub.mobileads.MoPubErrorCode;
import java.util.Map;

public class CriteoInterstitialAdapter extends CustomEventInterstitial {

    private static final String TAG = CriteoInterstitialAdapter.class.getSimpleName();
    protected static final String ADUNIT_ID = "adUnitId";
    protected static final String CRITEO_PUBLISHER_ID = "cpId";
    private CriteoInterstitial criteoInterstitial;

    private final CriteoInitializer criteoInitializer;

    public CriteoInterstitialAdapter() {
        this(new CriteoInitializer());
    }

    @VisibleForTesting
    CriteoInterstitialAdapter(@NonNull CriteoInitializer criteoInitializer) {
        this.criteoInitializer = criteoInitializer;
    }

    @Override
    protected void loadInterstitial(Context context, CustomEventInterstitialListener customEventInterstitialListener,
            Map<String, Object> localExtras, Map<String, String> serverExtras) {

        if (serverExtras == null || serverExtras.isEmpty()) {
            MoPubLog.log(LOAD_FAILED, TAG, "Server parameters are empty");
            customEventInterstitialListener.onInterstitialFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
            return;
        }

        String criteoPublisherId = serverExtras.get(CRITEO_PUBLISHER_ID);

        if (criteoPublisherId == null) {
            MoPubLog.log(LOAD_FAILED, TAG, "CriteoPublisherId cannot be null");
            customEventInterstitialListener.onInterstitialFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
            return;
        }

        String adUnitId = serverExtras.get(ADUNIT_ID);

        if (adUnitId == null) {
            MoPubLog.log(LOAD_FAILED, TAG, "Missing adunit Id");
            customEventInterstitialListener.onInterstitialFailed(MoPubErrorCode.MISSING_AD_UNIT_ID);
            return;
        }

        criteoInitializer.init(context, criteoPublisherId);

        try {
            InterstitialAdUnit interstitialAdUnit = new InterstitialAdUnit(adUnitId);
            criteoInterstitial = new CriteoInterstitial(context, interstitialAdUnit);
            CriteoInterstitialEventListener listener = new CriteoInterstitialEventListener(
                    customEventInterstitialListener);
            criteoInterstitial.setCriteoInterstitialAdListener(listener);
            criteoInterstitial.setCriteoInterstitialAdDisplayListener(listener);
            criteoInterstitial.loadAd();
            MoPubLog.log(LOAD_ATTEMPTED, TAG, "Criteo Interstitial is loading");
        } catch (Exception e) {
            MoPubLog.log(LOAD_FAILED, TAG, "Initialization failed");
            customEventInterstitialListener.onInterstitialFailed(MoPubErrorCode.INTERNAL_ERROR);
        }
    }

    @Override
    protected void showInterstitial() {
        if (criteoInterstitial != null) {
            criteoInterstitial.show();
        }
    }

    @Override
    protected void onInvalidate() {

    }
}
