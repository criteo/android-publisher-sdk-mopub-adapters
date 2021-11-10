package com.criteo.mediation.mopub

import android.os.Handler
import android.os.Looper

internal open class ThreadingUtil {

  private val uiHandler: Handler by lazy { Handler(Looper.getMainLooper()) }

  open fun runOnUiThread(command: () -> Unit) {
    if (Thread.currentThread() === uiHandler.looper.thread) {
      command.invoke()
    } else {
      uiHandler.post(command)
    }
  }

  /**
   * Singleton instance because the adapters are instantiated by MoPub and should have a 0-arg
   * constructor, so using a singleton allows the tests to mock this.
   */
  companion object {
    private val lazyInstance: ThreadingUtil by lazy { ThreadingUtil() }

    @JvmStatic
    var instance = lazyInstance
  }
}
