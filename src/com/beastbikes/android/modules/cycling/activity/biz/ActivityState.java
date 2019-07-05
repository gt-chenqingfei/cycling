package com.beastbikes.android.modules.cycling.activity.biz;

import com.beastbikes.framework.android.fsm.annotation.State;
import com.beastbikes.framework.android.fsm.annotation.Transition;


public interface ActivityState {

    @State(initial = true)
    @Transition(ActivityState.STATE_STARTED)
    public static final int STATE_NONE = 0;

    @State
    @Transition({ActivityState.STATE_PAUSED, ActivityState.STATE_AUTO_PAUSED,
            ActivityState.STATE_COMPLETE})
    public static final int STATE_STARTED = 1;

    @State
    @Transition({ActivityState.STATE_STARTED, ActivityState.STATE_COMPLETE})
    public static final int STATE_PAUSED = 2;

    @State
    @Transition({ActivityState.STATE_STARTED, ActivityState.STATE_COMPLETE})
    public static final int STATE_AUTO_PAUSED = 3;

    @State(terminate = true)
    public static final int STATE_COMPLETE = 4;

}
