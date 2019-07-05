package com.beastbikes.android.home.view;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.beastbikes.android.R;
import com.beastbikes.android.locale.LocaleManager;
import com.beastbikes.android.modules.cycling.activity.biz.ActivityManager;
import com.beastbikes.android.modules.cycling.activity.biz.ActivityState;
import com.beastbikes.android.modules.cycling.activity.dao.entity.LocalActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenqingfei on 16/1/7.
 */
public class NavigationView implements AdapterView.OnItemClickListener {

    private ListView mLvLeftMenu;
    private NavigationCallback callbacks;
    private DrawerLayout drawer;
    private Activity context;
    private MenuItemAdapter adapter;
    private Toolbar toolbar;
    private int currentNavItem;
    private ActivityManager am;

    public NavigationView(Activity context, ListView listView) {
        this.context = context;
        mLvLeftMenu = listView;
    }

    public void setNavigationSelectListener(NavigationCallback callbacks) {
        this.callbacks = callbacks;
    }

    public void setup(AppCompatActivity activty) {

        if (activty == null) {
            throw new RuntimeException("you must be set paramerter of Activity context!!");
        }
        am = new ActivityManager(context);
        mLvLeftMenu.setAdapter(adapter = new MenuItemAdapter(context));
        mLvLeftMenu.setOnItemClickListener(this);

        this.drawer = (DrawerLayout) activty.findViewById(R.id.drawer_layout);
        this.toolbar = (Toolbar) activty.findViewById(R.id.toolbar);
        activty.setSupportActionBar(toolbar);
        final ActionBar ab = activty.getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                activty, drawer, (Toolbar) activty.findViewById(R.id.toolbar),
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);

        toggle.syncState();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        boolean ret = false;
        MenuItem item = (MenuItem) adapter.getItem((int) id);

        if (item == null) {
            item = new MenuItem();
            item.id = -1;
        }

        if (callbacks != null) {
            ret = callbacks.onNavigationItemSelected(item.getId());
        }

        if (!ret)
            return;

        if (drawer != null) {
            drawer.closeDrawer(GravityCompat.START);
        }

