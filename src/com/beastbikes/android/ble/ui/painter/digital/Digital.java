package com.beastbikes.android.ble.ui.painter.digital;

import com.beastbikes.android.ble.ui.painter.Painter;

/**
 * @author Adrián García Lomas
 */
public interface Digital extends Painter {

  void setValue(float value);

  void setDesc(String desc);

  void setTitle(String title);
}
