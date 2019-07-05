package com.beastbikes.framework.android.schedule;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class RequestQueueFactory {

    private RequestQueueFactory() {
    }

    public static RequestQueue newRequestQueue(Context context) {
        return Volley.newRequestQueue(context);
    }

}