        currentNavItem = item.getId();
        adapter.notifyDataSetChanged();
    }


    /**
     * 设置 Navigation  HeaderView
     *
     * @param v
     */
    public void setHeaderView(View v) {
        if (mLvLeftMenu != null) {
            mLvLeftMenu.addHeaderView(v);
        }
    }

    /**
     * 设置 Navigation  FooterView
     *
     * @param v
     */
    public void setFooterView(View v) {
        mLvLeftMenu.addFooterView(v);
    }

    /**
     * 设置HOME 的title
     *
     * @param title
     */
    public void setTitle(String title) {
        if (toolbar != null && !TextUtils.isEmpty(title)) {
            toolbar.setTitle(title);
        }
    }

    /**
     * 设置 HOME NavigationIcon
     *
     * @param res
     */
    public void setIcon(int res) {
        if (toolbar != null && res > 0) {
            toolbar.setNavigationIcon(res);
        }
    }

    /**
     * 触发返回键 判断是否可以关闭 nav
     *
     * @return
     */
    public boolean isDispatchKeyEvent() {

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 设置被选中的 nav item
     *
     * @param id
     */
    public void setCurrentItem(int id) {
        if (callbacks != null) {
            currentNavItem = id;
            callbacks.onNavigationItemSelected(id);
        }
    }

    /**
     * 设置小红点
     *
     * @param id
     * @param unReadCont
     * @param lp
     * @param visibility
     */
    public void setDot(int id, int unReadCont, ViewGroup.LayoutParams lp, int visibility) {
        Object obj = adapter.getItemById(id);
        if (obj != null) {
            MenuItem item = (MenuItem) obj;
            item.dotUnReadCont = unReadCont;
            item.dotLp = lp;
            item.dotVisibility = visibility;
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * 更新骑行中的状态
     */
    public void notifyCyclingState() {
        if (am == null) return;
        LocalActivity currentActivity = am.getCurrentActivity();
        Object obj = adapter.getItemById(R.id.nav_item_cycling);

        if (obj == null) return;
        MenuItem item = (MenuItem) obj;
        item.name = context.getString(R.string.activity_fragment_title);
        if (currentActivity != null) {
            item.state = currentActivity.getState();

            switch (item.state) {
                case ActivityState.STATE_STARTED:
                    item.name += " -";
                    break;
                case ActivityState.STATE_PAUSED:
                case ActivityState.STATE_AUTO_PAUSED:
                    item.name += " -";
                    break;
                default:
                    break;
            }
        } else {
            item.state = ActivityState.STATE_NONE;
        }

        adapter.notifyDataSetChanged();
    }

    public class MenuItem {

        private static final int NO_ICON = 0;
        public static final int TYPE_NORMAL = 0;
        public static final int TYPE_NO_ICON = 1;
        public static final int TYPE_SEPARATOR = 2;

        private int type;
        private String name;
        private int icon;
        private int id;

        private int dotUnReadCont;
        private ViewGroup.LayoutParams dotLp;
        private int dotVisibility = View.GONE;
        private int state = ActivityState.STATE_NONE;

        public MenuItem(int icon, String name, int id) {
            this.icon = icon;
            this.name = name;
            this.id = id;

            if (icon == NO_ICON && TextUtils.isEmpty(name)) {
                type = TYPE_SEPARATOR;
            } else if (icon == NO_ICON) {
                type = TYPE_NO_ICON;
            } else {
                type = TYPE_NORMAL;
            }

            if (type != TYPE_SEPARATOR && TextUtils.isEmpty(name)) {
                throw new IllegalArgumentException("you need set a name for a item");
            }
        }

        public MenuItem(String name) {
            this(NO_ICON, name, 0);
        }

        public MenuItem() {
            this(null);
        }

        public int getId() {
            return id;
        }

    }

    public class MenuItemAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private Context mContext;
        private List<MenuItem> mItems = new ArrayList<MenuItem>();

        public MenuItemAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
            mContext = context;
            mItems.clear();
            mItems.add(new MenuItem(R.drawable.ic_nav_ranking1, context.getString(R.string.ranking_fragment_title), R.id.nav_item_ranking));
            mItems.add(new MenuItem(R.drawable.ic_nav_activity, context.getString(R.string.discovery_fragment_event), R.id.nav_item_activity));

//            mItems.add(new MenuItem(R.drawable.ic_nav_activity, context.getString(R.string.discovery_fragment_railway), R.id.nav_item_railway));
            if (LocaleManager.isChineseTimeZone()) {
                mItems.add(new MenuItem(R.drawable.ic_nav_route, context.getString(R.string.discovery_fragment_good_route), R.id.nav_item_route));
                mItems.add(new MenuItem(R.drawable.ic_nav_cycling_stroe, context.getString(R.string.discovery_fragment_store), R.id.nav_item_store));
            }
            mItems.add(new MenuItem(R.drawable.ic_settings, context.getString(R.string.profile_fragment_action_button_tips), R.id.nav_item_setting));
        }

        @Override
        public int getCount() {
            return mItems.size();
        }

        public Object getItemById(int id) {
            if (id > 0) {

                for (int i = 0; i < mItems.size(); i++) {
                    MenuItem item = mItems.get(i);
                    if (item.getId() == id) {
                        return item;
                    }
                }
            }
            return null;
        }

        @Override
        public Object getItem(int position) {
            if (position >= 0 && position < mItems.size()) {
                return mItems.get(position);
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getViewTypeCount() {
            return 3;
        }

        @Override
        public int getItemViewType(int position) {
            return mItems.get(position).type;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MenuItem item = mItems.get(position);
            switch (item.type) {
                case MenuItem.TYPE_NORMAL:
                    if (convertView == null) {
                        convertView = mInflater.inflate(R.layout.design_drawer_item, parent,
                                false);
                    }
                    ImageView ivIcon = (ImageView) convertView.findViewById(R.id.iv_icon);
                    ivIcon.setImageResource(item.icon);
                    TextView itemView = (TextView) convertView.findViewById(R.id.tv_title);
                    itemView.setText(item.name);
                    convertView.setBackgroundResource(currentNavItem == item.id ?
                            R.drawable.bg_303030 : R.drawable.transparent);
                    TextView tvDot = (TextView) convertView.findViewById(R.id.tv_dot);
                    tvDot.setBackgroundResource(R.drawable.bg_oval);
                    tvDot.setTextColor(context.getResources().getColor(R.color.text_number_color));
                    if (item.getId() != R.id.nav_item_cycling) {

                        tvDot.setText(item.dotUnReadCont > 99 ? "99+" : item.dotUnReadCont + "");
                        if (item.dotLp != null) {
                            tvDot.getLayoutParams().height = item.dotLp.height;
                            tvDot.getLayoutParams().width = item.dotLp.width;
                        }
                        tvDot.setVisibility(item.dotVisibility);
                    } else {
                        switch (item.state) {
                            case ActivityState.STATE_AUTO_PAUSED:
                            case ActivityState.STATE_PAUSED:
                                tvDot.setVisibility(View.VISIBLE);
                                tvDot.setBackgroundResource(R.drawable.transparent);
                                tvDot.setTextColor(context.getResources().getColor(R.color.red_color));
                                tvDot.setText(R.string.notification_riding_stop);
                                break;
                            case ActivityState.STATE_STARTED:
                                tvDot.setVisibility(View.VISIBLE);
                                tvDot.setBackgroundResource(R.drawable.transparent);
                                tvDot.setTextColor(context.getResources().getColor(R.color.red_color));
                                tvDot.setText(R.string.notification_riding);
                                break;
                            default:
                                tvDot.setVisibility(View.GONE);
                                break;
                        }
                    }

                    break;
                case MenuItem.TYPE_NO_ICON:
                    if (convertView == null) {
                        convertView = mInflater.inflate(R.layout.design_drawer_item_subheader,
                                parent, false);
                    }
                    TextView subHeader = (TextView) convertView;
                    subHeader.setText(item.name);
                    break;
                case MenuItem.TYPE_SEPARATOR:
                    if (convertView == null) {
                        convertView = mInflater.inflate(R.layout.design_drawer_item_separator,
                                parent, false);
                    }
                    break;
            }
            return convertView;
        }

    }


    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public interface NavigationCallback {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        boolean onNavigationItemSelected(int id);
    }
}
