package com.beastbikes.android.modules.cycling.club.ui.view;

import android.content.Context;
import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beastbikes.android.R;
import com.beastbikes.android.modules.cycling.club.dto.ClubFeedComment;
import com.beastbikes.android.modules.cycling.club.dto.ClubPhotoDTO;
import com.beastbikes.android.modules.cycling.club.dto.ClubUser;
import com.beastbikes.android.modules.cycling.club.ui.ClubFeedDetailsActivity;
import com.beastbikes.android.modules.cycling.club.ui.ThumbsListActivity;
import com.beastbikes.android.modules.cycling.club.ui.widget.CommentEditView;
import com.beastbikes.android.modules.user.ui.ProfileActivity;
import com.beastbikes.android.widget.LinearListView;
import com.beastbikes.android.widget.ListViewForScroll;
import com.beastbikes.android.widget.multiactiontextview.InputObject;
import com.beastbikes.android.widget.multiactiontextview.MultiActionTextView;
import com.beastbikes.android.widget.multiactiontextview.MultiActionTextviewClickListener;
import com.beastbikes.framework.android.utils.DimensionUtils;
import com.beastbikes.framework.ui.android.utils.Toasts;
import com.beastbikes.framework.ui.android.widget.CircleImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenqingfei on 15/12/3.
 */
public class PhotoItemComment extends LinearLayout implements View.OnClickListener, LinearListView.OnItemClickListener {
    private final int USER_CLICKED = 1;
    private final int REPLY_USER_CLICKED = 2;
    private final int COMMON_CLICKED = 3;
    private View likeContainer;
    private LinearListView llvLike;
    private LinearListView llvCommon;
    private ListViewForScroll listViewForScroll;
    private TextView tvShowAll;
    private TextView tvLikeCount;
    private LikeAdapter likeAdapter;
    private CommentAdapter1 commentAdapter1;
    private LayoutInflater inflater;
    private List<ClubUser> users = new ArrayList<ClubUser>();
    private List<ClubFeedComment> commons = new ArrayList<ClubFeedComment>();
    private ClubPhotoDTO feedBase;
    private CommentEditView commentEditView;

    public PhotoItemComment(Context context, CommentEditView commentEditView) {
        super(context);
        this.inflater = LayoutInflater.from(context);
        this.commentEditView = commentEditView;
        initCommonView();
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
        commentAdapter1 = new CommentAdapter1();
        listViewForScroll.setAdapter(commentAdapter1);

        llvLike.setAdapter(likeAdapter);


        llvLike.setOnItemClickListener(this);
    }

    public void bind(ClubPhotoDTO base) {
        if (base != null) {
            this.feedBase = base;
            this.commons = base.getCommentList();
            if (null == commons || this.commons.size() <= 0) {
                this.listViewForScroll.setVisibility(GONE);
            } else {
                this.listViewForScroll.setVisibility(VISIBLE);
            }
            this.users = base.getLikeUserList();
            if (null == this.users || this.users.size() <= 0) {
                this.llvLike.setVisibility(GONE);
            } else {
                this.llvLike.setVisibility(VISIBLE);
            }
            this.commentAdapter1.notifyDataSetChanged();
            this.likeAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == tvShowAll) {
            Intent it = new Intent(getContext(), ClubFeedDetailsActivity.class);
            it.putExtra(ClubFeedDetailsActivity.EXTRA_FID, feedBase.getPhotoId());
//            it.getBooleanExtra(ClubFeedDetailsActivity.EXTRA_IS_MY_CLUB,baseItem.isMyClub);
            getContext().startActivity(it);
        }
    }

    @Override
    public void onItemClick(LinearListView parent, View view, int position, long id) {
        if (parent == llvLike) {
            if(position < likeAdapter.getCount() - 1) {
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
            } else{
                final Intent intent = new Intent( getContext(),ThumbsListActivity.class);
                intent.putExtra(ThumbsListActivity.EXTRA_PHOTOID,feedBase.getPhotoId());
                getContext().startActivity(intent);
            }

        }
    }

