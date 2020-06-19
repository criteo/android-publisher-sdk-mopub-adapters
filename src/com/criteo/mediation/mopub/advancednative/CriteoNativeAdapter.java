package com.criteo.mediation.mopub.advancednative;

import static com.mopub.common.logging.MoPubLog.AdapterLogEvent.LOAD_FAILED;
import static com.mopub.nativeads.NativeErrorCode.NATIVE_ADAPTER_CONFIGURATION_ERROR;

import android.content.Context;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import com.criteo.mediation.mopub.CriteoInitializer;
import com.criteo.publisher.advancednative.CriteoNativeLoader;
import com.criteo.publisher.model.NativeAdUnit;
import com.mopub.common.logging.MoPubLog;
import com.mopub.nativeads.CustomEventNative;
import java.util.Map;

@Keep
public class CriteoNativeAdapter extends CustomEventNative {

  private static final String TAG = CriteoNativeAdapter.class.getSimpleName();

  private static final String ADUNIT_ID = "adUnitId";
  private static final String CRITEO_PUBLISHER_ID = "cpId";

  @NonNull
  private final CriteoInitializer criteoInitializer;

  @SuppressWarnings("unused") // Used by MoPub via reflection
  public CriteoNativeAdapter() {
    this(new CriteoInitializer());
  }

  public CriteoNativeAdapter(@NonNull CriteoInitializer criteoInitializer) {
    this.criteoInitializer = criteoInitializer;
  }

  @Override
  protected void loadNativeAd(
      @NonNull Context context,
      @NonNull CustomEventNativeListener customEventNativeListener,
      @NonNull Map<String, Object> localExtras,
      @NonNull Map<String, String> serverExtras
  ) {
    String criteoPublisherId = serverExtras.get(CRITEO_PUBLISHER_ID);
    String adUnitId = serverExtras.get(ADUNIT_ID);

    if (criteoPublisherId == null || adUnitId == null) {
      MoPubLog.log(LOAD_FAILED, TAG, "CriteoPublisherId and AdUnit ID should not be null");
      customEventNativeListener.onNativeAdFailed(NATIVE_ADAPTER_CONFIGURATION_ERROR);
      return;
    }

    criteoInitializer.init(context, criteoPublisherId);

    CriteoNativeLoader nativeLoader = new CriteoNativeLoader(
        new NativeAdUnit(adUnitId),
        new CriteoNativeEventListener(customEventNativeListener),
        new NoOpNativeRenderer()
    );

    nativeLoader.loadAd();
  }

}
