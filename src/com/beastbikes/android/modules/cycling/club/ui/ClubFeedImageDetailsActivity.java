package com.beastbikes.android.modules.cycling.club.ui;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.beastbikes.android.BeastBikes;
import com.beastbikes.android.R;
import com.beastbikes.android.authentication.AVUser;
import com.beastbikes.android.dialog.LoadingDialog;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.android.modules.cycling.club.biz.ClubFeedManager;
import com.beastbikes.android.modules.cycling.club.dto.ClubPhotoDTO;
import com.beastbikes.android.modules.cycling.club.ui.widget.SaveImagePopupWindow;
import com.beastbikes.android.widget.MaterialDialog;
import com.beastbikes.android.widget.multiimageselector.utils.TouchImageView;
import com.beastbikes.framework.android.analytics.annotation.Alias;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.android.res.annotation.MenuResource;
import com.beastbikes.framework.ui.android.lib.pulltorefresh.DensityUtil;
import com.beastbikes.framework.ui.android.utils.Toasts;
import com.beastbikes.framework.ui.android.widget.CircleImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

@Alias("俱乐部发布动态查看照片详情")
@MenuResource(R.menu.clubfeed_image_deatils_menu)
@LayoutResource(R.layout.activity_clubfeed_image_deatils)
public class ClubFeedImageDetailsActivity extends SessionFragmentActivity implements View.OnClickListener {

    public static final String EXTRA_IMAGES = "images";
    public static final String EXTRA_POS = "position";
    public static final String EXTRA_RESULT = "select_result";
    public static final String EXTRA_PHOTO_IMAGES = "gallery_photos";
    public static final String EXTRA_CANDEL = "canDel";
    public static final String EXTRA_COMPRESS = "compress";

    @IdResource(R.id.club_feed_image_desc_view)
    private LinearLayout descView;
    @IdResource(R.id.club_feed_image_avatar)
    CircleImageView avatar;
    @IdResource(R.id.club_feed_user_name)
    private TextView nickNameTv;
    @IdResource(R.id.club_feed_image_praise)
    private ImageButton praise;
    @IdResource(R.id.club_feed_image_praise_count)
    private TextView praiseTv;
    @IdResource(R.id.club_feed_image_commend_count)
    private TextView commendTv;
    @IdResource(R.id.club_feed_image_desc)
    private TextView descTv;

    private ArrayList<String> imageUrls = null;
    private ArrayList<ClubPhotoDTO> photos = null;
    private ViewPager pager;
    private int pagerPosition = 0;

    private AdapterImagePager adapterImagePager;
    private ClubFeedManager clubFeedManager;
    private Menu menu;
    private String clubManagerId;
    private boolean fromAlbum;
    private boolean canDel = true;
    private boolean isCompress  = true;
    private SaveImagePopupWindow imagePopupWindow;
    private String myUserId;

//    private Menu menu;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        final android.support.v7.app.ActionBar bar = getSupportActionBar();
        if (null != bar) {
            bar.setDisplayHomeAsUpEnabled(true);
        }
        AVUser avUser = AVUser.getCurrentUser();
        if (avUser == null)
            return;


