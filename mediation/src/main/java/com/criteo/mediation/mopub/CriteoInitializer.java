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

import android.app.Application;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import com.criteo.publisher.Criteo;
import com.criteo.publisher.CriteoInitException;
import com.mopub.common.MoPub;
import com.mopub.common.privacy.PersonalInfoManager;

public class CriteoInitializer {

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
