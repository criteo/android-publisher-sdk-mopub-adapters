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

import java.util.HashMap;
import java.util.Map;

public class MoPubHelper {

  // /!\ Do not replace those class names with programmatic expressions. The class are specified on
  // MoPub server by each publisher for each of their ad units. Renaming any of these classes must
  // make the tests fail
  public static final String BANNER_ADAPTER_CLASS_NAME = "com.criteo.mediation.mopub.CriteoBannerAdapter";
  public static final String INTERSTITIAL_ADAPTER_CLASS_NAME = "com.criteo.mediation.mopub.CriteoInterstitialAdapter";
  public static final String NATIVE_ADAPTER_CLASS_NAME = "com.criteo.mediation.mopub.advancednative.CriteoNativeAdapter";

  public static final String ADUNIT_ID = "adUnitId";
  public static final String CRITEO_PUBLISHER_ID = "cpId";

  public static Map<String, String> serverExtras(String cpId, String adUnitId) {
    Map<String, String> extras = new HashMap<>();
    extras.put(CRITEO_PUBLISHER_ID, cpId);
    extras.put(ADUNIT_ID, adUnitId);
    return extras;
  }

}
