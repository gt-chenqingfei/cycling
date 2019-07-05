package com.beastbikes.android.modules.cycling.club.ui.widget.richeditor;

import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.BaseInputConnection;

/**
 * Created by caoxiao on 16/4/24.
 */
public class RichEditorInputConnection extends BaseInputConnection {

    public RichEditorInputConnection(View targetView, boolean fullEditor) {
        super(targetView, fullEditor);
    }

    @Override
    public boolean deleteSurroundingText(int beforeLength, int afterLength) {
        // magic: in latest Android, deleteSurroundingText(1, 0) will be called for backspace
        if (beforeLength == 1 && afterLength == 0) {
            // backspace
            return super.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
                    && super.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL));
        }

        return super.deleteSurroundingText(beforeLength, afterLength);
    }
}
