package com.beastbikes.android.modules.cycling.club.ui.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beastbikes.android.R;
import com.beastbikes.android.home.HomeActivity;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.android.modules.cycling.club.biz.ClubFeedManager;
import com.beastbikes.android.modules.cycling.club.dto.ClubFeedBase;
import com.beastbikes.android.modules.cycling.club.dto.ClubFeedComment;
import com.beastbikes.android.modules.cycling.club.dto.ClubUser;
import com.beastbikes.android.modules.cycling.club.ui.ClubFeedDetailsActivity;
import com.beastbikes.android.modules.cycling.club.ui.ClubFeedInfoActivity;
import com.beastbikes.android.modules.cycling.club.ui.ThumbsListActivity;
import com.beastbikes.android.modules.cycling.club.ui.widget.CommentEditView;
import com.beastbikes.android.modules.user.ui.ProfileActivity;
import com.beastbikes.android.widget.LinearListView;
import com.beastbikes.android.widget.ListViewForScroll;
import com.beastbikes.android.widget.multiactiontextview.InputObject;
import com.beastbikes.android.widget.multiactiontextview.MultiActionTextView;
import com.beastbikes.android.widget.multiactiontextview.MultiActionTextviewClickListener;
import com.beastbikes.framework.business.BusinessException;
import com.beastbikes.framework.ui.android.widget.CircleImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenqingfei on 15/12/3.
 */
public class FeedItemComment extends LinearLayout implements View.OnClickListener, LinearListView.OnItemClickListener {
    private final int USER_CLICKED = 1;
    private final int REPLY_USER_CLICKED = 2;
    private final int COMMON_CLICKED = 3;
    private final int COMMENT_SHOW_MAX = 5;
    private View likeContainer;
    private LinearListView llvLike;
    private LinearListView llvCommon;
    private ListViewForScroll listViewForScroll;
    private TextView tvShowAll;
    private TextView tvLikeCount;
    private LikeAdapter likeAdapter;
    private CommentAdapter commentAdapter;
    private CommentAdapter1 commentAdapter1;
    private LayoutInflater inflater;
    private List<ClubUser> users = new ArrayList<ClubUser>();
    private ClubFeedBase feedBase;
    private CommentEditView commentEditView;
    private String userId;
    private FeedItemBase baseItem;

    public FeedItemComment(Context context, String userId, FeedItemBase baseItem) {
        super(context);
        this.userId = userId;
        this.baseItem = baseItem;
        this.inflater = LayoutInflater.from(context);
        initCommonView();
    }

    public void setCommentEditView(CommentEditView editView) {
        this.commentEditView = editView;
    }

    private void initCommonView() {
        LayoutInflater.from(this.getContext()).inflate(R.layout.clubfeed_comment, this);
        likeContainer = findViewById(R.id.like_container);
        listViewForScroll = (ListViewForScroll) findViewById(R.id.common_list1);
        llvLike = (LinearListView) findViewById(R.id.like_list);
        llvCommon = (LinearListView) findViewById(R.id.common_list);
        tvShowAll = (TextView) findViewById(R.id.show_all);
        tvShowAll.setOnClickListener(this);
        tvLikeCount = (TextView) findViewById(R.id.tv_like_count);

        likeAdapter = new LikeAdapter();
        if (getContext() instanceof ClubFeedInfoActivity || getContext() instanceof HomeActivity) {
            commentAdapter = new CommentAdapter();
            llvCommon.setAdapter(commentAdapter);
        } else {
            commentAdapter1 = new CommentAdapter1();
            listViewForScroll.setAdapter(commentAdapter1);
        }

        llvLike.setAdapter(likeAdapter);
        llvLike.setOnItemClickListener(this);
    }

    public void bind(ClubFeedBase base) {
        if (base != null) {
            this.feedBase = base;
            this.users = base.getLikeList();
            if (getContext() instanceof ClubFeedInfoActivity || getContext() instanceof HomeActivity) {
                this.commentAdapter.notifyDataSetChanged();
                tvShowAll.setText(String.format(getContext().
                        getString(R.string.clubfeed_show_comment), base.getCommentCount()));
                tvShowAll.setVisibility(base.getCommentCount() >= 5 ? VISIBLE : GONE);
            } else {
                this.commentAdapter1.notifyDataSetChanged();
            }
            this.likeAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == tvShowAll) {
            Intent it = new Intent(getContext(), ClubFeedDetailsActivity.class);
            it.putExtra(ClubFeedDetailsActivity.EXTRA_FID, feedBase.getFid());
            it.putExtra(ClubFeedDetailsActivity.EXTRA_IS_MY_CLUB,baseItem.isMyClub);
            getContext().startActivity(it);
        }
    }

