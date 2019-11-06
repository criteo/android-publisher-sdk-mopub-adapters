package com.criteo.mediation.mopub;

import static com.mopub.common.logging.MoPubLog.AdapterLogEvent.LOAD_ATTEMPTED;
import static com.mopub.common.logging.MoPubLog.AdapterLogEvent.LOAD_FAILED;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import com.criteo.publisher.Criteo;
import com.criteo.publisher.CriteoBannerView;
import com.criteo.publisher.CriteoInitException;
import com.criteo.publisher.model.AdSize;
import com.criteo.publisher.model.AdUnit;
import com.criteo.publisher.model.BannerAdUnit;
import com.mopub.common.logging.MoPubLog;
import com.mopub.mobileads.CustomEventBanner;
import com.mopub.mobileads.MoPubErrorCode;
import java.util.Collections;
import java.util.Map;

public class CriteoBannerAdapter extends CustomEventBanner {

    private static final String TAG = CriteoBannerAdapter.class.getSimpleName();
    protected static final String ADUNIT_ID = "adUnitId";
    protected static final String CRITEO_PUBLISHER_ID = "cpId";
    protected static final String MOPUB_WIDTH = "com_mopub_ad_width";
    protected static final String MOPUB_HEIGHT = "com_mopub_ad_height";
    private CriteoBannerView bannerView;

    @Override
    protected void loadBanner(Context context, CustomEventBannerListener customEventBannerListener,
            Map<String, Object> localExtras, Map<String, String> serverExtras) {

        if (TextUtils.isEmpty(localExtras.toString()) || TextUtils.isEmpty(serverExtras.toString())) {
            MoPubLog.log(LOAD_FAILED, TAG, "Server parameters are empty");
            customEventBannerListener.onBannerFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
            return;
        }

        AdSize adSize = getAdSize(localExtras);
        String criteoPublisherId = serverExtras.get(CRITEO_PUBLISHER_ID);

        if (adSize == null || criteoPublisherId == null) {
            MoPubLog.log(LOAD_FAILED, TAG, "CriteoPublisherId cannot be null");
            customEventBannerListener.onBannerFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
            return;
        }

        String adUnitId = serverExtras.get(ADUNIT_ID);

        if (adUnitId == null) {
            MoPubLog.log(LOAD_FAILED, TAG, "Missing adUnit Id");
            customEventBannerListener.onBannerFailed(MoPubErrorCode.MISSING_AD_UNIT_ID);
            return;
        }

        try {
            Criteo.init((Application) (context.getApplicationContext()), criteoPublisherId,
                Collections.<AdUnit>emptyList());
        } catch (CriteoInitException e1) {
        }

        try {
            BannerAdUnit bannerAdUnit = new BannerAdUnit(adUnitId, adSize);
            bannerView = new CriteoBannerView(context, bannerAdUnit);
            CriteoBannerEventListener listener = new CriteoBannerEventListener(customEventBannerListener);
            bannerView.setCriteoBannerAdListener(listener);
            bannerView.loadAd();
            MoPubLog.log(LOAD_ATTEMPTED, TAG, "BannerView is loading");
        } catch (Exception e) {
            MoPubLog.log(LOAD_FAILED, TAG, "Initialization failed");
            customEventBannerListener.onBannerFailed(MoPubErrorCode.INTERNAL_ERROR);
        }
    }

    @Override
    protected void onInvalidate() {
        if (bannerView != null) {
            bannerView.destroy();
        }
    }

    private AdSize getAdSize(Map<String, Object> localExtras) {
        Object objHeight = localExtras.get(MOPUB_HEIGHT);
        Object objWidth = localExtras.get(MOPUB_WIDTH);
        if (objHeight == null || objWidth == null) {
            return null;
        }

        Integer height = (Integer) objHeight;
        Integer width = (Integer) objWidth;
        return new AdSize(width, height);
    }
}
