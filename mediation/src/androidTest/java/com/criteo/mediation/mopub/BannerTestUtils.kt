package com.criteo.mediation.mopub

import android.content.Context
import com.mopub.common.AdFormat
import com.mopub.mobileads.MoPubView
import com.mopub.mobileads.loadAd
import com.mopub.network.AdResponse
import com.mopub.network.parseAdResponse
import org.json.JSONObject

fun MoPubView.loadAd() {
    val adResponse = givenMoPubResponseForCriteoAdapter(context)
    loadAd(adResponse)
}

private fun givenMoPubResponseForCriteoAdapter(appContext: Context): AdResponse {
    val json = JSONObject("""
        {
		"content": "custom selector: ",
		"metadata": {
			"content-type": "text/html; charset=UTF-8",
			"imptrackers": ["https://ads.mopub.com/m/imp?appid="],
			"vast-player-version": 1,
			"x-ad-timeout-ms": 10000,
			"x-adgroupid": "dummyId",
			"x-adtype": "custom",
			"x-before-load-url": "https://ads.mopub.com/m/attempt?account_id=",
			"x-browser-agent": 0,
			"x-clickthrough": "https://ads.mopub.com/m/aclk?appid=",
			"x-creativeid": "dummyId",
			"x-custom-event-class-data": "{\"cpId\":\"B-000001\",\"adUnitId\":\"dummyAdUnit\"}",
			"x-custom-event-class-name": "com.criteo.mediation.mopub.CriteoBannerAdapter",
			"x-height": 250,
			"x-networktype": "custom_native",
			"x-refreshtime": 30,
			"x-width": 300
		}
	}
    """.trimIndent())

    return parseAdResponse(appContext, json, AdFormat.BANNER)
}