/*
 *    Copyright 2020 Criteo
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.criteo.mediation.mopub;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import android.content.Context;
import com.mopub.mobileads.AdData;
import com.mopub.mobileads.AdLifecycleListener;
import com.mopub.mobileads.BaseAdExtKt;
import com.mopub.mobileads.MoPubErrorCode;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@RunWith(Parameterized.class)
public class CriteoInterstitialAdapterTest {
  @Parameterized.Parameters
  public static Collection extrasParameters() {
    return Arrays.asList(new Object[][]{
        {"fake_publisher_id", "fake_adunit_id", true},
        {null, null, false},
        {"fake_publisher_id", null, false},
        {null, "fake_adunit_id", false},
    });
  }

  @Parameter(0)
  public String publisherId;

  @Parameter(1)
  public String adUnitId;

  @Parameter(2)
  public boolean shouldInitCriteo;

  @Mock
  private CriteoInitializer criteoInitializer;

  @InjectMocks
  private CriteoInterstitialAdapter criteoInterstitialAdapter;

  @Mock
  private Context context;

  @Mock
  private AdLifecycleListener.LoadListener loadListener;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void testCriteoInit() {
    // given
    Map<String, String> serverExtras = setupServerExtras(adUnitId, publisherId);
    AdData adData = new AdData.Builder().extras(serverExtras).build();

    MoPubErrorCode expectedError = MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR;
    if (publisherId != null && adUnitId == null) {
      // If everything is good except the ad unit id
      expectedError = MoPubErrorCode.MISSING_AD_UNIT_ID;
    }

    // when
    BaseAdExtKt.load(criteoInterstitialAdapter, context, loadListener, adData);

    // then
    if (shouldInitCriteo) {
      verify(criteoInitializer).init(context, "fake_publisher_id");
    } else {
      verify(criteoInitializer, never()).init(any(Context.class), anyString());
      verify(loadListener).onAdLoadFailed(expectedError);
      verifyNoMoreInteractions(loadListener);
    }
  }

  private Map<String, String> setupServerExtras(String adUnitId, String publisherId) {
    Map<String, String> serverExtras = new HashMap<>();
    if (adUnitId != null) {
      serverExtras.put(CriteoBannerAdapter.ADUNIT_ID, adUnitId);
    }

    if (publisherId != null) {
      serverExtras.put(CriteoBannerAdapter.CRITEO_PUBLISHER_ID, publisherId);
    }

    return serverExtras;
  }
}