        myUserId = avUser.getObjectId();
        pager = (ViewPager) findViewById(R.id.pager);
        this.clubFeedManager = new ClubFeedManager(this);
        this.praise.setOnClickListener(this);
        this.commendTv.setOnClickListener(this);
        this.descView.setOnClickListener(this);
        bundle = this.getIntent().getExtras();
        if (bundle != null) {
            canDel = bundle.getBoolean(EXTRA_CANDEL, true);
            isCompress = bundle.getBoolean(EXTRA_COMPRESS, true);
            imageUrls = bundle.getStringArrayList(EXTRA_IMAGES);
            if (null == imageUrls || imageUrls.size() <= 0) {
                this.imageUrls = new ArrayList<>();
                this.photos = (ArrayList<ClubPhotoDTO>) bundle.getSerializable(EXTRA_PHOTO_IMAGES);
                this.clubManagerId = bundle.getString(ClubGalleryActivity.EXTRA_CLUB_MANAGER_ID);
                this.fromAlbum = true;
                for (ClubPhotoDTO dto : photos) {
                    imageUrls.add(dto.getImageUrl());
                }
            }

            if (null == photos || photos.size() <= 0) {
                this.descView.setVisibility(View.GONE);
            } else {
                this.descView.setVisibility(View.VISIBLE);
            }

            pagerPosition = bundle.getInt(EXTRA_POS, 0);

            pager.setAdapter(adapterImagePager = new AdapterImagePager(this, imageUrls));
            pager.setCurrentItem(pagerPosition);
            refreshView(pagerPosition);

            setTitle((pagerPosition + 1) + "/" + imageUrls.size());
            pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    pagerPosition = position;
                    setTitle(position + 1 + "/" + imageUrls.size());
                    refreshView(position);
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem menuItem = menu.findItem(R.id.menu_clubfeed_image_del);
        if (menuItem == null)
            return super.onPrepareOptionsMenu(menu);
        if (!canDel) {
//            menuItem.setVisible(false);
            hiddenMenu();
        } else {
//            menuItem.setVisible(true);
            showMenu();
        }
        return super.onPrepareOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_clubfeed_image_del: {
                //删除图片

                if (fromAlbum) {
                    final MaterialDialog deleteDialog = new MaterialDialog(this);
                    deleteDialog.setMessage(R.string.dialog_sure_or_delete);
                    deleteDialog.setPositiveButton(R.string.activity_alert_dialog_text_ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            deleteDialog.dismiss();
                            deleteClubPhoto(pagerPosition);
                        }
                    }).setNegativeButton(R.string.cancel, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            deleteDialog.dismiss();
                        }
                    }).show();
                    break;
                }

                adapterImagePager.images.remove(pagerPosition);
                adapterImagePager.notifyDataSetChanged();
                //pager.removeViewAt(pagerPosition);
                pagerPosition = pagerPosition--;
                if (adapterImagePager.images.size() == 0) {
                    finish();
                } else {
                    setTitle(pagerPosition + 1 + "/" + imageUrls.size());
                }
                if (pagerPosition >= 0 && adapterImagePager.images.size() > 0)
                    pager.setCurrentItem(pagerPosition, true);
                break;
            }
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.finish();
        return super.onTouchEvent(event);
    }

    @Override
    public void finish() {
        Intent data = new Intent();
        if (fromAlbum) {
            data.putExtra(EXTRA_PHOTO_IMAGES, photos);
        } else {
            data.putStringArrayListExtra(EXTRA_RESULT, imageUrls);
        }
        setResult(RESULT_OK, data);
        super.finish();
        super.overridePendingTransition(R.anim.activity_none, R.anim.activity_out_to_right);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.club_feed_image_praise:
                this.praiseImage(pagerPosition);
                break;
            case R.id.club_feed_image_commend_count:
                this.start2CommentActivity(pagerPosition);
                break;
        }
    }

    private void deleteClubPhoto(final int position) {
        if (null == this.imageUrls || this.imageUrls.size() <= 0) {
            return;
        }

        if (null == this.photos || this.photos.isEmpty()) {
            return;
        }

        final int photoId = this.photos.get(position).getPhotoId();
        this.getAsyncTaskQueue().add(
                new AsyncTask<String, Void, Boolean>() {
                    LoadingDialog loadingDialog = null;

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        loadingDialog = new LoadingDialog(ClubFeedImageDetailsActivity.this,
                                getString(R.string.loading_msg),
                                true);
                        loadingDialog.show();
                    }

                    @Override
                    protected Boolean doInBackground(String... params) {
                        return clubFeedManager.deleteClubPhoto(photoId);
                    }

                    @Override
                    protected void onPostExecute(Boolean result) {
                        if (loadingDialog != null) {
                            loadingDialog.dismiss();
                        }
                        if (result) {
                            adapterImagePager.images.remove(pagerPosition);
                            adapterImagePager.notifyDataSetChanged();
                            //pager.removeViewAt(pagerPosition);
                            pagerPosition = pagerPosition--;
                            if (adapterImagePager.images.size() == 0) {
                                finish();
                                return;
                            }
                            if (pagerPosition >= 0 && adapterImagePager.images.size() > 0)
                                pager.setCurrentItem(pagerPosition, true);

                            photos.remove(pagerPosition);
                            refreshView(pagerPosition);
                        } else {
                            Toasts.show(ClubFeedImageDetailsActivity.this, getString(R.string.delete_err));
                        }
                    }

                });
    }

    private void hiddenMenu() {
        if (null != this.menu) {
            for (int i = 0; i < this.menu.size(); i++) {
                this.menu.getItem(i).setVisible(false);
                this.menu.getItem(i).setEnabled(false);
            }
        }
    }

    private void showMenu() {
        if (null != this.menu) {
            for (int i = 0; i < this.menu.size(); i++) {
                this.menu.getItem(i).setVisible(true);
                this.menu.getItem(i).setEnabled(true);
            }
        }
    }

    private void refreshView(int position) {
        if (fromAlbum) {
            descView.setVisibility(View.VISIBLE);
            if (position < photos.size()) {
                ClubPhotoDTO dto = photos.get(position);
                if (null != dto) {
                    if (myUserId.equals(dto.getUserId()) || this.clubManagerId.
                            equals(myUserId)) {
                        this.showMenu();
                    } else {
                        this.hiddenMenu();
                    }
                    String desc = dto.getContent();
                    if (TextUtils.isEmpty(desc)) {
                        descTv.setVisibility(View.GONE);
                    } else {
                        descTv.setVisibility(View.VISIBLE);
                        descTv.setText(desc);
                    }
                    nickNameTv.setText(dto.getNickName());
                    int likeNum = dto.getLikeNum();
                    if (likeNum <= 0) {
                        praiseTv.setText("");
                    } else {
                        praiseTv.setText(String.valueOf(dto.getLikeNum()));
                    }
                    if (dto.isHasLiked()) {
                        praise.setSelected(true);
                    } else {
                        praise.setSelected(false);
                    }
                    int commend = dto.getCommentNum();
                    if (commend <= 0) {
                        commendTv.setText("");
                    } else {
                        commendTv.setText(String.valueOf(dto.getCommentNum()));
                    }

                    if (!TextUtils.isEmpty(dto.getAvatar())) {
                        Picasso.with(ClubFeedImageDetailsActivity.this).load(dto.getAvatar()).fit()
                                .placeholder(R.drawable.ic_avatar).error(R.drawable.ic_avatar)
                                .into(avatar);
                    } else {
                        this.avatar.setImageResource(R.drawable.ic_avatar);
                    }
                }
            }
        } else {
            descView.setVisibility(View.GONE);
        }
    }

    /**
     * 喜欢or取消喜欢
     *
     * @param position
     */
    private void praiseImage(final int position) {


        this.getAsyncTaskQueue().add(new AsyncTask<Integer, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Integer... params) {
                if (position < 0 || position >= photos.size()) {
                    return false;
                }

                final ClubPhotoDTO photo = photos.get(position);
                if (null == photo) {
                    return false;
                }

                int command = photo.isHasLiked() ? 1 : 0;
                return clubFeedManager.likeClubPhoto(photo.getPhotoId(), command);
            }

            @Override
            protected void onPostExecute(Boolean result) {

                if (result) {
                    if (position < 0 || position >= photos.size()) {
                        return;
                    }

                    final ClubPhotoDTO photo = photos.get(position);
                    if (null == photo) {
                        return;
                    }
                    int likeNum = photo.getLikeNum();

                    if (photo.isHasLiked()) {
                        praise.setSelected(false);
                        likeNum = likeNum - 1;
                        photo.setHasLiked(false);
                        photo.setLikeNum(likeNum);

                    } else {
                        praise.setSelected(true);
                        likeNum = likeNum + 1;
                        praiseTv.setText(String.valueOf(likeNum));
                        photo.setHasLiked(true);
                        photo.setLikeNum(likeNum);
                    }

                    if (likeNum <= 0) {
                        praiseTv.setText("");
                    } else {
                        praiseTv.setText(String.valueOf(likeNum));
                    }
                }
            }
        }, position);
    }

    /**
     * 跳转Image comment
     *
     * @param position
     */
    private void start2CommentActivity(int position) {
        if (position < 0 || position >= photos.size()) {
            return;
        }

        final ClubPhotoDTO photo = photos.get(position);
        if (null == photo) {
            return;
        }

        Intent intent = new Intent(this, ClubImageDetailsActivity.class);
        intent.putExtra(ClubImageDetailsActivity.EXTRA_PHOTO, photo);
        startActivity(intent);
    }

    /**
     * 保存图片
     *
     * @param imageUrl
     */
    private void showSaveImageWindow(String imageUrl) {
        if (null == ClubFeedImageDetailsActivity.this || isFinishing()) {
            return;
        }

        if (null == this.imagePopupWindow) {
            this.imagePopupWindow = new SaveImagePopupWindow(this, imageUrl);
        }

        this.imagePopupWindow.setImageUrl(imageUrl);
        this.imagePopupWindow.showAtLocation(findViewById(R.id.activity_clubfeed_image_view),
                Gravity.CENTER, 0, 0);
    }

    public class AdapterImagePager extends PagerAdapter {
        private int imageSize = DensityUtil.getWidth(BeastBikes.getInstance()) * 9 / 15;
        private LayoutInflater inflater;
        public List<String> images = null;

        public AdapterImagePager(Context context, List<String> urls) {
            images = urls;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getItemPosition(Object object) {
            return PagerAdapter.POSITION_NONE;
        }

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public Object instantiateItem(ViewGroup view, int position) {
            View imageLayout = inflater.inflate(R.layout.clubfeed_image_deatils, view, false);
            assert imageLayout != null;
            TouchImageView imageView = (TouchImageView) imageLayout.findViewById(R.id.image);
            final ProgressBar spinner = (ProgressBar) imageLayout.findViewById(R.id.loading);
            if (images.size() <= 0) {
                return null;
            }

            final String url = (images.get(position).contains("http://")||images.get(position).contains("https://")) ?  images.get(position)
                    + (isCompress ? "?imageView2/2/w/"  + imageSize : "") : "file://" + images.get(position);




            if (!TextUtils.isEmpty(url)) {
                spinner.setVisibility(View.VISIBLE);
                Picasso.with(getApplicationContext()).load(url).error(R.drawable.multi_image_selector_default_error)
                        .placeholder(R.drawable.multi_image_selector_default_error).into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        spinner.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        spinner.setVisibility(View.GONE);
                    }
                });
            } else {
                imageView.setImageResource(R.drawable.multi_image_selector_default_error);
            }

            imageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    showSaveImageWindow(url);
                    return false;
                }
            });

            view.addView(imageLayout, 0);
            return imageLayout;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

    }
}
