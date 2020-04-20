package com.criteo.mediation.mopub;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import com.criteo.publisher.Criteo;
import com.criteo.publisher.CriteoInitException;
import com.mopub.common.MoPub;
import com.mopub.common.privacy.PersonalInfoManager;

class CriteoInitializer {

  @Nullable
  private final PersonalInfoManager personalInfoManager;

  public CriteoInitializer() {
    this(MoPub.getPersonalInformationManager());
  }

  @VisibleForTesting
  CriteoInitializer(@Nullable PersonalInfoManager personalInfoManager) {
    this.personalInfoManager = personalInfoManager;
  }

  public void init(@NonNull Context context, @NonNull String criteoPublisherId) {
    Criteo.Builder criteoBuilder = getCriteoBuilder(context, criteoPublisherId);
    Criteo criteo = null;

    String consentStatus;
    if (personalInfoManager == null) {
      consentStatus = null;
    } else {
      consentStatus = personalInfoManager.getPersonalInfoConsentStatus().name();
    }

    try {
      criteoBuilder.mopubConsent(consentStatus);
      criteo = criteoBuilder.init();
    } catch (CriteoInitException e1) {
    }

    if (criteo != null) {
      // re-setting ConsentStatus in case its value changes while Criteo object is still in memory
      // this is necessary because CriteoBuilder.init() doesn't recreate Criteo object if it's
      // already instantiated
      criteo.setMopubConsent(consentStatus);
    }
  }

  @VisibleForTesting
  @NonNull
  Criteo.Builder getCriteoBuilder(@NonNull Context context, @NonNull String criteoPublisherId) {
      return new Criteo.Builder(
          (Application) context.getApplicationContext(), criteoPublisherId
      );
  }
}
