package com.criteo.mediation.mopub.degraded;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.criteo.mediation.mopub.BannerAdapterHelper;
import com.criteo.mediation.mopub.CriteoBannerAdapter;
import com.criteo.publisher.TestAdUnits;
import com.criteo.publisher.mock.MockedDependenciesRule;
import com.criteo.publisher.mock.SpyBean;
import com.criteo.publisher.util.DeviceUtil;
import com.mopub.mobileads.CustomEventBanner.CustomEventBannerListener;
import com.mopub.mobileads.MoPubErrorCode;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class CriteoBannerAdapterDegradedTest {

  @Rule
  public MockedDependenciesRule mockedDependenciesRule = new MockedDependenciesRule();

  @SpyBean
  private DeviceUtil deviceUtil;

  @Mock
  private CustomEventBannerListener listener;

  private BannerAdapterHelper adapterHelper;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    assumeIsDegraded();

    adapterHelper = new BannerAdapterHelper(new CriteoBannerAdapter());
  }

  @Test
  public void loadBanner_GivenValidBannerAndLoadTwice_NotifyTwiceForNoFill() throws Exception {
    loadBanner();
    loadBanner();

    verify(listener, times(2)).onBannerFailed(MoPubErrorCode.NETWORK_NO_FILL);
  }

  private void loadBanner() {
    adapterHelper.loadBanner(TestAdUnits.BANNER_320_50, listener);
    mockedDependenciesRule.waitForIdleState();
  }

  private void assumeIsDegraded() {
    when(deviceUtil.isVersionSupported()).thenReturn(false);
  }

}
