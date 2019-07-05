package com.beastbikes.android.modules.cycling.activity.biz;

import com.beastbikes.framework.android.fsm.FiniteStateMachine;
import com.beastbikes.framework.android.fsm.UnreachableTransitionException;

public class ActivityStateMachine extends FiniteStateMachine implements
        ActivityState {

    public ActivityStateMachine() {
    }

    public ActivityStateMachine(int state) {
        super(state);
    }

    public void start() throws UnreachableTransitionException {
        this.setState(STATE_STARTED);
    }

    public void pause(boolean auto) throws UnreachableTransitionException {
        this.setState(auto ? STATE_AUTO_PAUSED : STATE_PAUSED);
    }

    public void resume() throws UnreachableTransitionException {
        this.setState(STATE_STARTED);
    }

    public void complete() throws UnreachableTransitionException {
        this.setState(STATE_COMPLETE);
    }

}
