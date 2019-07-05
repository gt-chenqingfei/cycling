package com.beastbikes.framework.android.fsm;

/**
 * The generic state machine
 *
 * @author johnson
 */
public interface  StateMachine {

    /**
     * Returns the state of this state machine
     *
     * @return
     */
    public int getState();

    /**
     * Set the next state of this state machine
     *
     * @param state The new state
     * @throws UnreachableTransitionException
     */
    public void setState(int state) throws UnreachableTransitionException;

    /**
     * Test the specified state is reachable
     *
     * @param state The state to be tested
     * @return
     */
    public boolean isReachable(int state);

    /**
     * Set the reachability from state {@code from} to state {@code to} to
     * create a new transition
     *
     * @param from The start state of the transition
     * @param to   The next state of the transition
     */
    public void setReachability(int from, int to);

    /**
     * Returns all {@link StateChangeListener}s
     *
     * @return
     */
    public StateChangeListener[] getStateChangeListeners();

    /**
     * Register a {@link StateChangeListener}
     *
     * @param listener {@link StateChangeListener}
     */
    public void addStateChangeListener(StateChangeListener listener);

    /**
     * Unregister a {@link StateChangeListener}
     *
     * @param listener {@link StateChangeListener}
     */
    public void removeStateChangeListener(StateChangeListener listener);

}
