package com.mopub.network

import android.content.Context
import com.mopub.common.AdFormat
import com.mopub.volley.NetworkResponse
import org.json.JSONObject

fun parseAdResponse(
        appContext: Context,
        jsonObject: JSONObject,
        adFormat: AdFormat,
        networkResponse: NetworkResponse = NetworkResponse(byteArrayOf()),
        adUnitId: String? = null,
        adUnitFormat: String = adFormat.name,
        requestId: String? = null
): AdResponse {
    return MultiAdResponse.parseSingleAdResponse(
            appContext,
            networkResponse,
            jsonObject,
            adUnitId,
            adFormat,
            adUnitFormat,
            requestId
    )
}