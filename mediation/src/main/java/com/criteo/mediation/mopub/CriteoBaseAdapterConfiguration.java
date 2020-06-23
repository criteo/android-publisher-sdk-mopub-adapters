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

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.mopub.common.BaseAdapterConfiguration;
import com.mopub.common.OnNetworkInitializationFinishedListener;
import com.mopub.mobileads.MoPubErrorCode;
import java.util.Map;

public class CriteoBaseAdapterConfiguration extends BaseAdapterConfiguration {

    private String adapterVersion;
    private String networkSdkVersion;
    private String moPubNetworkName;


    @NonNull
    @Override
    public String getAdapterVersion() {
        return adapterVersion;
    }

    @Nullable
    @Override
    public String getBiddingToken(@NonNull Context context) {
        return null;
    }

    @NonNull
    @Override
    public String getMoPubNetworkName() {
        return moPubNetworkName;
    }

    @NonNull
    @Override
    public String getNetworkSdkVersion() {
        return networkSdkVersion;
    }

    @Override
    public void initializeNetwork(@NonNull Context context, @Nullable Map<String, String> configuration,
            @NonNull OnNetworkInitializationFinishedListener listener) {

        this.networkSdkVersion = BuildConfig.SDK_VERSION;
        this.adapterVersion = BuildConfig.VERSION_NAME;
        this.moPubNetworkName = "criteo";

        listener.onNetworkInitializationFinished(CriteoBaseAdapterConfiguration.class,
                MoPubErrorCode.ADAPTER_INITIALIZATION_SUCCESS);
    }
}
