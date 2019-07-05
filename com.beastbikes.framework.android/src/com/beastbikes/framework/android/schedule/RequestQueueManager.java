package com.beastbikes.framework.android.schedule;

import com.android.volley.RequestQueue;

/**
 * The request queue manager
 *
 * @author johnson
 */
public interface RequestQueueManager {

    /**
     * Returns an instance of {@link RequestQueue}
     *
     * @return an instance of {@link RequestQueue}
     */
    public RequestQueue getRequestQueue();

}
