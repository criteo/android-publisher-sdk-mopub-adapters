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
import com.mopub.mobileads.AdLifecycleListener;
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
  private AdLifecycleListener.LoadListener loadListener;

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

    verify(loadListener, times(2)).onAdLoadFailed(MoPubErrorCode.NETWORK_NO_FILL);
  }

  private void loadInterstitial() {
    adapterHelper.loadInterstitial(TestAdUnits.INTERSTITIAL, loadListener);
    mockedDependenciesRule.waitForIdleState();
  }

  private void assumeIsDegraded() {
    when(deviceUtil.isVersionSupported()).thenReturn(false);
  }

}
