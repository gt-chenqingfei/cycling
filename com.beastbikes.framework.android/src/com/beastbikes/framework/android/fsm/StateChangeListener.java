package com.beastbikes.framework.android.fsm;

public interface StateChangeListener {

    public void beforeStateChange(StateChangeEvent event);

    public void afterStateChange(StateChangeEvent event);

}
