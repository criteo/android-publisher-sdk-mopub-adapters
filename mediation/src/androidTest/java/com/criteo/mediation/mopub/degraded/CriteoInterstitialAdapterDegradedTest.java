package com.criteo.mediation.mopub.degraded;

import static com.criteo.mediation.mopub.CriteoHelper.TEST_CP_ID;
import static com.criteo.mediation.mopub.CriteoHelper.givenNotInitializedCriteo;
import static com.criteo.mediation.mopub.degraded.DegradedUtil.assumeIsDegraded;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import com.criteo.mediation.mopub.CriteoHelper;
import com.criteo.mediation.mopub.CriteoInterstitialAdapter;
import com.criteo.publisher.model.InterstitialAdUnit;
import com.mopub.mobileads.CustomEventInterstitial.CustomEventInterstitialListener;
import com.mopub.mobileads.MoPubErrorCode;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class CriteoInterstitialAdapterDegradedTest {

  private static final InterstitialAdUnit VALID_INTERSTITIAL = new InterstitialAdUnit("test-PubSdk-Interstitial");

  private Context context;

  @Mock
  private CustomEventInterstitialListener listener;

  CriteoInterstitialAdapter adapter;

  @Before
  public void setUp() throws Exception {
    assumeIsDegraded();

    MockitoAnnotations.initMocks(this);
    context = InstrumentationRegistry.getContext();

    adapter = new CriteoInterstitialAdapter();

    givenNotInitializedCriteo();
  }

  @Test
  public void loadInterstitial_GivenValidBannerAndLoadTwice_NotifyTwiceForNoFill() throws Exception {
    Map<String, String> serverExtras = new HashMap<>();
    serverExtras.put("cpId", TEST_CP_ID);
    serverExtras.put("adUnitId", VALID_INTERSTITIAL.getAdUnitId());

    loadInterstitial(serverExtras);
    loadInterstitial(serverExtras);

    verify(listener, times(2)).onInterstitialFailed(MoPubErrorCode.NETWORK_NO_FILL);
  }

  private void loadInterstitial(final Map<String, String> serverExtras) {
    InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
      @Override
      public void run() {
        CriteoHelper.loadInterstitial(adapter, context, listener, serverExtras);
      }
    });
  }

}
