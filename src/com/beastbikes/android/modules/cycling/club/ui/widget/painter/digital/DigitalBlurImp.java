package com.beastbikes.android.modules.cycling.club.ui.widget.painter.digital;

import android.content.Context;
import android.graphics.BlurMaskFilter;

/**
 * @author Adrián García Lomas
 */
public class DigitalBlurImp extends DigitalImp {

  public DigitalBlurImp(int color, Context context, int marginTop, int textSize) {
    super(color, context, marginTop, textSize);
    digitPaint.setMaskFilter(new BlurMaskFilter(6, BlurMaskFilter.Blur.NORMAL));
  }
}
