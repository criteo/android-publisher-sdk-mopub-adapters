package com.criteo.publisher.advancednative

import android.graphics.drawable.Drawable
import android.view.View

val CriteoMediaView.drawable: Drawable
  get() = imageView.drawable

fun AdChoiceOverlay.adChoiceView(view: View) = this.getAdChoiceView(view)