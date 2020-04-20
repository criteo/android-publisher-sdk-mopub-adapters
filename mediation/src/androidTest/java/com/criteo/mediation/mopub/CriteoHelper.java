package com.criteo.mediation.mopub;

import android.content.Context;
import com.criteo.publisher.Criteo;
import com.mopub.mobileads.CustomEventBanner.CustomEventBannerListener;
import com.mopub.mobileads.CustomEventInterstitial.CustomEventInterstitialListener;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class CriteoHelper {

  public static final String TEST_CP_ID = "B-000001";

  /**
   * The Criteo SDK is a singleton that contains some cached data.
   * In order to have isolated test scenario, those cached data should be clean.
   * However, this practice should stay exceptional and should never be done by publishers.
   * Hence, no public API should be developed for that.
   * Also, the Criteo.init method should keep creating only one instance because publishers
   * may call it many times.
   * That's why this is solved with reflection by putting null into the singleton instance holder.
   * Because SDK release artifact is obfuscated by proguard, we cannot directly get the "criteo" field,
   * instead we should loop over the fields and select the static Criteo one.
   */
  public static void givenNotInitializedCriteo() throws ReflectiveOperationException {
    Field[] fields = Criteo.class.getDeclaredFields();
    Field singletonField = null;
    for (Field field : fields) {
      if (Criteo.class.equals(field.getType()) && Modifier.isStatic(field.getModifiers())) {
        singletonField = field;
        break;
      }
    }

    if (singletonField == null) {
      throw new IllegalStateException("Criteo singleton was not found");
    }

    singletonField.setAccessible(true);
    singletonField.set(null, null);
  }

  public static void loadBanner(
      CriteoBannerAdapter adapter,
      Context context,
      CustomEventBannerListener customEventBannerListener,
      Map<String, Object> localExtras,
      Map<String, String> serverExtras) {
    adapter.loadBanner(context, customEventBannerListener, localExtras, serverExtras);
  }

  public static void loadInterstitial(
      CriteoInterstitialAdapter adapter,
      Context context,
      CustomEventInterstitialListener customEventInterstitialListener,
      Map<String, String> serverExtras) {
    adapter.loadInterstitial(context, customEventInterstitialListener, new HashMap<String, Object>(), serverExtras);
  }

}
