package com.criteo.mediation.mopub.advancednative

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.criteo.publisher.advancednative.*

open class TestNativeRenderer(private val placeholder: Drawable? = null) : CriteoNativeRenderer {

  companion object {
    val TITLE_TAG = Any()
    val DESCRIPTION_TAG = Any()
    val PRICE_TAG = Any()
    val CALL_TO_ACTION_TAG = Any()
    val PRODUCT_IMAGE_TAG = Any()
    val ADVERTISER_DOMAIN_TAG = Any()
    val ADVERTISER_DESCRIPTION_TAG = Any()
    val ADVERTISER_LOGO_TAG = Any()
  }

  override fun createNativeView(context: Context, parent: ViewGroup?): View {
    val layout = LinearLayout(context)
    layout.orientation = LinearLayout.VERTICAL
    layout.addView(createTextView(context, TITLE_TAG))
    layout.addView(createTextView(context, DESCRIPTION_TAG))
    layout.addView(createTextView(context, PRICE_TAG))
    layout.addView(createTextView(context, CALL_TO_ACTION_TAG))
    layout.addView(createMediaView(context, PRODUCT_IMAGE_TAG))
    layout.addView(createTextView(context, ADVERTISER_DOMAIN_TAG))
    layout.addView(createTextView(context, ADVERTISER_DESCRIPTION_TAG))
    layout.addView(createMediaView(context, ADVERTISER_LOGO_TAG))
    return layout
  }

  override fun renderNativeView(
      helper: RendererHelper,
      nativeView: View,
      nativeAd: CriteoNativeAd
  ) {
    // Casts and index are used to check that this views is the same than the one that was
    // created above.
    val layout = nativeView as LinearLayout
    (layout.getChildAt(0) as TextView).text = nativeAd.title
    (layout.getChildAt(1) as TextView).text = nativeAd.description
    (layout.getChildAt(2) as TextView).text = nativeAd.price
    (layout.getChildAt(3) as TextView).text = nativeAd.callToAction
    helper.setMediaInViewWithPlaceholder(
        nativeAd.productMedia,
        layout.getChildAt(4) as CriteoMediaView
    )
    (layout.getChildAt(5) as TextView).text = nativeAd.advertiserDomain
    (layout.getChildAt(6) as TextView).text = nativeAd.advertiserDescription
    helper.setMediaInViewWithPlaceholder(
        nativeAd.advertiserLogoMedia,
        layout.getChildAt(7) as CriteoMediaView
    )
  }

  private fun createTextView(context: Context, tag: Any): TextView {
    val view = TextView(context)
    view.tag = tag
    return view
  }

  private fun createMediaView(context: Context, tag: Any): CriteoMediaView {
    val view = CriteoMediaView(context)
    view.tag = tag
    return view
  }

  private fun RendererHelper.setMediaInViewWithPlaceholder(
      mediaContent: CriteoMedia,
      mediaView: CriteoMediaView
  ) {
    if (placeholder != null) {
      mediaView.setPlaceholder(placeholder)
    }
    setMediaInView(mediaContent, mediaView)
  }
}