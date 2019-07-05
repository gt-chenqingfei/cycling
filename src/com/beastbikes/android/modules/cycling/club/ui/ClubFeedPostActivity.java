package com.beastbikes.android.modules.cycling.club.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beastbikes.android.R;
import com.beastbikes.android.authentication.AVUser;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.android.modules.cycling.club.biz.ClubFeedService;
import com.beastbikes.android.modules.cycling.club.dto.ClubFeed;
import com.beastbikes.android.modules.cycling.club.dto.ClubFeedBase;
import com.beastbikes.android.modules.cycling.club.dto.ClubFeedImageTxtRecord;
import com.beastbikes.android.modules.cycling.club.dto.ClubFeedPost;
import com.beastbikes.android.modules.cycling.club.dto.ClubUser;
import com.beastbikes.android.modules.cycling.club.dto.ImageInfo;
import com.beastbikes.android.modules.cycling.club.dto.RecordInfo;
import com.beastbikes.android.modules.cycling.club.ui.widget.EditPhotoView;
import com.beastbikes.android.modules.user.ui.CyclingRecordActivity;
import com.beastbikes.android.modules.user.util.ActivityDataUtil;
import com.beastbikes.android.utils.StringUtil;
import com.beastbikes.android.widget.MaterialDialog;
import com.beastbikes.framework.android.analytics.annotation.Alias;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.beastbikes.android.widget.multiimageselector.MultiImageSelectorActivity;

