package com.criteo.mediation.mopub;


import static com.mopub.common.logging.MoPubLog.AdapterLogEvent.LOAD_ATTEMPTED;
import static com.mopub.common.logging.MoPubLog.AdapterLogEvent.LOAD_FAILED;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import com.criteo.publisher.Criteo;
import com.criteo.publisher.CriteoInitException;
import com.criteo.publisher.CriteoInterstitial;
import com.criteo.publisher.model.AdUnit;
import com.criteo.publisher.model.InterstitialAdUnit;
import com.mopub.common.logging.MoPubLog;
import com.mopub.mobileads.CustomEventInterstitial;
import com.mopub.mobileads.MoPubErrorCode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CriteoInterstitialAdapter extends CustomEventInterstitial {

    private static final String TAG = CriteoInterstitialAdapter.class.getSimpleName();
    protected static final String ADUNIT_ID = "adUnitId";
    protected static final String CRITEO_PUBLISHER_ID = "cpId";
    private CriteoInterstitial criteoInterstitial;

    @Override
    protected void loadInterstitial(Context context, CustomEventInterstitialListener customEventInterstitialListener,
            Map<String, Object> localExtras, Map<String, String> serverExtras) {

        if (TextUtils.isEmpty(serverExtras.toString())) {
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

        try {
            Criteo.init((Application) (context.getApplicationContext()), criteoPublisherId,
                Collections.<AdUnit>emptyList());
        } catch (CriteoInitException e1) {
        }

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