    @Override
    public void onItemClick(LinearListView parent, View view, int position, long id) {
        if (parent == llvLike) {
            if (position == likeAdapter.getCount() - 1) {
                final Intent intent = new Intent(getContext(), ThumbsListActivity.class);
                intent.putExtra(ThumbsListActivity.EXTRA_FEEDID, feedBase.getFid());
                getContext().startActivity(intent);
            } else {
                ClubUser user = (ClubUser) likeAdapter.getItem(position);
                if (user != null) {
                    if (getContext() != null) {
                        final Intent intent = new Intent();
                        intent.setClass(getContext(), ProfileActivity.class);
                        intent.putExtra(ProfileActivity.EXTRA_USER_ID, user.getUserId());
                        intent.putExtra(ProfileActivity.EXTRA_AVATAR, user.getAvatar());
                        intent.putExtra(ProfileActivity.EXTRA_NICK_NAME,user.getNickName());
                        intent.putExtra(ProfileActivity.EXTRA_REMARKS,user.getRemarks());
                        getContext().startActivity(intent);
                    }
                }
            }
        }
    }

    class CommentAdapter1 extends BaseAdapter implements MultiActionTextviewClickListener {
        @Override
        public int getCount() {
            int count = 0;
            if (feedBase != null && feedBase.getCommentList() != null) {
                count = feedBase.getCommentList().size();
            }
            return count;
        }

        @Override
        public Object getItem(int position) {
            return feedBase.getCommentList().get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.clubfeed_item_comment, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if (holder != null) {
                ClubFeedComment comment = (ClubFeedComment) getItem(position);
                holder.bind(comment, this);
            }
            return convertView;
            //return super.getView(position,convertView,parent);
        }

        @Override
        public void onTextClick(InputObject inputObject) {
            if (inputObject != null) {
                if (inputObject.getOperationType() == COMMON_CLICKED) {
                    ClubFeedComment common = (ClubFeedComment) inputObject.getInputObject();
//                    Toasts.show(getContext(), common.getContent());
                } else {
                    ClubUser user = (ClubUser) inputObject.getInputObject();
                    if (getContext() != null && user != null) {
                        final Intent intent = new Intent();
                        intent.setClass(getContext(), ProfileActivity.class);
                        intent.putExtra(ProfileActivity.EXTRA_USER_ID, user.getUserId());
                        intent.putExtra(ProfileActivity.EXTRA_AVATAR, user.getAvatar());
                        intent.putExtra(ProfileActivity.EXTRA_NICK_NAME,user.getNickName());
                        intent.putExtra(ProfileActivity.EXTRA_REMARKS,user.getRemarks());
                        getContext().startActivity(intent);
                    }
                }
            }
        }


        class ViewHolder {
            TextView tvName = null;
            TextView tvDate = null;
            TextView tvContent = null;
            CircleImageView image = null;

            ViewHolder(View convertView) {
                tvName = (TextView) convertView.findViewById(R.id.tv_name);
                tvDate = (TextView) convertView.findViewById(R.id.tv_date);
                tvContent = (TextView) convertView.findViewById(R.id.tv_content);
                image = (CircleImageView) convertView.findViewById(R.id.image);
            }

            public void bind(ClubFeedComment comment, MultiActionTextviewClickListener listener) {
                if (comment != null) {
                    final ClubUser u = comment.getUser();
                    if (u != null) {
                        tvName.setText(u.getNickName());
                        if (!TextUtils.isEmpty(u.getAvatar())) {
                            Picasso.with(getContext()).load(u.getAvatar()).fit().error(R.drawable.ic_avatar)
                                    .placeholder(R.drawable.ic_avatar).centerCrop().into(image);
                        } else {
                            image.setImageResource(R.drawable.ic_avatar);
                        }
                        image.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final Intent intent = new Intent();
                                intent.setClass(getContext(), ProfileActivity.class);
                                intent.putExtra(ProfileActivity.EXTRA_USER_ID, u.getUserId());
                                intent.putExtra(ProfileActivity.EXTRA_AVATAR, u.getAvatar());
                                intent.putExtra(ProfileActivity.EXTRA_NICK_NAME,u.getNickName());
                                intent.putExtra(ProfileActivity.EXTRA_REMARKS,u.getRemarks());
                                getContext().startActivity(intent);
                            }
                        });
                    }

                    tvDate.setText(comment.getCreateAt());

                    String textUser = "";
                    String textUserReply = "";
                    String textReply = "";
                    String textCommon = comment.getContent();
                    SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
                    int startSpan = 0;
                    int endSpan = 0;

