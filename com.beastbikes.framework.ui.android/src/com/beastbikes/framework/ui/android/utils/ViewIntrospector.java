package com.beastbikes.framework.ui.android.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.app.Activity;
import android.view.View;
import android.view.Window;

import com.beastbikes.framework.android.res.annotation.IdResource;

public final class ViewIntrospector {

    public static void introspect(View v, Object o) {
        new ViewIntrospector(v).introspect(o);
    }

    public static void introspect(Activity a, Object o) {
        new ViewIntrospector(a).introspect(o);
    }

    public static void introspect(Window w, Object o) {
        new ViewIntrospector(w).introspect(o);
    }

    private static final String FIND_VIEW_BY_ID = "findViewById";

    private final Object view;
    private final Method findViewById;

    public ViewIntrospector(View view) {
        this.view = view;

        try {
            final Class<?> clazz = view.getClass();
            this.findViewById = clazz.getMethod(FIND_VIEW_BY_ID, int.class);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public ViewIntrospector(Activity activity) {
        this.view = activity;

        try {
            final Class<?> clazz = activity.getClass();
            this.findViewById = clazz.getMethod(FIND_VIEW_BY_ID, int.class);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public ViewIntrospector(Window window) {
        this.view = window;

        try {
            final Class<?> clazz = window.getClass();
            this.findViewById = clazz.getMethod(FIND_VIEW_BY_ID, int.class);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public void introspect(Object o) {
        final Class<?> clazz = o.getClass();
        final Field[] fields = clazz.getDeclaredFields();

        for (int i = 0; i < fields.length; i++) {
            final Field field = fields[i];

            if (!View.class.isAssignableFrom(field.getType()))
                continue;

            final IdResource id = field.getAnnotation(IdResource.class);
            if (id == null)
                continue;

            field.setAccessible(true);

            try {
                field.set(o, this.findViewById.invoke(this.view, id.value()));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

}
