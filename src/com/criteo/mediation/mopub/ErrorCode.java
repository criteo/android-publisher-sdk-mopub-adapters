package com.criteo.mediation.mopub;

import androidx.annotation.NonNull;
import com.criteo.publisher.CriteoErrorCode;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.nativeads.NativeErrorCode;

public class ErrorCode {

  @NonNull
  public static MoPubErrorCode toMoPub(@NonNull CriteoErrorCode code) {
    switch (code) {
      case ERROR_CODE_INTERNAL_ERROR:
        return MoPubErrorCode.INTERNAL_ERROR;
      case ERROR_CODE_NETWORK_ERROR:
        return MoPubErrorCode.NETWORK_TIMEOUT;
      case ERROR_CODE_INVALID_REQUEST:
        return MoPubErrorCode.SERVER_ERROR;
      case ERROR_CODE_NO_FILL:
        return MoPubErrorCode.NETWORK_NO_FILL;
      default:
        throw new UnsupportedOperationException("Unknown Criteo error code: " + code);
    }
  }

  @NonNull
  public static NativeErrorCode toNativeMoPub(@NonNull CriteoErrorCode code) {
    switch (code) {
      case ERROR_CODE_INTERNAL_ERROR:
        return NativeErrorCode.NETWORK_INVALID_STATE;
      case ERROR_CODE_NETWORK_ERROR:
        return NativeErrorCode.NETWORK_TIMEOUT;
      case ERROR_CODE_INVALID_REQUEST:
        return NativeErrorCode.NETWORK_INVALID_REQUEST;
      case ERROR_CODE_NO_FILL:
        return NativeErrorCode.NETWORK_NO_FILL;
      default:
        throw new UnsupportedOperationException("Unknown Criteo error code: " + code);
    }
  }

}
