package com.criteo.mediation.mopub;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import com.criteo.publisher.Criteo;
import com.criteo.publisher.CriteoBannerView;
import com.criteo.publisher.model.AdUnit;
import com.mopub.mobileads.CustomEventBanner;
import com.mopub.mobileads.MoPubErrorCode;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

@RunWith(AndroidJUnit4.class)
public class CriteoMopubBannerAdapterTest {

    private static final String BANNER_ADUNIT_ID = "ca-app-pub-2995206374493561/3062725613";
    private static final String ADUNIT_ID = "adUnitId";
    private static final String CRITEO_PUBLISHER_ID = "cpId";
    private static final String MOPUB_WIDTH = "com_mopub_ad_width";
    private static final String MOPUB_HEIGHT = "com_mopub_ad_height";

    private static final String TEST_ID = "30s6zt3ayypfyemwjvmp";
    private static final String CP_ID = "B-056946";

    private Context context;
    private Map<String, Object> localExtras;
    private Map<String, String> serverExtras;
    private CriteoBannerAdapter criteoMopubBannerAdapter;

    @Mock
    private CustomEventBanner.CustomEventBannerListener customEventBannerListener;


    @Before
    public void setUp() throws Exception {
        context = InstrumentationRegistry.getContext();
        MockitoAnnotations.initMocks(this);
        localExtras = new HashMap<String, Object>();
        serverExtras = new HashMap<String, String>();
    }

    // serverExtras and localExtras are empty
    @Test
    public void requestBannerAdWithEmptyParameters() {
        criteoMopubBannerAdapter = new CriteoBannerAdapter();
        criteoMopubBannerAdapter.loadBanner(context, customEventBannerListener, localExtras, serverExtras);

        Mockito.verify(customEventBannerListener, Mockito.times(1))
                .onBannerFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
        Mockito.verify(customEventBannerListener, Mockito.times(0))
                .onBannerFailed(MoPubErrorCode.MISSING_AD_UNIT_ID);
    }

    @Test
    public void requestBannerAdWithNullAdSize() {
        criteoMopubBannerAdapter = new CriteoBannerAdapter();
        serverExtras.put(ADUNIT_ID, BANNER_ADUNIT_ID);
        serverExtras.put(CRITEO_PUBLISHER_ID, "123");
        localExtras.put("Test", "local extras shouldnt be empty");

        criteoMopubBannerAdapter.loadBanner(context, customEventBannerListener, localExtras, serverExtras);
        Mockito.verify(customEventBannerListener, Mockito.times(1))
                .onBannerFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
        Mockito.verify(customEventBannerListener, Mockito.times(0))
                .onBannerFailed(MoPubErrorCode.MISSING_AD_UNIT_ID);
    }

    @Test
    public void requestBannerAdWithNullPublisherId() {
        criteoMopubBannerAdapter = new CriteoBannerAdapter();
        serverExtras.put(ADUNIT_ID, BANNER_ADUNIT_ID);
        localExtras.put(MOPUB_WIDTH, 320);
        localExtras.put(MOPUB_HEIGHT, 50);

        criteoMopubBannerAdapter.loadBanner(context, customEventBannerListener, localExtras, serverExtras);
        Mockito.verify(customEventBannerListener, Mockito.times(1))
                .onBannerFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
        Mockito.verify(customEventBannerListener, Mockito.times(0))
                .onBannerFailed(MoPubErrorCode.MISSING_AD_UNIT_ID);
    }

    @Test
    public void requestBannerAdWithNullAdUnitId() {
        criteoMopubBannerAdapter = new CriteoBannerAdapter();
        serverExtras.put(CRITEO_PUBLISHER_ID, "123");
        localExtras.put(MOPUB_WIDTH, 320);
        localExtras.put(MOPUB_HEIGHT, 50);

        criteoMopubBannerAdapter.loadBanner(context, customEventBannerListener, localExtras, serverExtras);
        Mockito.verify(customEventBannerListener, Mockito.times(0))
                .onBannerFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
        Mockito.verify(customEventBannerListener, Mockito.times(1))
                .onBannerFailed(MoPubErrorCode.MISSING_AD_UNIT_ID);
    }

    @Test
    public void requestBannerAdWithNullCriteo() {
        criteoMopubBannerAdapter = new CriteoBannerAdapter();
        serverExtras.put(CRITEO_PUBLISHER_ID, "123");
        serverExtras.put(ADUNIT_ID, BANNER_ADUNIT_ID);
        localExtras.put(MOPUB_WIDTH, 320);
        localExtras.put(MOPUB_HEIGHT, 50);

        criteoMopubBannerAdapter.loadBanner(context, customEventBannerListener, localExtras, serverExtras);
        Mockito.verify(customEventBannerListener, Mockito.times(0))
                .onBannerFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
        Mockito.verify(customEventBannerListener, Mockito.times(0))
                .onBannerFailed(MoPubErrorCode.MISSING_AD_UNIT_ID);
        Mockito.verify(customEventBannerListener, Mockito.times(1))
                .onBannerFailed(MoPubErrorCode.INTERNAL_ERROR);
    }

    @Test
    public void givenNotInitializedCriteo_WhenLoadingBannerTwice_MissFirstOpportunityBecauseOfBidCachingAndSucceedOnNextOne() throws Exception {
        CriteoHelper.givenNotInitializedCriteo();

        whenLoadingBannerTwice();

        checkMissFirstOpportunityBecauseOfBidCachingAndSucceedOnNextOne();
    }

    @Test
    public void givenInitializedCriteo_WhenLoadingBannerTwice_MissFirstOpportunityBecauseOfBidCachingAndSucceedOnNextOne() throws Exception {
        // Clean the cache state first.
        // TODO: To make all tests independent, we could place this @Before every tests.
        CriteoHelper.givenNotInitializedCriteo();

        Criteo.init((Application) context.getApplicationContext(), CP_ID, Collections.<AdUnit>emptyList());

        whenLoadingBannerTwice();

        checkMissFirstOpportunityBecauseOfBidCachingAndSucceedOnNextOne();
    }

    private void whenLoadingBannerTwice() throws Exception {
        criteoMopubBannerAdapter = new CriteoBannerAdapter();
        serverExtras.put(CRITEO_PUBLISHER_ID, CP_ID);
        serverExtras.put(ADUNIT_ID, TEST_ID);
        localExtras.put(MOPUB_WIDTH, 320);
        localExtras.put(MOPUB_HEIGHT, 50);

        final CyclicBarrier latch = new CyclicBarrier(2);

        Runnable loadBanner = new Runnable() {
            @Override
            public void run() {
                criteoMopubBannerAdapter
                    .loadBanner(context, customEventBannerListener, localExtras, serverExtras);

                try {
                    latch.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }
        };

        final Handler handler = new Handler(Looper.getMainLooper());

        handler.post(loadBanner);
        latch.await();
        Thread.sleep(5000);

        handler.post(loadBanner);
        latch.await();
        Thread.sleep(2000);
    }

    private void checkMissFirstOpportunityBecauseOfBidCachingAndSucceedOnNextOne() {
        InOrder inOrder = inOrder(customEventBannerListener);
        inOrder.verify(customEventBannerListener).onBannerFailed(MoPubErrorCode.NETWORK_NO_FILL);
        inOrder.verify(customEventBannerListener).onBannerLoaded(any(CriteoBannerView.class));
        inOrder.verifyNoMoreInteractions();
    }

}