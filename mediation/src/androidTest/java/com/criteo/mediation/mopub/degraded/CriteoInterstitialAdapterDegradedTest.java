package com.criteo.mediation.mopub.degraded;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.criteo.mediation.mopub.CriteoInterstitialAdapter;
import com.criteo.mediation.mopub.InterstitialAdapterHelper;
import com.criteo.publisher.TestAdUnits;
import com.criteo.publisher.mock.MockedDependenciesRule;
import com.criteo.publisher.mock.SpyBean;
import com.criteo.publisher.util.DeviceUtil;
import com.mopub.mobileads.CustomEventInterstitial.CustomEventInterstitialListener;
import com.mopub.mobileads.MoPubErrorCode;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class CriteoInterstitialAdapterDegradedTest {

  @Rule
  public MockedDependenciesRule mockedDependenciesRule = new MockedDependenciesRule();

  @SpyBean
  private DeviceUtil deviceUtil;

  @Mock
  private CustomEventInterstitialListener listener;

  private InterstitialAdapterHelper adapterHelper;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    assumeIsDegraded();

    adapterHelper = new InterstitialAdapterHelper(new CriteoInterstitialAdapter());
  }

  @Test
  public void loadInterstitial_GivenValidBannerAndLoadTwice_NotifyTwiceForNoFill() throws Exception {
    loadInterstitial();
    loadInterstitial();

    verify(listener, times(2)).onInterstitialFailed(MoPubErrorCode.NETWORK_NO_FILL);
  }

  private void loadInterstitial() {
    adapterHelper.loadInterstitial(TestAdUnits.INTERSTITIAL, listener);
    mockedDependenciesRule.waitForIdleState();
  }

  private void assumeIsDegraded() {
    when(deviceUtil.isVersionSupported()).thenReturn(false);
  }

}