                    if (comment.getReplyUser() != null) {

                        textUserReply = comment.getReplyUser().getNickName();
                        textReply = getContext().getString(R.string.clubfeed_comment_reply);
                        stringBuilder.append(textReply);
                        stringBuilder.append(textUserReply);

                        startSpan = endSpan + textReply.length();
                        endSpan = startSpan + textUserReply.length();

                        //  Log.d(TAG, " start : endspan::" + startSpan + " : " + endSpan);
                        InputObject replyUserClick = new InputObject();
                        replyUserClick.setStartSpan(startSpan);
                        replyUserClick.setEndSpan(endSpan);
                        replyUserClick.setStringBuilder(stringBuilder);
                        replyUserClick.setInputObject(comment.getReplyUser());
                        replyUserClick.setMultiActionTextviewClickListener(listener);
                        replyUserClick.setOperationType(REPLY_USER_CLICKED);
                        MultiActionTextView.addActionOnTextViewWithoutLink(replyUserClick);
                    }

                    if (!TextUtils.isEmpty(textCommon)) {
                        stringBuilder.append(textCommon);
                        startSpan = endSpan;
                        endSpan = endSpan + textCommon.length();

                        InputObject commonClick = new InputObject();
                        commonClick.setStartSpan(startSpan);
                        commonClick.setEndSpan(endSpan);
                        commonClick.setInputObject(comment);
                        commonClick.setStringBuilder(stringBuilder);
                        commonClick.setMultiActionTextviewClickListener(listener);
                        commonClick.setOperationType(COMMON_CLICKED);
                        MultiActionTextView.addActionOnTextViewWithoutLink(commonClick);

                        CharacterStyle span1 = new ForegroundColorSpan(0xff666666);
                        stringBuilder.setSpan(span1, startSpan, endSpan, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }

                    MultiActionTextView.setSpannableText(tvContent,
                            stringBuilder, 0xffbbbbbb);
                }

            }

        }
    }

    class CommentAdapter extends BaseAdapter implements MultiActionTextviewClickListener {

        @Override
        public int getCount() {
            int count = 0;
            if (feedBase != null && feedBase.getCommentList() != null) {
                count = feedBase.getCommentList().size() > 5 ? 5 : feedBase.getCommentList().size();
            }
            return count;
        }

        @Override
        public Object getItem(int position) {
            return feedBase.getCommentList().get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ClubFeedComment comment = (ClubFeedComment) getItem(position);
            if (comment != null) {

                TextView textView = new TextView(getContext());
                LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                params.bottomMargin = getResources().getDimensionPixelSize(R.dimen.margin_3dp);
                textView.setLayoutParams(params);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, getResources().getInteger(R.integer.font_comment_size));
                textView.setTextColor(0xff666666);

                String textUser = "";
                String textUserReply = "";
                String textReply = "";
                String textCommon = comment.getContent();
                SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
                int startSpan = 0;
                int endSpan = 0;


                if (comment.getUser() != null) {
                    textUser = comment.getUser().getNickName() + ":  ";
                    stringBuilder.append(textUser);
                    endSpan = textUser.length();

                    InputObject userClick = new InputObject();
                    userClick.setStartSpan(startSpan);
                    userClick.setEndSpan(endSpan);
                    userClick.setInputObject(comment.getUser());
                    userClick.setStringBuilder(stringBuilder);
                    userClick.setMultiActionTextviewClickListener(this);
                    userClick.setOperationType(USER_CLICKED);
                    MultiActionTextView.addActionOnTextViewWithoutLink(userClick);
                }

                if (comment.getReplyUser() != null) {
                    textUserReply = comment.getReplyUser().getNickName();
                    textReply = getContext().getString(R.string.clubfeed_comment_reply);
                    stringBuilder.append(textReply);
                    stringBuilder.append(textUserReply);

                    startSpan = endSpan + textReply.length();
                    endSpan = startSpan + textUserReply.length();

                    //  Log.d(TAG, " start : endspan::" + startSpan + " : " + endSpan);
                    InputObject replyUserClick = new InputObject();
                    replyUserClick.setStartSpan(startSpan);
                    replyUserClick.setEndSpan(endSpan);
                    replyUserClick.setStringBuilder(stringBuilder);
                    replyUserClick.setInputObject(comment.getReplyUser());
                    replyUserClick.setMultiActionTextviewClickListener(this);
                    replyUserClick.setOperationType(REPLY_USER_CLICKED);
                    MultiActionTextView.addActionOnTextViewWithoutLink(replyUserClick);
                }


                if (!TextUtils.isEmpty(textCommon)) {
                    stringBuilder.append(textCommon);
                    startSpan = endSpan;
                    endSpan = endSpan + textCommon.length();

                    InputObject commonClick = new InputObject();
                    commonClick.setStartSpan(startSpan);
                    commonClick.setEndSpan(endSpan);
                    commonClick.setInputObject(comment);
                    commonClick.setStringBuilder(stringBuilder);
                    commonClick.setMultiActionTextviewClickListener(this);
                    commonClick.setOperationType(COMMON_CLICKED);
                    MultiActionTextView.addActionOnTextViewWithoutLink(commonClick);

                    CharacterStyle span1 = new ForegroundColorSpan(0xff666666);
                    stringBuilder.setSpan(span1, startSpan, endSpan, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }


                MultiActionTextView.setSpannableText(textView,
                        stringBuilder, 0xffbbbbbb);


                return textView;
            }
            return null;
        }

        @Override
        public void onTextClick(InputObject inputObject) {
            if (inputObject != null) {
                if (inputObject.getOperationType() == COMMON_CLICKED) {
                    final ClubFeedComment comment = (ClubFeedComment) inputObject.getInputObject();
                    if (comment != null) {
                        if (comment.getUser().getUserId().equals(userId)) {
                            String[] items = getContext().getResources().getStringArray(
                                    R.array.context_menu_ok_cancel);
                            android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(getContext()).setItems(items,
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            switch (which) {
                                                case 0: {
                                                    dialog.dismiss();
                                                    deleteComment(comment.getCid(), comment.getFid());
                                                    break;
                                                }
                                                case 1: {
                                                    dialog.dismiss();
                                                    break;
                                                }
                                            }
                                        }
                                    }).create();
                            dialog.show();


                        } else {
                            commentEditView.setParams(comment.getFid(), comment.getCid());
                            commentEditView.setTextHint(getContext().getString(R.string.clubfeed_comment_reply) +
                                    ":  " + comment.getUser().getNickName());
                            commentEditView.toggleSoftInput();
                        }
                    }
                } else {
                    ClubUser user = (ClubUser) inputObject.getInputObject();
                    if (getContext() != null && user != null) {
                        final Intent intent = new Intent();
                        intent.setClass(getContext(), ProfileActivity.class);
                        intent.putExtra(ProfileActivity.EXTRA_USER_ID, user.getUserId());
                        intent.putExtra(ProfileActivity.EXTRA_NICK_NAME, user.getNickName());
                        intent.putExtra(ProfileActivity.EXTRA_AVATAR, user.getAvatar());
                        intent.putExtra(ProfileActivity.EXTRA_REMARKS, user.getRemarks());
                        getContext().startActivity(intent);
                    }
                }
            }
        }
    }

    class LikeAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            int count = 1;
            int likeCount = 0;
            if (users != null && users.size() > 0) {
                likeCount = users.size();
                count += likeCount;
            }
            if(feedBase != null) {
                tvLikeCount.setText(String.format(getContext().getString(R.string.clubfeed_liked), feedBase.getLikeCount()));
            }
            likeContainer.setVisibility(likeCount > 0 ? View.VISIBLE : View.GONE);
            return count;
        }

        @Override
        public Object getItem(int position) {
            return users.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            CircleImageView image = new CircleImageView(getContext());
            LayoutParams params = new LayoutParams((int) getResources().getDimension(R.dimen.avatar_like),
                    (int) getResources().getDimension(R.dimen.avatar_like));
            params.setMargins(0, 0, (int) getResources().getDimension(R.dimen.avatar_margin), 0);
            image.setScaleType(ImageView.ScaleType.CENTER_CROP);
            image.setLayoutParams(params);
            if (position < getCount() - 1) {
                ClubUser user = (ClubUser) getItem(position);
                if (user != null) {
                    if (!TextUtils.isEmpty(user.getAvatar())) {
                        Picasso.with(getContext()).load(user.getAvatar()).fit().error(R.drawable.ic_avatar)
                                .placeholder(R.drawable.ic_avatar).centerCrop().into(image);
                    } else {
                        image.setImageResource(R.drawable.ic_avatar);
                    }
                    return image;
                }

            } else {
                image.setImageResource(R.drawable.ic_paire_more);

                return image;
            }
            return null;
        }
    }

    public void deleteComment(final int cid, final int fid) {
        final SessionFragmentActivity activity = (SessionFragmentActivity) getContext();
        activity.getAsyncTaskQueue().add(
                new AsyncTask<String, Void, Boolean>() {

                    @Override
                    protected Boolean doInBackground(String... params) {

                        try {
                            return new ClubFeedManager(activity).deleteClubComment(cid);
                        } catch (BusinessException e) {
                            return false;
                        }
                    }

                    @Override
                    protected void onPostExecute(Boolean result) {

                        if (result) {
                            if (baseItem != null && baseItem.listener != null) {
                                baseItem.listener.onDeleteComment(fid, cid);
                            }
                        }
                    }

                });
    }
}