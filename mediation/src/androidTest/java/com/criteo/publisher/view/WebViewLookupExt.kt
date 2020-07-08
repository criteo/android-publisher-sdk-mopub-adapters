package com.criteo.publisher.view

import android.view.View
import java.util.concurrent.Future

fun WebViewLookup.lookForNonEmptyHtmlContent(view: View): Future<String> {
  // TODO move this in test-utils module
  lookForWebViews(view).forEach {
    it.waitUntilLoaded()
  }

  return lookForHtmlContent(view)
}