@Alias("俱乐部发布feed")
@LayoutResource(R.layout.activity_clubfeed_post)
public class ClubFeedPostActivity extends SessionFragmentActivity implements
        View.OnClickListener, EditPhotoView.MaxPostImageNumber {

    public static final int MAX_SIZE = 140;
    public static final int REQUEST_ACTIVITY_RECORD = 90;
    public static final String EXTRA_CLUB_ID = "club_extra_id";
    public static final int POST_FEED_ID = 1001;

    protected int maxSelected = 9;

    @IdResource(R.id.clubfeed_add_record)
    protected LinearLayout addCyclingRecord;

    @IdResource(R.id.iv_record_img)
    ImageView ivRecordImg;

    @IdResource(R.id.tv_record_name)
    TextView tvRecordName;

    @IdResource(R.id.iv_right_icon)
    ImageView ivRightIcon;

    @IdResource(R.id.edit)
    protected EditText editText;

    @IdResource(R.id.clubfeed_save_cb)
    protected CheckBox cbSyncAblum;

    protected ArrayList<String> paths = new ArrayList<String>();
    protected AlertDialog dialog;
    protected MenuItem menuItemPost;

    protected EditPhotoView editPhoto;
    private RecordInfo recordInfo;

    private String clubId;
    private boolean hasText;
    private boolean hasImage;
    private boolean hasRecord;

    private SharedPreferences sp;

    public final static String NEEDSYNC = "NEEDSYNC";
    private boolean needSync;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.overridePendingTransition(R.anim.activity_in_from_right,
                R.anim.activity_none);
        final ActionBar bar = getSupportActionBar();
        if (null != bar) {
            bar.setDisplayHomeAsUpEnabled(true);
        }
        if (getIntent() != null) {
            clubId = getIntent().getStringExtra(EXTRA_CLUB_ID);
        }
        initView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.clubfeed_post_menu, menu);
        menuItemPost = menu.getItem(0);
        menuItemPost.setEnabled(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void finish() {
        super.finish();
        super.overridePendingTransition(R.anim.activity_none, R.anim.activity_out_to_right);
        InputMethodManager imm = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    protected int getClubPostType() {
        return ClubFeedPost.TYPE_FEED;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_clubfeed_post: {
                ClubFeedService.getInstance().clubFeedPostQueueIn(generatePostClubFeed(), clubId);
                setResult(RESULT_OK);
                this.finish();
                break;
            }
            case android.R.id.home:
                if (hasText || hasImage || hasRecord) {
                    final MaterialDialog dialog = new MaterialDialog(this);
                    dialog.setMessage(R.string.club_post_feed_back_warming);
                    dialog.setPositiveButton(R.string.activity_club_manager_dialog_ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            finish();
                        }
                    });
                    dialog.setNegativeButton(R.string.cancel, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                    return true;
                }
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    protected ClubFeed generatePostClubFeed() {
        String content = editText.getText().toString();

        String sportIdentify = (recordInfo == null) ? "" : recordInfo.getSportIdentify();
        List<ImageInfo> imageInfos = getImageList();

        ClubFeed feed = new ClubFeed();
        feed.setStamp(System.currentTimeMillis());
        feed.setFeedType(ClubFeed.FEED_TYPE_TEXT_IMAGE_RECORD);
        feed.setFid((int) System.currentTimeMillis());
        feed.setState(ClubFeed.STATE_DOING);
        int postType = getClubPostType();
        ClubFeedPost post = new ClubFeedPost(needSync ? 1 : 0, clubId, content, sportIdentify, postType);
        feed.setPost(post);

        ClubFeedImageTxtRecord imageTxtRecord = new ClubFeedImageTxtRecord();

        imageTxtRecord.setRecordInfo(recordInfo);
        imageTxtRecord.setText(post.getContent());
        imageTxtRecord.setFeedType(feed.getFeedType());
        imageTxtRecord.setFid(feed.getFid());
        imageTxtRecord.setClubId(clubId);

        AVUser user = AVUser.getCurrentUser();
        if (user == null)
            return null;
        imageTxtRecord.setUserId(user.getObjectId());
        imageTxtRecord.setUser(new ClubUser(user.getObjectId(), user.getDisplayName(), user.getAvatar()));
        imageTxtRecord.setDate(new Date(feed.getStamp()));
        if (imageInfos == null) {
            imageInfos = new ArrayList<>();
        }
        imageTxtRecord.setImageList(imageInfos);
        imageTxtRecord.setStatus(ClubFeed.STATE_DOING);


        ClubFeedBase base = imageTxtRecord;
        base.setClubId(clubId);
        base.setText(post.getContent());

        feed.setImageTxt(imageTxtRecord);
        return feed;
    }

    private void refreshMenuItem() {
        if (null == menuItemPost)
            return;

        if (hasImage || hasText || hasRecord) {
            this.menuItemPost.setEnabled(true);
        } else {
            this.menuItemPost.setEnabled(false);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_OK:
                switch (requestCode) {
                    case REQUEST_ACTIVITY_RECORD:
                        this.recordInfo = (RecordInfo) data.getSerializableExtra(
                                CyclingRecordActivity.EXTRA_SELECT_ACTIVITY);
                        if (null != recordInfo) {
                            this.hasRecord = true;
                            ivRightIcon.setImageResource(R.drawable.ic_delete);
                            String title = recordInfo.getTitle();
                            if (TextUtils.isEmpty(title) || title.equals("null")) {
                                SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
                                String date = sdf.format(recordInfo.getStartDate());
                                title = ActivityDataUtil.formatDateTime(
                                        ClubFeedPostActivity.this, recordInfo.getStartDate().getTime());
                                tvRecordName.setText(date + title);
                            } else {
                                tvRecordName.setText(title);
                            }

                            if (!TextUtils.isEmpty(recordInfo.getCyclingImage())) {
                                Picasso.with(ClubFeedPostActivity.this).load(recordInfo.getCyclingImage())
                                        .fit().error(R.drawable.ic_feed_cycling).placeholder(R.drawable.ic_feed_cycling)
                                        .centerCrop().into(ivRecordImg);
                            } else {
                                this.ivRecordImg.setImageResource(R.drawable.ic_feed_cycling);
                            }
                        }
                        this.refreshMenuItem();
                        break;
                    case EditPhotoView.REQ_SELECT_IMAGE:
                        if (editPhoto != null) {
                            ArrayList<String> datas = data.getStringArrayListExtra(
                                    MultiImageSelectorActivity.EXTRA_RESULT);
                            if (null != datas && !datas.isEmpty()) {
                                this.hasImage = true;
                            } else {
                                this.hasImage = false;
                            }
                            this.refreshMenuItem();
                            editPhoto.onActivityResult(requestCode, resultCode, data);
                        }
                        break;
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initView() {
        LinearLayout layout = (LinearLayout) findViewById(R.id.add_pic_lay);
        editPhoto = new EditPhotoView(this, this);
        layout.addView(editPhoto);

        this.addCyclingRecord = (LinearLayout) findViewById(R.id.clubfeed_add_record);
        this.addCyclingRecord.setOnClickListener(this);
        this.editText = (EditText) findViewById(R.id.edit);

        this.sp = getSharedPreferences(getUserId(), 0);
        needSync = this.sp.getBoolean(NEEDSYNC, true);
        this.cbSyncAblum = (CheckBox) findViewById(R.id.clubfeed_save_cb);
        if (needSync) {
            cbSyncAblum.setChecked(true);
        } else {
            cbSyncAblum.setChecked(false);
        }


        ivRightIcon = (ImageView) findViewById(R.id.iv_right_icon);
        ivRightIcon.setOnClickListener(this);
        if (ivRightIcon != null) {
            ivRightIcon.setOnClickListener(this);
        }
        cbSyncAblum.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                needSync = isChecked;
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean(NEEDSYNC, isChecked);
                editor.apply();
            }
        });
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (StringUtil.getLength(editText.getText().toString().trim()) > 0 || paths.size() > 0) {
                    hasText = true;
                } else {
                    hasText = false;
                }
                refreshMenuItem();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.clubfeed_add_record:
                Intent intent = new Intent(this, CyclingRecordActivity.class);
                intent.putExtra(CyclingRecordActivity.EXTRA_FORM_CLUB, true);
                startActivityForResult(intent, REQUEST_ACTIVITY_RECORD);
                break;
            case R.id.iv_right_icon: {
                if (recordInfo != null) {
                    this.recordInfo = null;
                    ivRecordImg.setImageResource(R.drawable.ic_feed_cycling);
                    tvRecordName.setText(R.string.club_feed_add_cycling_record);
                    ivRightIcon.setImageResource(R.drawable.ic_arrow_right_icon);
                }
                break;
            }
        }
    }

    protected String getImages() {
        StringBuilder builder = new StringBuilder();
        if (paths != null) {
            for (int i = 0; i < paths.size(); i++) {
                builder.append(paths.get(i));
                if (i <= paths.size() - 1) {
                    builder.append(",");
                }
            }
        }
        return builder.toString();
    }

    protected List<ImageInfo> getImageList() {

        List<ImageInfo> infos = new ArrayList<ImageInfo>();
        for (int i = 0, size = editPhoto.getSelectedFiles().size(); i < size; i++) {

            ImageInfo info = new ImageInfo();
            String path = editPhoto.getSelectedFiles().get(i);
            info.setUrl(path);
            info.setId(System.currentTimeMillis() + "");

            BitmapFactory.Options options = new BitmapFactory.Options();

            /**
             * 最关键在此，把options.inJustDecodeBounds = true;
             * 这里再decodeFile()，返回的bitmap为空，但此时调用options.outHeight时，已经包含了图片的高了
             */
            options.inJustDecodeBounds = true;
            Bitmap bitmap = BitmapFactory.decodeFile(path, options); // 此时返回的bitmap为null
            /**
             *options.outHeight为原始图片的高
             */
            info.setHeight(options.outHeight);
            info.setWidth(options.outWidth);
            info.setMine(options.outMimeType);
            infos.add(info);
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
                bitmap = null;
            }
        }

        return infos;
    }

    @Override
    public int getMaxPostImageNumber() {
        return maxSelected;
    }
}
