package com.beastbikes.android.ble.ui.painter.progress;

import android.content.Context;
import android.graphics.BlurMaskFilter;

/**
 * @author Adrián García Lomas
 */
public class BlurProgressPowerPainter extends ProgressPowerPainterImp {

  public BlurProgressPowerPainter(int color, float max, int margin, Context context, int strokeWidth) {
    super(color, max, margin, context, strokeWidth);
    paint.setMaskFilter(new BlurMaskFilter(45, BlurMaskFilter.Blur.NORMAL));
  }
}
