package com.criteo.mediation.mopub;

import static com.criteo.mediation.mopub.MoPubHelper.localExtras;
import static com.criteo.mediation.mopub.MoPubHelper.serverExtras;
import static com.criteo.publisher.CriteoUtil.clearCriteo;
import static com.criteo.publisher.CriteoUtil.givenInitializedCriteo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import com.criteo.publisher.CriteoBannerView;
import com.criteo.publisher.TestAdUnits;
import com.criteo.publisher.mock.MockedDependenciesRule;
import com.mopub.mobileads.CustomEventBanner;
import com.mopub.mobileads.MoPubErrorCode;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class CriteoMopubBannerAdapterTest {

    @Rule
    public MockedDependenciesRule mockedDependenciesRule = new MockedDependenciesRule();

    private Context context;

    private CriteoBannerAdapter adapter;
    private BannerAdapterHelper adapterHelper;

    @Mock
    private CustomEventBanner.CustomEventBannerListener listener;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        clearCriteo();

        context = InstrumentationRegistry.getContext();

        adapter = new CriteoBannerAdapter();
        adapterHelper = new BannerAdapterHelper(adapter);
    }

    @Test
    public void requestBannerAdWithEmptyParameters() {
        Map<String, String> serverExtras = new HashMap<>();
        Map<String, Object> localExtras = new HashMap<>();

        adapter.loadBanner(context, listener, localExtras, serverExtras);

        verify(listener).onBannerFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
        verifyNoMoreInteractions(listener);
    }

    @Test
    public void requestBannerAdWithNullAdSize() {
        Map<String, String> serverExtras = serverExtras("cpId", "myAdUnit");
        Map<String, Object> localExtras = new HashMap<>();

        adapter.loadBanner(context, listener, localExtras, serverExtras);
        verify(listener).onBannerFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
        verifyNoMoreInteractions(listener);
    }

    @Test
    public void requestBannerAdWithNullPublisherId() {
        Map<String, String> serverExtras = serverExtras(null, "myAdUnit");
        Map<String, Object> localExtras = localExtras(320, 50);

        adapter.loadBanner(context, listener, localExtras, serverExtras);
        verify(listener).onBannerFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
        verifyNoMoreInteractions(listener);
    }

    @Test
    public void requestBannerAdWithNullAdUnitId() {
        Map<String, String> serverExtras = serverExtras("cpId", null);
        Map<String, Object> localExtras = localExtras(320, 50);

        adapter.loadBanner(context, listener, localExtras, serverExtras);

        verify(listener).onBannerFailed(MoPubErrorCode.MISSING_AD_UNIT_ID);
        verifyNoMoreInteractions(listener);
    }

    @Test
    public void givenNotInitializedCriteo_WhenLoadingBannerTwice_MissFirstOpportunityBecauseOfBidCachingAndSucceedOnNextOne() throws Exception {
        loadValidBanner();
        loadValidBanner();

        checkMissFirstOpportunityBecauseOfBidCachingAndSucceedOnNextOne();
    }

    @Test
    public void givenInitializedCriteo_WhenLoadingBannerTwice_MissFirstOpportunityBecauseOfBidCachingAndSucceedOnNextOne() throws Exception {
        givenInitializedCriteo();

        loadValidBanner();
        loadValidBanner();

        checkMissFirstOpportunityBecauseOfBidCachingAndSucceedOnNextOne();
    }

    private void loadValidBanner() {
        adapterHelper.loadBanner(TestAdUnits.BANNER_320_50, listener);
        mockedDependenciesRule.waitForIdleState();
    }

    private void checkMissFirstOpportunityBecauseOfBidCachingAndSucceedOnNextOne() {
        InOrder inOrder = inOrder(listener);
        inOrder.verify(listener).onBannerFailed(MoPubErrorCode.NETWORK_NO_FILL);
        inOrder.verify(listener).onBannerLoaded(any(CriteoBannerView.class));
        inOrder.verifyNoMoreInteractions();
    }

}