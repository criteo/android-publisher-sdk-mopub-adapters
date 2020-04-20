package com.criteo.mediation.mopub.degraded;

import static com.criteo.mediation.mopub.CriteoHelper.TEST_CP_ID;
import static com.criteo.mediation.mopub.CriteoHelper.givenNotInitializedCriteo;
import static com.criteo.mediation.mopub.degraded.DegradedUtil.assumeIsDegraded;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import com.criteo.mediation.mopub.CriteoBannerAdapter;
import com.criteo.mediation.mopub.CriteoHelper;
import com.criteo.publisher.model.AdSize;
import com.criteo.publisher.model.BannerAdUnit;
import com.mopub.mobileads.CustomEventBanner.CustomEventBannerListener;
import com.mopub.mobileads.MoPubErrorCode;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class CriteoBannerAdapterDegradedTest {

  private static final BannerAdUnit VALID_BANNER = new BannerAdUnit("test-PubSdk-Base",
      new AdSize(320, 50));

  private Context context;

  @Mock
  private CustomEventBannerListener listener;

  CriteoBannerAdapter adapter;

  @Before
  public void setUp() throws Exception {
    assumeIsDegraded();

    MockitoAnnotations.initMocks(this);
    context = InstrumentationRegistry.getContext();

    adapter = new CriteoBannerAdapter();

    givenNotInitializedCriteo();
  }

  @Test
  public void loadBanner_GivenValidBannerAndLoadTwice_NotifyTwiceForNoFill() throws Exception {
    Map<String, Object> localExtras = new HashMap<>();
    localExtras.put("com_mopub_ad_width", VALID_BANNER.getSize().getWidth());
    localExtras.put("com_mopub_ad_height", VALID_BANNER.getSize().getHeight());

    Map<String, String> serverExtras = new HashMap<>();
    serverExtras.put("cpId", TEST_CP_ID);
    serverExtras.put("adUnitId", VALID_BANNER.getAdUnitId());

    loadBanner(localExtras, serverExtras);
    loadBanner(localExtras, serverExtras);

    verify(listener, times(2)).onBannerFailed(MoPubErrorCode.NETWORK_NO_FILL);
  }

  private void loadBanner(
      final Map<String, Object> localExtras,
      final Map<String, String> serverExtras
  ) {
    InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
      @Override
      public void run() {
        CriteoHelper.loadBanner(adapter, context, listener, localExtras, serverExtras);
      }
    });
  }

}
