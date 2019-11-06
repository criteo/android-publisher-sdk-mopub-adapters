package com.criteo.mediation.mopub;

import static org.mockito.Mockito.inOrder;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import com.criteo.publisher.Criteo;
import com.criteo.publisher.model.AdUnit;
import com.mopub.mobileads.CustomEventInterstitial;
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
public class CriteoMopubInterstitialAdapterTest {

    private static final String BANNER_ADUNIT_ID = "86c36b6223ce4730acf52323de3baa93";
    private static final String ADUNIT_ID = "adUnitId";
    private static final String CRITEO_PUBLISHER_ID = "cpId";
    private static final String MOPUB_WIDTH = "com_mopub_ad_width";
    private static final String MOPUB_HEIGHT = "com_mopub_ad_height";

    private static final String TEST_ID = "6yws53jyfjgoq1ghnuqb";
    private static final String CP_ID = "B-056946";

    private Context context;
    private Map<String, Object> localExtras;
    private Map<String, String> serverExtras;
    private CriteoInterstitialAdapter criteoMopubInterstitialAdapter;

    @Mock
    private CustomEventInterstitial.CustomEventInterstitialListener customEventInterstitialListener;

    @Before
    public void setUp(){
        context = InstrumentationRegistry.getContext();
        MockitoAnnotations.initMocks(this);
        localExtras = new HashMap<String, Object>();
        serverExtras = new HashMap<String, String>();
        criteoMopubInterstitialAdapter = new CriteoInterstitialAdapter();
    }

    // serverExtras and localExtras are empty
    @Test
    public void requestInterstitialAdWithEmptyParameters() {

        criteoMopubInterstitialAdapter.loadInterstitial(context, customEventInterstitialListener, localExtras, serverExtras);

        Mockito.verify(customEventInterstitialListener, Mockito.times(1))
                .onInterstitialFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
        Mockito.verify(customEventInterstitialListener, Mockito.times(0))
                .onInterstitialFailed(MoPubErrorCode.MISSING_AD_UNIT_ID);
    }

    @Test
    public void requestInterstitialAdWithNullPublisherId() {
        serverExtras.put(ADUNIT_ID, BANNER_ADUNIT_ID);

        criteoMopubInterstitialAdapter.loadInterstitial(context, customEventInterstitialListener, localExtras, serverExtras);
        Mockito.verify(customEventInterstitialListener, Mockito.times(1))
                .onInterstitialFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
        Mockito.verify(customEventInterstitialListener, Mockito.times(0))
                .onInterstitialFailed(MoPubErrorCode.MISSING_AD_UNIT_ID);
    }

    @Test
    public void requestBannerAdWithNullAdUnitId() {
        serverExtras.put(CRITEO_PUBLISHER_ID, "123");

        criteoMopubInterstitialAdapter.loadInterstitial(context, customEventInterstitialListener, localExtras, serverExtras);
        Mockito.verify(customEventInterstitialListener, Mockito.times(0))
                .onInterstitialFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
        Mockito.verify(customEventInterstitialListener, Mockito.times(1))
                .onInterstitialFailed(MoPubErrorCode.MISSING_AD_UNIT_ID);
    }

    @Test
    public void requestBannerAdWithNullCriteo() {
        serverExtras.put(CRITEO_PUBLISHER_ID, "123");
        serverExtras.put(ADUNIT_ID, BANNER_ADUNIT_ID);
        localExtras.put(MOPUB_WIDTH, 320);
        localExtras.put(MOPUB_HEIGHT, 50);

        criteoMopubInterstitialAdapter.loadInterstitial(context, customEventInterstitialListener, localExtras, serverExtras);
        Mockito.verify(customEventInterstitialListener, Mockito.times(0))
                .onInterstitialFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
        Mockito.verify(customEventInterstitialListener, Mockito.times(0))
                .onInterstitialFailed(MoPubErrorCode.MISSING_AD_UNIT_ID);
        Mockito.verify(customEventInterstitialListener, Mockito.times(1))
                .onInterstitialFailed(MoPubErrorCode.INTERNAL_ERROR);
    }

    @Test
    public void givenNotInitializedCriteo_WhenLoadingInterstitialTwice_MissFirstOpportunityBecauseOfBidCachingAndSucceedOnNextOne() throws Exception {
        CriteoHelper.givenNotInitializedCriteo();

        whenLoadingInterstitialTwice();

        checkMissFirstOpportunityBecauseOfBidCachingAndSucceedOnNextOne();
    }

    @Test
    public void givenInitializedCriteo_WhenLoadingInterstitialTwice_MissFirstOpportunityBecauseOfBidCachingAndSucceedOnNextOne() throws Exception {
        // Clean the cache state first.
        // TODO: To make all tests independent, we could place this @Before every tests.
        CriteoHelper.givenNotInitializedCriteo();

        Criteo.init((Application) context.getApplicationContext(), CP_ID, Collections.<AdUnit>emptyList());

        whenLoadingInterstitialTwice();

        checkMissFirstOpportunityBecauseOfBidCachingAndSucceedOnNextOne();
    }

    private void whenLoadingInterstitialTwice() throws Exception {
        serverExtras.put(CRITEO_PUBLISHER_ID, CP_ID);
        serverExtras.put(ADUNIT_ID, TEST_ID);

        final CyclicBarrier latch = new CyclicBarrier(2);

        Runnable loadBanner = new Runnable() {
            @Override
            public void run() {
                criteoMopubInterstitialAdapter.loadInterstitial(context, customEventInterstitialListener, localExtras, serverExtras);

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
        InOrder inOrder = inOrder(customEventInterstitialListener);
        inOrder.verify(customEventInterstitialListener).onInterstitialFailed(MoPubErrorCode.NETWORK_NO_FILL);
        inOrder.verify(customEventInterstitialListener).onInterstitialLoaded();
        inOrder.verifyNoMoreInteractions();
    }

}