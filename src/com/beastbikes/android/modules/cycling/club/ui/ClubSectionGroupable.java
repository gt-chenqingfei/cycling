package com.beastbikes.android.modules.cycling.club.ui;

import java.util.ArrayList;
import java.util.List;

import android.view.View;

import com.beastbikes.android.modules.cycling.club.dto.ClubInfoCompact;
import com.beastbikes.framework.ui.android.lib.pulltorefresh.Groupable;

/**
 * @author chenqingfei
 */
public class ClubSectionGroupable implements Groupable<ClubInfoCompact> {

    private static final long serialVersionUID = 8268785190810427096L;
    public String title;
    public String title2;
    ArrayList<ClubInfoCompact> children;
    public View.OnClickListener listener;

    public ClubSectionGroupable() {
    }

    public ClubSectionGroupable(String title, List<ClubInfoCompact> children) {
        this.title = title;
        this.addChildren((ArrayList<ClubInfoCompact>) children);
    }

    public ClubSectionGroupable(String title) {
        this.title = title;
    }

    public ClubSectionGroupable(String title, String title2) {
        this.title = title;
        this.title2 = title2;
    }

    public ClubSectionGroupable(String title, String title2, View.OnClickListener listener) {
        this.title = title;
        this.title2 = title2;
        this.listener = listener;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public String getTitle2() {
        return title2;
    }

    public View.OnClickListener getListener() {
        return listener;
    }

    @Override
    public ArrayList<ClubInfoCompact> getChildren() {
        return children;
    }

    @Override
    public void addChild(ClubInfoCompact child) {
        if (children == null) {
            children = new ArrayList<ClubInfoCompact>();
        }
        children.add(child);
    }

    @Override
    public void addChildren(ArrayList<ClubInfoCompact> children) {
        if (this.children == null) {
            this.children = new ArrayList<ClubInfoCompact>();
        }
        this.children.addAll(children);
    }

}