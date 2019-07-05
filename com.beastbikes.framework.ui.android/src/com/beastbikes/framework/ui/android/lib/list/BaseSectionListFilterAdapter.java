package com.beastbikes.framework.ui.android.lib.list;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.widget.AbsListView.RecyclerListener;
import android.widget.ExpandableListView;

import com.beastbikes.framework.ui.android.BuildConfig;
import com.beastbikes.framework.ui.android.lib.pulltorefresh.Groupable;

public abstract class BaseSectionListFilterAdapter<G extends Groupable<C>, C>
        extends BaseSectionListAdapter<G, C> implements RecyclerListener {

    protected String keyword = null;
    private ArrayList<Integer> groupIndexes;
    private ArrayList<ArrayList<Integer>> childIndexes;

    public BaseSectionListFilterAdapter(Context activity,
                                        ExpandableListView listview, ArrayList<G> groups) {
        super(activity, listview, groups);
        groupIndexes = new ArrayList<Integer>();
        childIndexes = new ArrayList<ArrayList<Integer>>();
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    private void filter() {

        long start = System.currentTimeMillis();
        groupIndexes.clear();
        childIndexes.clear();
        if (groups == null)
            return;

        int groupIndex = 0;
        for (G group : groups) {
            ArrayList<C> children = group.getChildren();
            if (children != null) {
                int childIndex = 0;
                ArrayList<Integer> cIndex = null;
                for (C child : children) {
                    if (isChildMatched(child, keyword)) {
                        if (cIndex == null) {
                            cIndex = new ArrayList<Integer>();
                        }
                        cIndex.add(childIndex);
                    }
                    childIndex++;
                }
                if (cIndex != null) {
                    groupIndexes.add(groupIndex);
                    childIndexes.add(cIndex);
                }
            }
            groupIndex++;
        }

        if(BuildConfig.DEBUG)Log.d("filter", System.currentTimeMillis() - start + "ms");
    }

    protected boolean isChildMatched(C child, String keyword) {
        return true;
    }

    @Override
    public void notifyDataSetChanged() {
        this.filter();
        super.notifyDataSetChanged();
    }

    public int getGroupCount() {
        return groupIndexes.size();
    }

    public G getGroup(int groupPosition) {

        if (groups != null && groupIndexes.size() > groupPosition) {
            return groups.get(groupIndexes.get(groupPosition));
        }

        return null;
    }

    public C getChild(int groupPosition, int childPosition) {

        Groupable<C> group = this.getGroup(groupPosition);
        if (group != null) {
            ArrayList<C> children = group.getChildren();
            ArrayList<Integer> cids = childIndexes.get(groupPosition);
            if (children != null && cids.size() > childPosition) {
                return children.get(cids.get(childPosition));
            }
        }
        return null;
    }

    public int getChildrenCount(int groupPosition) {
        Groupable<C> group = this.getGroup(groupPosition);
        if (group != null) {
            return childIndexes.get(groupPosition).size();
        }
        return 0;
    }

}
