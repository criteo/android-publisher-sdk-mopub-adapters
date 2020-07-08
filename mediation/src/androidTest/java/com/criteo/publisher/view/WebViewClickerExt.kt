package com.criteo.publisher.view

import android.webkit.WebView
import com.criteo.publisher.concurrent.ThreadingUtil.runOnMainThreadAndWait
import java.util.concurrent.CountDownLatch

fun WebView.waitUntilLoaded() {
  // TODO duplicate of [WebViewClicker#waitUntilWebViewIsLoaded]
  val isHtmlLoaded = CountDownLatch(1)

  runOnMainThreadAndWait {
    postVisualStateCallback(42, object : WebView.VisualStateCallback() {
      override fun onComplete(ignored: Long) {
        isHtmlLoaded.countDown()
      }
    })
  }

  isHtmlLoaded.await()
}