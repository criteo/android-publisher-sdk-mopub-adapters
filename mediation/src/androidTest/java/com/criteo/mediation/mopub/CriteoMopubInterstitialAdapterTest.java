package com.criteo.mediation.mopub;

import static com.criteo.mediation.mopub.MoPubHelper.serverExtras;
import static com.criteo.publisher.CriteoUtil.clearCriteo;
import static com.criteo.publisher.CriteoUtil.givenInitializedCriteo;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import com.criteo.publisher.TestAdUnits;
import com.criteo.publisher.mock.MockedDependenciesRule;
import com.mopub.mobileads.CustomEventInterstitial.CustomEventInterstitialListener;
import com.mopub.mobileads.MoPubErrorCode;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class CriteoMopubInterstitialAdapterTest {

    @Rule
    public MockedDependenciesRule mockedDependenciesRule = new MockedDependenciesRule();

    private Context context;

    private final Map<String, Object> localExtras = new HashMap<>();

    @Mock
    private CustomEventInterstitialListener listener;

    private CriteoInterstitialAdapter adapter;
    private InterstitialAdapterHelper adapterHelper;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        clearCriteo();

        context = InstrumentationRegistry.getContext();

        adapter = new CriteoInterstitialAdapter();
        adapterHelper = new InterstitialAdapterHelper(adapter);
    }

    @Test
    public void requestInterstitialAdWithEmptyParameters() {
        Map<String, String> serverExtras = new HashMap<>();

        adapter.loadInterstitial(context, listener, localExtras, serverExtras);

        verify(listener).onInterstitialFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
        verifyNoMoreInteractions(listener);
    }

    @Test
    public void requestInterstitialAdWithNullPublisherId() {
        Map<String, String> serverExtras = serverExtras(null, "myAdUnit");

        adapter.loadInterstitial(context, listener, localExtras, serverExtras);

        verify(listener).onInterstitialFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
        verifyNoMoreInteractions(listener);
    }

    @Test
    public void requestBannerAdWithNullAdUnitId() {
        Map<String, String> serverExtras = serverExtras("cpId", null);

        adapter.loadInterstitial(context, listener, localExtras, serverExtras);

        verify(listener).onInterstitialFailed(MoPubErrorCode.MISSING_AD_UNIT_ID);
        verifyNoMoreInteractions(listener);
    }

    @Test
    public void givenNotInitializedCriteo_WhenLoadingInterstitialTwice_MissFirstOpportunityBecauseOfBidCachingAndSucceedOnNextOne() throws Exception {
        loadValidInterstitial();
        loadValidInterstitial();

        checkMissFirstOpportunityBecauseOfBidCachingAndSucceedOnNextOne();
    }

    @Test
    public void givenInitializedCriteo_WhenLoadingInterstitialTwice_MissFirstOpportunityBecauseOfBidCachingAndSucceedOnNextOne() throws Exception {
        givenInitializedCriteo();

        loadValidInterstitial();
        loadValidInterstitial();

        checkMissFirstOpportunityBecauseOfBidCachingAndSucceedOnNextOne();
    }

    private void loadValidInterstitial() {
        adapterHelper.loadInterstitial(TestAdUnits.INTERSTITIAL, listener);
        mockedDependenciesRule.waitForIdleState();
    }

    private void checkMissFirstOpportunityBecauseOfBidCachingAndSucceedOnNextOne() {
        InOrder inOrder = inOrder(listener);
        inOrder.verify(listener).onInterstitialFailed(MoPubErrorCode.NETWORK_NO_FILL);
        inOrder.verify(listener).onInterstitialLoaded();
        inOrder.verifyNoMoreInteractions();
    }

}