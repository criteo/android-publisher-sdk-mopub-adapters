package com.criteo.publisher.view

import android.webkit.WebView
import com.criteo.publisher.concurrent.ThreadingUtil.runOnMainThreadAndWait
import com.criteo.publisher.util.CompletableFuture
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

fun WebView.simulateClickOnAd() {
  // TODO kind of duplication of [WebViewClicker#simulateClickOnAd]
  waitUntilLoaded()
  val isClickDone = CompletableFuture<Void>()

  // Simulate click via JavaScript
  runOnMainThreadAndWait {
    val javascript = """
          (function() {
            var elements = document.getElementsByTagName('a');
            if (elements.length != 1) {
              return false;
            }
            elements[0].click();
            return true;
          })();""".trimIndent()

    evaluateJavascript(javascript) { value: String ->
      if ("true" != value) {
        isClickDone.completeExceptionally(IllegalStateException("Clickable element was not found in the WebView"))
      } else {
        isClickDone.complete(null)
      }
    }
  }

  isClickDone.get()
}