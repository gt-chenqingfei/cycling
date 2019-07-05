package com.beastbikes.android.ble.ui.painter.digital;

import android.content.Context;
import android.graphics.BlurMaskFilter;

/**
 * @author Adrián García Lomas
 */
public class DigitalBlurImp extends DigitalImp {

  public DigitalBlurImp(int color, int blurColor, Context context, int marginTop, int textSize,
                        String units, int unitSize) {
    super(color, blurColor, context, marginTop, textSize, units, unitSize);
    digitPaint.setMaskFilter(new BlurMaskFilter(6, BlurMaskFilter.Blur.NORMAL));
  }
}
