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

import android.content.Context;
import com.mopub.mobileads.CustomEventBanner.CustomEventBannerListener;
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
public class CriteoBannerAdapterTest {
  @Parameterized.Parameters
  public static Collection extrasParameters() {
    return Arrays.asList(new Object[][]{
        /** All required extras provided */
        {100, 100, "fake_publisher_id", "fake_adunit_id", true},

        /** All local extras provided only */
        {100, 100, null, null, false},

        /** All local extras provided, server extras partially provided */
        {100, 100, "fake_publisher_id", null, false},
        {100, 100, null, "fake_adunit_id", false},

        /** Local extras partially provided, server extras all provided */
        {100, null, "fake_publisher_id", "fake_adunit_id", false},
        {null, 100, "fake_publisher_id", "fake_adunit_id", false},

        /** Local extras partially provided, server extras partially provided */
        {100, null, "fake_publisher_id", null, false},
        {100, null, null, "fake_adunit_id", false},
        {null, 100, null, "fake_adunit_id", false},
        {null, 100, "fake_publisher_id", "fake_adunit_id", false},

        /** All server extras provided only */
        {null, null, "fake_publisher_id", "fake_adunit_id", false},

        /** Nothing is provided */
        {null, null, null, null, false},
    });
  }

  @Parameter(0)
  public Integer adSizeHeight;

  @Parameter(1)
  public Integer adSizeWidth;

  @Parameter(2)
  public String publisherId;

  @Parameter(3)
  public String adUnitId;

  @Parameter(4)
  public boolean shouldInitCriteo;

  @Mock
  private CriteoInitializer criteoInitializer;

  @InjectMocks
  private CriteoBannerAdapter criteoBannerAdapter;

  @Mock
  private Context context;

  @Mock
  private CustomEventBannerListener customEvenBannerListener;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void testCriteoInit() {
    // given
    Map<String, Object> localExtras = setupLocalExtras(adSizeWidth, adSizeHeight);
    Map<String, String> serverExtras = setupServerExtras(adUnitId, publisherId);

    // when
    criteoBannerAdapter
        .loadBanner(context, customEvenBannerListener, localExtras, serverExtras);

    // then
    if (shouldInitCriteo) {
      verify(criteoInitializer).init(context, "fake_publisher_id");
    } else {
      verify(criteoInitializer, never()).init(any(Context.class), anyString());
    }
  }

  private Map<String, Object> setupLocalExtras(Integer height, Integer width) {
    Map<String, Object> localExtras = new HashMap<>();

    if (height != null) {
      localExtras.put(CriteoBannerAdapter.MOPUB_HEIGHT, height);
    }

    if (width != null) {
      localExtras.put(CriteoBannerAdapter.MOPUB_WIDTH, width);
    }

    return localExtras;
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