    class CommentAdapter1 extends BaseAdapter implements MultiActionTextviewClickListener {
        @Override
        public int getCount() {
            int count = 0;
            if (commons != null) {
                count = commons.size();
            }
            return count;
        }

        @Override
        public Object getItem(int position) {
            return commons.get(position);
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
                    commentEditView.togglePhotoSoftInput();
                    commentEditView.setParams(common.getFid(), common.getCid());
                    Toasts.show(getContext(), common.getContent());
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
                    Toasts.show(getContext(), user.getUserId() + "name=" + user.getNickName());
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
                    ClubUser u = comment.getUser();
                    if (u != null) {
                        tvName.setText(u.getNickName());
                        if (!TextUtils.isEmpty(u.getAvatar())) {
                            Picasso.with(getContext()).load(u.getAvatar()).fit().error(R.drawable.ic_avatar)
                                    .placeholder(R.drawable.ic_avatar).centerCrop().into(image);
                        } else {
                            image.setImageResource(R.drawable.ic_avatar);
                        }
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
            if (commons != null) {
                count = commons.size();
            }
            return count;
        }

        @Override
        public Object getItem(int position) {
            return commons.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ClubFeedComment common = (ClubFeedComment) getItem(position);
            if (common != null) {

                TextView textView = new TextView(getContext());
                LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                textView.setLayoutParams(params);
                textView.setTextSize(getResources().getDimensionPixelSize(R.dimen.font_5));
                textView.setTextColor(0xff666666);
                String textUser = "";
                String textUserReply = "";
                String textReply = "";
                String textCommon = common.getContent();
                SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
                int startSpan = 0;
                int endSpan = 0;


                if (common.getUser() != null) {
                    textUser = common.getUser().getNickName() + ":";
                    stringBuilder.append(textUser);
                    endSpan = textUser.length();

                    InputObject userClick = new InputObject();
                    userClick.setStartSpan(startSpan);
                    userClick.setEndSpan(endSpan);
                    userClick.setInputObject(common.getUser());
                    userClick.setStringBuilder(stringBuilder);
                    userClick.setMultiActionTextviewClickListener(this);
                    userClick.setOperationType(USER_CLICKED);
                    MultiActionTextView.addActionOnTextViewWithoutLink(userClick);
                }

                if (common.getReplyUser() != null) {
                    textUserReply = common.getReplyUser().getNickName();
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
                    replyUserClick.setInputObject(common.getReplyUser());
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
                    commonClick.setInputObject(common);
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
                    ClubFeedComment common = (ClubFeedComment) inputObject.getInputObject();


                    Toasts.show(getContext(), common.getContent());
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
                    Toasts.show(getContext(), user.getUserId() + "name=" + user.getNickName());
                }
            }
        }
    }

    class LikeAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            int count = 1;
            int likeCount =0;
            if (users != null && users.size()>0) {
                likeCount = users.size();
                count += likeCount;
            }
            tvLikeCount.setText(String.format(getContext().getString(R.string.clubfeed_liked), likeCount));
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

            if(position < getCount()-1){
                ClubUser user = (ClubUser) getItem(position);
                if (user != null) {
                    if (!TextUtils.isEmpty(user.getAvatar())) {
                        int width = DimensionUtils.dip2px(getContext(), 24);
                        Picasso.with(getContext()).load(user.getAvatar()).error(R.drawable.ic_avatar)
                                .resize(width, width).placeholder(R.drawable.ic_avatar).centerCrop().into(image);
                    } else {
                        image.setImageResource(R.drawable.ic_avatar);
                    }
                    return image;
                }
            }
            else{
                image.setImageResource(R.drawable.ic_paire_more);
                return image;
            }

            return null;
        }
    }
}