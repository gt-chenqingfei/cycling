package com.beastbikes.android.modules.cycling.club.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.beastbikes.android.R;
import com.beastbikes.android.authentication.AVUser;
import com.beastbikes.android.modules.cycling.club.dto.ClubFeed;
import com.beastbikes.android.modules.cycling.club.dto.ClubFeedBase;
import com.beastbikes.android.modules.cycling.club.dto.ClubFeedComment;
import com.beastbikes.android.modules.cycling.club.dto.ClubInfoCompact;
import com.beastbikes.android.modules.cycling.club.dto.ClubUser;
import com.beastbikes.android.modules.cycling.club.ui.view.FeedItemActivity;
import com.beastbikes.android.modules.cycling.club.ui.view.FeedItemBase;
import com.beastbikes.android.modules.cycling.club.ui.view.FeedItemImageTxtRecord;
import com.beastbikes.android.modules.cycling.club.ui.view.FeedItemNotice;
import com.beastbikes.android.modules.cycling.club.ui.widget.ClubFeedNav;
import com.beastbikes.android.modules.cycling.club.ui.widget.CommentEditView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.beastbikes.android.widget.stickylistlibrary.stickylistheaders.StickyListHeadersAdapter;

public class ClubFeedAdapter extends BaseAdapter implements
        StickyListHeadersAdapter, FeedItemBase.ClubFeedListener {
    private static final Logger logger = LoggerFactory.getLogger(ClubFeedAdapter.class);
    List<ClubFeed> clubFeeds = new ArrayList<ClubFeed>();

    private Context context;
    private ClubInfoCompact clubInfoCompact;
    private LayoutInflater mInflater;
    private ClubFeedNav nav;
    private CommentEditView commentEditView;
    private AVUser user;
    private ClubUser clubUser;
    private Map<Integer, View> convertViewCache = new HashMap<>();
    private boolean isMyClub = false;

    public ClubFeedAdapter(Context context, ClubInfoCompact info, CommentEditView view, boolean isMyClub) {
        this.commentEditView = view;
        this.clubInfoCompact = info;
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        user = AVUser.getCurrentUser();
        if (user == null)
            return;
        clubUser = new ClubUser(user.getObjectId(), user.getDisplayName(), user.getAvatar());
        this.isMyClub = isMyClub;
    }

    public void notifyNav(ClubInfoCompact club) {
        if (club != null) {
            this.clubInfoCompact = club;
        }
        if (this.nav != null) {
            this.nav.setClubInfoCompact(this.clubInfoCompact);
        }
    }

    public void dataSetChanged(List<ClubFeed> clubFeeds) {
        if (clubFeeds != null && clubFeeds.size() > 0) {
            this.convertViewCache.clear();
            this.clubFeeds = clubFeeds;
        }
    }

    public void dataSetChanged(List<ClubFeed> clubFeeds, boolean isAppend) {
        if (clubFeeds != null && clubFeeds.size() > 0) {
            this.clubFeeds.addAll(clubFeeds);
        }
    }

    public void notifyDataSetChanged(List<ClubFeed> clubFeeds, boolean isAppend) {
        if (isAppend) {
            dataSetChanged(clubFeeds, isAppend);
        } else {
            dataSetChanged(clubFeeds);
        }
//        logger.info("size = "+this.clubFeeds.size());
        super.notifyDataSetChanged();
    }


    public void notifyDataSetClean() {
        this.convertViewCache.clear();
        super.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return clubFeeds.size();
    }

    @Override
    public Object getItem(int position) {

        return clubFeeds.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ClubFeed feed = (ClubFeed) getItem(position);
        if (feed != null) {
            if (feed.getFid() == -1) {
                //empty view
                convertView = new TextView(context);
                AbsListView.LayoutParams params = new AbsListView.LayoutParams(10, 10);
                convertView.setLayoutParams(params);
            } else {
                ClubFeedHolder feedHolder;
                convertView = convertViewCache.get(position);
                if (convertView == null) {
                    convertView = mInflater.inflate(R.layout.clubfeed_item_base, null);
                    feedHolder = new ClubFeedHolder(convertView, feed);
                    convertViewCache.put(position, convertView);
                    convertView.setTag(feedHolder);
                } else {
                    feedHolder = (ClubFeedHolder) convertView.getTag();
                }

                feedHolder.bind();
            }
        }
        return convertView;
    }


    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            nav = new ClubFeedNav(context, isMyClub);
            convertView = nav;
        }
        if (nav != null) {
            nav.setClubInfoCompact(this.clubInfoCompact);
        }
        return convertView;
    }

    /**
     * Remember that these have to be static, postion=1 should always return
     * the same Id that is.
     */
    @Override
    public long getHeaderId(int position) {
        // return the first character of the country as ID because this is what
        // headers are based upon
        return 0;
    }

    @Override
    public void onDeleteFeed(int id) {
        for (int i = 0; i < clubFeeds.size(); i++) {
            ClubFeed feed = clubFeeds.get(i);
            if (feed.getFid() == id) {
                clubFeeds.remove(i);
                break;
            }
        }
        this.notifyDataSetClean();
    }

    @Override
    public void onComment(int id, ClubFeedComment comment) {

        ClubFeedBase base = getBaseById(id);
        if (base != null) {
            base.addComment(comment);
            notifyDataSetClean();
        }
    }

    @Override
    public void onDeleteComment(int fid, int cid) {
        ClubFeedBase base = getBaseById(fid);
        if (base != null) {
            base.removeComment(cid);
            notifyDataSetClean();
        }
    }

    @Override
    public void onPraise(int id, boolean isPraise) {

        ClubFeedBase base = getBaseById(id);
        if (base != null) {

            if (isPraise) {

                base.addHasLiked(clubUser);
            } else {
                base.removeLiked(user.getObjectId());
            }
            notifyDataSetClean();
        }
    }


    class HeaderViewHolder {
        TextView text;
    }


    private class ClubFeedHolder implements View.OnClickListener {
        ClubFeed clubFeed;

        FeedItemBase feedItemBase = null;

        public ClubFeedHolder(View convertView, ClubFeed feed) {
            this.clubFeed = feed;

            switch (feed.getFeedType()) {
                case ClubFeed.FEED_TYPE_TEXT_IMAGE_RECORD: {
                    feedItemBase = new FeedItemImageTxtRecord(context, convertView, user);
                    break;
                }
                case ClubFeed.FEED_TYPE_ACTIVITY: {
                    feedItemBase = new FeedItemActivity(context, convertView, user);
                    break;
                }
                case ClubFeed.FEED_TYPE_NOTICE: {
                    feedItemBase = new FeedItemNotice(context, convertView, user);
                    break;
                }
                default:
                    feedItemBase = new FeedItemImageTxtRecord(context, convertView, user);
                    break;
            }
            feedItemBase.setClubFeedListener(ClubFeedAdapter.this);
            feedItemBase.setCommentEditView(commentEditView);

            commentEditView.setListener(feedItemBase);
        }

        public void bind() {
            switch (clubFeed.getFeedType()) {
                case ClubFeed.FEED_TYPE_TEXT_IMAGE_RECORD:
                    feedItemBase.bind(clubFeed.getImageTxt());
                    break;

                case ClubFeed.FEED_TYPE_ACTIVITY:
                    feedItemBase.bind(clubFeed.getActivity());
                    break;
                case ClubFeed.FEED_TYPE_NOTICE:
                    feedItemBase.bind(clubFeed.getNotice());
                    break;
            }
            feedItemBase.setMyClub(isMyClub);
        }

        @Override
        public void onClick(View v) {

        }

    }

    public ClubFeedBase getBaseById(int fid) {
        ClubFeedBase base = null;

        for (int i = 0; i < clubFeeds.size(); i++) {
            ClubFeed feed = clubFeeds.get(i);
            if (feed.getFid() == fid) {
                switch (feed.getFeedType()) {
                    case ClubFeed.FEED_TYPE_NOTICE:
                        base = feed.getNotice();
                        break;
                    case ClubFeed.FEED_TYPE_ACTIVITY:
                        base = feed.getActivity();
                        break;
                    case ClubFeed.FEED_TYPE_TEXT_IMAGE_RECORD:
                        base = feed.getImageTxt();
                        break;
                }
                break;
            }
        }
        return base;

    }

}
