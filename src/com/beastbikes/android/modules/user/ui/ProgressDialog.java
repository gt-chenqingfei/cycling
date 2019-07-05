package com.beastbikes.android.modules.user.ui;

import android.content.Context;

public class ProgressDialog extends android.app.ProgressDialog {

    private final Context context;

    public ProgressDialog(Context context) {
        super(context);
        this.context = context;
    }

    public ProgressDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;
    }

    public void setMessage(int msgId) {
        super.setMessage(this.context.getString(msgId));
    }

}
