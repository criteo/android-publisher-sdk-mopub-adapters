package com.criteo.mediation.mopub;

import java.util.HashMap;
import java.util.Map;

public class MoPubHelper {

  public static final String ADUNIT_ID = "adUnitId";
  public static final String CRITEO_PUBLISHER_ID = "cpId";
  private static final String MOPUB_WIDTH = "com_mopub_ad_width";
  private static final String MOPUB_HEIGHT = "com_mopub_ad_height";

  public static Map<String, String> serverExtras(String cpId, String adUnitId) {
    Map<String, String> extras = new HashMap<>();
    extras.put(CRITEO_PUBLISHER_ID, cpId);
    extras.put(ADUNIT_ID, adUnitId);
    return extras;
  }

  public static Map<String, Object> localExtras(int width, int height) {
    Map<String, Object> extras = new HashMap<>();
    extras.put(MOPUB_WIDTH, width);
    extras.put(MOPUB_HEIGHT, height);
    return extras;
  }

}
