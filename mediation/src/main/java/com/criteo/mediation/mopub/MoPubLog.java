package com.criteo.mediation.mopub;

import androidx.annotation.Nullable;
import com.mopub.common.logging.MoPubLog.AdapterLogEvent;

public class MoPubLog {

  /**
   * Java wrapper around MoPub logger. The <code>MPLogEventType</code> interface is private and
   * makes kotlin runtime fail with an {@link IllegalAccessError}. Although, the call is valid from
   * Java.
   */
  public static void log(
      @Nullable final AdapterLogEvent logEventType,
      @Nullable final Object... args
  ) {
    com.mopub.common.logging.MoPubLog.log(logEventType, args);
  }

}
