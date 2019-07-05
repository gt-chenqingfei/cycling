package com.beastbikes.framework.android.fsm;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

import com.beastbikes.framework.android.fsm.annotation.State;
import com.beastbikes.framework.android.fsm.annotation.Transition;

public abstract class FiniteStateMachine implements StateMachine {

    static final String TAG = "FiniteStateMachine";

    private final boolean[][] table;
    private final Vector<StateChangeListener> listeners = new Vector<StateChangeListener>();

    private int state;

    public FiniteStateMachine(int initialState) {
        final Map<Field, Integer> states = this.getStateFields();
        final int n = this.getStatesCount(states);

        this.state = initialState;
        this.table = new boolean[n][n];
        this.initializeTransitionTable(states);
    }

    public FiniteStateMachine() {
        final Map<Field, Integer> states = getStateFields();
        final int n = this.getStatesCount(states);

        this.state = this.getInitialState(states);
        this.table = new boolean[n][n];
        this.initializeTransitionTable(states);
    }

    protected Map<Field, Integer> getStateFields() {
        final Class<?> clazz = getClass();
        final Field[] fields = clazz.getFields();
        final int nfields = fields.length;
        final Map<Field, Integer> states = new HashMap<Field, Integer>();

        for (int i = 0; i < nfields; i++) {
            final Field field = fields[i];
            final State s = field.getAnnotation(State.class);
            if (null == s)
                continue;

            final int modifiers = field.getModifiers();
            if ((!Modifier.isPublic(modifiers))) {
                continue;
            }

            if (!Modifier.isStatic(modifiers)) {
                continue;
            }

            if (!Modifier.isFinal(modifiers)) {
                continue;
            }

            final Class<?> type = field.getType();
            if ((!int.class.equals(type)) && (!Integer.class.equals(type))) {
                continue;
            }

            try {
                states.put(field, field.getInt(clazz));
            } catch (Exception e) {
            }
        }

        return states;
    }

    protected int getStatesCount(final Map<Field, Integer> states) {
        int max = 0;

        final Collection<Integer> values = states.values();
        for (Integer i : values) {
            max = Math.max(max, i.intValue());
        }

        return max + 1;
    }

    protected int getInitialState(final Map<Field, Integer> states) {
        final Set<Entry<Field, Integer>> entries = states.entrySet();

        for (final Entry<Field, Integer> entry : entries) {
            final Field field = entry.getKey();
            final State s = field.getAnnotation(State.class);

            if (s.initial()) {
                return entry.getValue();
            }
        }

        return 0;
    }

    protected void initializeTransitionTable(final Map<Field, Integer> states) {
        final Set<Entry<Field, Integer>> entries = states.entrySet();

        for (final Entry<Field, Integer> entry : entries) {
            final Field field = entry.getKey();
            final Integer from = entry.getValue();
            final Transition t = field.getAnnotation(Transition.class);
            if (null == t)
                continue;

            final int[] next = t.value();
            final int ntrans = next.length;
            for (int i = 0; i < ntrans; i++) {
                setReachability(from, next[i]);
            }
        }
    }

    public int getState() {
        return this.state;
    }

    @Override
    public void setState(int state) throws UnreachableTransitionException {
        final int from = this.getState();
        final int to = state;

        if (!isReachable(to))
            throw new UnreachableTransitionException(from + " -> " + to);

        synchronized (this) {
            final StateChangeEvent event = new StateChangeEvent(this, from, to);
            final StateChangeListener[] listeners = getStateChangeListeners();

            for (int i = 0; i < listeners.length; i++) {
                listeners[i].beforeStateChange(event);
            }

            this.state = to;

            for (int i = 0; i < listeners.length; i++) {
                listeners[i].afterStateChange(event);
            }
        }
    }

    @Override
    public boolean isReachable(int to) {
        final int from = this.getState();

        try {
            return this.table[from][to];
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void setReachability(int from, int to) {
        synchronized (this) {
            this.table[from][to] = true;
        }
    }

    @Override
    public StateChangeListener[] getStateChangeListeners() {
        final int n = this.listeners.size();
        final StateChangeListener[] listeners = new StateChangeListener[n];
        return this.listeners.toArray(listeners);
    }

    public void addStateChangeListener(StateChangeListener listener) {
        this.listeners.add(listener);
    }

    public void removeStateChangeListener(StateChangeListener listener) {
        this.listeners.remove(listener);
    }

}

