package com.beastbikes.framework.android.fsm;

import java.util.EventObject;

public class StateChangeEvent extends EventObject {

    private static final long serialVersionUID = 6502119784248708786L;

    private final int from;
    private final int to;

    StateChangeEvent(StateMachine fsm, int from, int to) {
        super(fsm);
        this.from = from;
        this.to = to;
    }

    @Override
    public StateMachine getSource() {
        return (StateMachine) super.getSource();
    }

    public int getFromState() {
        return from;
    }

    public int getToState() {
        return to;
    }

}
