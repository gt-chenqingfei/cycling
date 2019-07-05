package com.beastbikes.android.modules.cycling.club.ui;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beastbikes.android.R;
import com.beastbikes.android.dialog.LoadingDialog;
import com.beastbikes.android.dialog.Wheelview;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.android.modules.cycling.club.biz.ClubFeedManager;
import com.beastbikes.android.modules.cycling.club.biz.ClubFeedService;
import com.beastbikes.android.modules.cycling.club.biz.ClubManager;
import com.beastbikes.android.modules.cycling.club.dto.ClubInfoCompact;
import com.beastbikes.android.modules.cycling.club.dto.ClubPhotoDTO;
import com.beastbikes.android.modules.cycling.club.ui.widget.ClubCalendarPopupWindow;
import com.beastbikes.android.modules.cycling.club.ui.widget.ShowHideOnScroll;
import com.beastbikes.android.utils.DateFormatUtil;
import com.beastbikes.android.widget.MaterialDialog;
import com.beastbikes.android.widget.SwipeRefreshAndLoadLayout;
import com.beastbikes.android.widget.smoothprogressbar.SmoothProgressBar;
import com.beastbikes.android.widget.smoothprogressbar.SmoothProgressBarUtils;
import com.beastbikes.android.widget.smoothprogressbar.SmoothProgressDrawable;
import com.beastbikes.android.widget.stickylistlibrary.stickygridheaders.StickyGridHeadersSimpleAdapter;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.android.utils.DimensionUtils;
import com.beastbikes.framework.business.BusinessException;
import com.beastbikes.framework.ui.android.utils.Toasts;
import com.beastbikes.framework.ui.android.utils.ViewHolder;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by icedan on 15/12/9.
 */
@LayoutResource(R.layout.club_gallery_activity)
public class ClubGalleryActivity extends SessionFragmentActivity implements
        View.OnClickListener, Wheelview.SelectFinishCalendarListener, SwipeRefreshLayout.OnRefreshListener,
        AdapterView.OnItemClickListener, SwipeRefreshAndLoadLayout.OnLoadListener,
        ClubFeedService.ProgressCallback, ClubFeedService.ClubFeedPostNotifyListener,
        ClubFeedManager.CachePhotoDataListener {

    public static final String EXTRA_CLUB_ID = "club_id";
    public static final String EXTRA_PHOTO_MAX_COUNT = "photo_max_count";
    public static final String EXTRA_PHOTO_COUNT = "photo_count";
    public static final String EXTRA_CLUB_MANAGER_ID = "club_manager_id";
    public static final String EXTRA_CLUB_STATUS = "club_status";
    public static final int EXTRA_REQUEST_ID = 88;

//    private int level = 0;//128管理员0成员

    @IdResource(R.id.club_gallery_go_back)
    private ImageView goBackIv;

    @IdResource(R.id.club_gallery_title_left_tv)
    private TextView leftBtn;

    @IdResource(R.id.club_gallery_title_desc)
    private TextView imageCount;

    @IdResource(R.id.club_gallery_title_right_tv)
    private TextView rightBtn;

    @IdResource(R.id.club_gallery_floating_action_button)
    private FloatingActionButton actionBtn;

    @IdResource(R.id.club_gallery_no_image)
    private TextView noImageTv;

    @IdResource(R.id.asset_grid)
    private GridView listView;

    @IdResource(R.id.club_gallery_refresh_layout)
    private SwipeRefreshAndLoadLayout refreshLayout;

    @IdResource(R.id.club_gallery_upload_image_view)
    private RelativeLayout uploadView;

    @IdResource(R.id.club_gallery_upload_count_tv)
    private TextView uploadCountTv;

    @IdResource(R.id.club_gallery_progress_bar)
    private SmoothProgressBar progressBar;

    private GalleryAdapter galleryAdapter;
    private List<ClubPhotoDTO> list = new LinkedList<>();
    private String clubId;

    private ClubCalendarPopupWindow calendarPopupWindow;
    private ClubFeedManager clubFeedManager;
    private String clubManagerId;
    private int maxCount;
    private int currCount;
    private boolean frist = true;
    private int windowWidth;
    private boolean isEdit;
    private String startDate;
    private String endDate = "";
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.overridePendingTransition(R.anim.activity_in_from_right, R.anim.activity_none);

        DisplayMetrics dm = new DisplayMetrics();
        WindowManager vm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        vm.getDefaultDisplay().getMetrics(dm);
        this.windowWidth = dm.widthPixels / 2;

        this.calendarPopupWindow = new ClubCalendarPopupWindow(this, this);

        this.goBackIv.setOnClickListener(this);
        this.rightBtn.setOnClickListener(this);
        this.leftBtn.setOnClickListener(this);
        this.actionBtn.setOnClickListener(this);

        Intent intent = getIntent();
        if (null == intent) {
            return;
        }

        this.clubId = intent.getStringExtra(EXTRA_CLUB_ID);
        this.maxCount = intent.getIntExtra(EXTRA_PHOTO_MAX_COUNT, 500);
        this.currCount = intent.getIntExtra(EXTRA_PHOTO_COUNT, 0);
        int status = intent.getIntExtra(EXTRA_CLUB_STATUS, 0);
        this.clubManagerId = intent.getStringExtra(EXTRA_CLUB_MANAGER_ID);
        if (savedInstanceState != null) {
            maxCount = savedInstanceState.getInt(EXTRA_PHOTO_MAX_COUNT, 500);
            currCount = savedInstanceState.getInt(EXTRA_PHOTO_COUNT, 0);
        }

        if (clubManagerId != null) {
            Log.e("clubManagerId", clubManagerId);
        } else {
            Log.e("clubManagerId", "null");
        }
        if (status == 1 || status == 4) {
            this.rightBtn.setVisibility(View.VISIBLE);
        } else {
            this.rightBtn.setVisibility(View.GONE);
        }
        this.imageCount.setText(String.format("(%d/%d)", currCount, maxCount));
        this.clubFeedManager = new ClubFeedManager(this);

        onRefresh();
        this.galleryAdapter = new GalleryAdapter(list);
        this.listView.setNumColumns(2);
        this.progressBar.setSmoothProgressDrawableBackgroundDrawable(
                SmoothProgressBarUtils.generateDrawableWithColors(
                        getResources().getIntArray(R.array.pocket_background_colors),
                        ((SmoothProgressDrawable) this.progressBar.getIndeterminateDrawable()).getStrokeWidth()));
        this.progressBar.progressiveStart();
        this.listView.setAdapter(this.galleryAdapter);

        this.listView.setOnTouchListener(new ShowHideOnScroll(this.actionBtn));
        this.refreshLayout.setChildGridView(this.listView);
        this.refreshLayout.setOnRefreshListener(this);
        this.refreshLayout.setOnLoadListener(this);
        this.listView.setOnItemClickListener(this);
        ClubFeedService.getInstance().setProgressCallback(this);
        ClubFeedService.getInstance().setClubFeedPostNotifyListener(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(EXTRA_PHOTO_MAX_COUNT, maxCount);
        outState.putInt(EXTRA_PHOTO_COUNT, currCount);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void finish() {
        setResult(RESULT_OK);
        ClubFeedService.getInstance().setProgressCallback(null);
        super.finish();
        super.overridePendingTransition(R.anim.activity_none, R.anim.activity_out_to_right);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.club_gallery_go_back:// Back
                finish();
                break;
            case R.id.club_gallery_title_left_tv:// Cancel
                this.isEdit = false;
                this.refreshView();
                break;
            case R.id.club_gallery_title_right_tv:// Delete or edit
                this.isEdit = true;
                if (leftBtn.getVisibility() == View.VISIBLE) {
                    this.showDialog();
                }
                this.refreshView();
                break;
            case R.id.club_gallery_floating_action_button:
                this.uploadPhoto();
                break;
        }
    }

    @Override
    public void onRefresh() {
        refreshLayout.setCanLoad(true);

        this.getGalleryList(this.clubId, "", "", true);
    }

    @Override
    public void onLoad() {
        this.getGalleryList(this.clubId, this.startDate, this.endDate, false);
    }

    @Override
    public void endSelect(String year, String month) {
        if (TextUtils.isEmpty(year) || TextUtils.isEmpty(month)) {
            return;
        }

        this.startDate = DateFormatUtil.getLastDayOfMonth(Integer.parseInt(year),
                Integer.parseInt(month));
        this.list.clear();
        this.getGalleryList(this.clubId, startDate, endDate, true);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position >= this.list.size()) {
            return;
        }

        ClubPhotoDTO photo = this.list.get(position);
        if (null == photo) {
            return;
        }

        if (isEdit) {
            if (!photo.getUserId().equals(getUserId()) && !getUserId().equals(clubManagerId)) {
                Toast.makeText(ClubGalleryActivity.this, "您仅可编辑自己上传的照片", Toast.LENGTH_SHORT).show();
                return;
            }
            photo.setEdit(!photo.isEdit());
            CheckBox box = (CheckBox) view.findViewById(R.id.club_gallery_list_item_check);
            if (box != null) {
                box.setChecked(photo.isEdit());
            }
            //galleryAdapter.notifyDataSetChanged();
            return;
        }
        Intent imageIntent = new Intent(ClubGalleryActivity.this, ClubFeedImageDetailsActivity.class);
        imageIntent.putExtra(ClubFeedImageDetailsActivity.EXTRA_PHOTO_IMAGES, (Serializable) list);
        imageIntent.putExtra(ClubFeedImageDetailsActivity.EXTRA_POS, position);
        imageIntent.putExtra(EXTRA_CLUB_MANAGER_ID, this.clubManagerId);
        if (getUserId().equals(clubManagerId) || photo.getUserId().equals(getUserId())) {
            imageIntent.putExtra(ClubFeedImageDetailsActivity.EXTRA_CANDEL, true);
        } else {
            imageIntent.putExtra(ClubFeedImageDetailsActivity.EXTRA_CANDEL, false);
        }
        this.startActivityForResult(imageIntent, EXTRA_REQUEST_ID);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case RESULT_OK:
                if (requestCode == EXTRA_REQUEST_ID) {
                    onRefresh();
                }
                break;
        }
    }

    private void refreshView() {
        if (isEdit) {
            this.goBackIv.setVisibility(View.GONE);
            this.leftBtn.setVisibility(View.VISIBLE);
            this.rightBtn.setText(R.string.delete);
        } else {
            this.goBackIv.setVisibility(View.VISIBLE);
            this.leftBtn.setVisibility(View.GONE);
            this.rightBtn.setText(R.string.label_edit);
            for (ClubPhotoDTO dto : list) {
                dto.setEdit(false);
            }
        }

        this.galleryAdapter.notifyDataSetChanged();
    }

    /**
     * 选择时间
     */
    private void showCalendarWindow() {
        if (null == this.calendarPopupWindow) {
            this.calendarPopupWindow = new ClubCalendarPopupWindow(this, this);
        }

        this.calendarPopupWindow.showAtLocation(findViewById(R.id.club_gallery_view), Gravity.BOTTOM, 0, 0);
    }

    /**
     * 获取图片
     */
    private void getGalleryList(final String clubId, final String startDate, final String endDate, final boolean refresh) {
        getClubInfo(clubId);
        this.getAsyncTaskQueue().add(new AsyncTask<String, Void, List<ClubPhotoDTO>>() {

            @Override
            protected void onPreExecute() {
                if (null == list || list.size() <= 0 && frist) {
                    loadingDialog = new LoadingDialog(ClubGalleryActivity.this,
                            getString(R.string.loading_msg),
                            true);
                    loadingDialog.show();
                    frist = false;
                }
                if (!refresh) {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.progressiveStart();
                }
            }

            @Override
            protected List<ClubPhotoDTO> doInBackground(String... params) {
                try {
                    String startTime = startDate;
                    if (!refresh && null != list && list.size() > 0) {
                        startTime = list.get(list.size() - 1).getCreateDate();
                    }

                    return clubFeedManager.getClubGalleryList(clubId, startTime, endDate, 30, ClubGalleryActivity.this, refresh);
                } catch (BusinessException e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(List<ClubPhotoDTO> clubPhotoDTOs) {
                if (loadingDialog != null)
                    loadingDialog.dismiss();
                if (!refresh) {
                    progressBar.progressiveStop();
                    progressBar.setVisibility(View.GONE);
                }
                refreshLayout.setLoading(false);
                refreshLayout.setRefreshing(false);
                if (null != loadingDialog) {
                    loadingDialog.dismiss();
                }
                if (null != clubPhotoDTOs && !clubPhotoDTOs.isEmpty()) {
                    if (refresh) {
                        list.clear();
                        list.addAll(clubPhotoDTOs);
                    } else {
                        list.addAll(clubPhotoDTOs);
                    }
                } else {
                    refreshLayout.setCanLoad(false);
                }

                galleryAdapter.notifyDataSetChanged();
                if (null == list || list.isEmpty()) {
                    noImageTv.setVisibility(View.VISIBLE);
                    listView.setVisibility(View.GONE);
                    rightBtn.setEnabled(false);
                } else {
                    noImageTv.setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);
                    rightBtn.setEnabled(true);
                }
            }
        });
    }

    /**
     * 获取俱乐部详情
     *
     * @param clubId
     */
    private void getClubInfo(final String clubId) {
        if (TextUtils.isEmpty(clubId)) {
            return;
        }

        this.getAsyncTaskQueue().add(
                new AsyncTask<String, Void, ClubInfoCompact>() {

                    @Override
                    protected ClubInfoCompact doInBackground(String... params) {
                        try {
                            return new ClubManager(getApplicationContext()).getClubInfo(params[0]);
                        } catch (BusinessException e) {
                            return null;
                        }
                    }

                    @Override
                    protected void onPostExecute(ClubInfoCompact result) {
                        if (null == result)
                            return;
                        currCount = result.getCurPhotoNum();
                        maxCount = result.getMaxPhotoNum();
                        imageCount.setText(String.format("(%d/%d)", result.getCurPhotoNum(), result.getMaxPhotoNum()));
                    }
                }, clubId);
    }

    private void uploadPhoto() {
        if (this.currCount >= this.maxCount) {
            final MaterialDialog uploadDialog = new MaterialDialog(this);
            uploadDialog.setMessage(R.string.club_gallery_image_count_msg);
            uploadDialog.setPositiveButton(R.string.label_i_know, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    uploadDialog.dismiss();
                }
            }).show();
        } else {
            Intent it = new Intent(this, ClubFeedPicUploadActivity.class);
            it.putExtra(ClubFeedPostActivity.EXTRA_CLUB_ID, this.clubId);
            if (maxCount > currCount) {
                it.putExtra(ClubFeedPicUploadActivity.EXTRA_DIFF_COUNT, maxCount - currCount);
//                it.putExtra(ClubFeedPicUploadActivity.EXTRA_DIFF_COUNT, 5);//测试最多上传5张
            }
            this.startActivity(it);
        }
    }

    private void showDialog() {
        if (null == list || list.size() <= 0) {
            return;
        }

        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (ClubPhotoDTO dto : this.list) {
            if (dto.isEdit()) {
                sb.append(dto.getPhotoId()).append(",");
                count++;
            }
        }

        if (count <= 0) {
            Toasts.show(this, getString(R.string.club_gallery_select_delete_image));
            return;
        }

        String content = String.format(getString(R.string.club_gallery_delete_image_count), count);
        final MaterialDialog deleteDialog = new MaterialDialog(this);
        deleteDialog.setMessage(content);
        deleteDialog.setPositiveButton(R.string.activity_alert_dialog_text_ok, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDialog.dismiss();
                deletePhotos();
            }
        }).setNegativeButton(R.string.cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDialog.dismiss();
            }
        }).show();
    }

    /**
     * 删除照片
     */
    private void deletePhotos() {
        if (null == list || list.size() <= 0) {
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (ClubPhotoDTO dto : this.list) {
            if (dto.isEdit()) {
                sb.append(dto.getPhotoId()).append(",");
            }
        }

        if (sb.length() <= 0) {
            Toasts.show(this, getString(R.string.club_gallery_select_delete_image));
            return;
        }

        String photoIds = sb.toString();
        this.getAsyncTaskQueue().add(new AsyncTask<String, Void, List<Integer>>() {

            @Override
            protected void onPreExecute() {
                loadingDialog = new LoadingDialog(ClubGalleryActivity.this,
                        getString(R.string.loading_msg),
                        true);
                loadingDialog.show();
            }

            @Override
            protected List<Integer> doInBackground(String... params) {
                return clubFeedManager.deleteClubPhotos(params[0]);
            }

            @Override
            protected void onPostExecute(List<Integer> integers) {

                if (null != integers) {
                    onRefresh();
                    isEdit = false;
                    refreshView();
                }
            }
        }, photoIds);
    }

    @Override
    public void onUpLoadProgress(final int progress, final int total, final String club_id) {

        if (clubId.equals(club_id)) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    uploadView.setVisibility(View.VISIBLE);
                    String label = getString(R.string.club_gallery_upload_image_label) + progress + "/" + total;
                    uploadCountTv.setText(label);

                    if (progress == total) {
                        uploadView.setVisibility(View.GONE);
                    }
                }
            });

        }
    }

    @Override
    public void onClubFeedNotify(String clubId) {

        if (this.clubId.equals(clubId)) {
            onRefresh();
        }
    }

    @Override
    public void onGetPhotoCacheData(final List<ClubPhotoDTO> data) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (data == null)
                    return;
                list.clear();
                list.addAll(data);
                galleryAdapter.notifyDataSetChanged();
            }
        });
    }


    private class GalleryAdapter extends BaseAdapter implements StickyGridHeadersSimpleAdapter {
        private boolean isManager;

        private GalleryAdapter(List<ClubPhotoDTO> list) {
            if (getUserId().equals(clubManagerId))
                isManager = true;

        }

        @Override
        public int getCount() {
            if (list == null)
                return 0;
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final GalleryViewHolder viewHolder;
            if (null == convertView) {
                convertView = LayoutInflater.from(parent.getContext()).
                        inflate(R.layout.club_gallery_list_item, null);
                viewHolder = new GalleryViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (GalleryViewHolder) convertView.getTag();
            }

            if (position < list.size()) {
                viewHolder.bind(list.get(position));
            }
            return convertView;
        }

        @Override
        public long getHeaderId(int position) {
            return list.get(position).getHeaderId();
        }

        @Override
        public View getHeaderView(int position, View convertView, ViewGroup parent) {
            final GalleryHeaderViewHolder viewHolder;
            if (null == convertView) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.club_gallery_list_header_item, null);
                viewHolder = new GalleryHeaderViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (GalleryHeaderViewHolder) convertView.getTag();
            }


            if (list.size() > position) {
                viewHolder.bind(list.get(position));
            }
            return convertView;
        }
    }

    private class GalleryHeaderViewHolder extends ViewHolder<ClubPhotoDTO> implements View.OnClickListener {

        private LinearLayout dateView;
        private TextView yearTv;
        private TextView monthTv;
        private TextView countTv;
        private View view;

        public GalleryHeaderViewHolder(View view) {
            super(view);
            this.view = view;
            this.dateView = (LinearLayout) view.findViewById(R.id.club_gallery_header_date);
            this.yearTv = (TextView) view.findViewById(R.id.club_gallery_list_heard_item_year);
            this.monthTv = (TextView) view.findViewById(R.id.club_gallery_list_heard_item_month);
            this.countTv = (TextView) view.findViewById(R.id.club_gallery_list_heard_item_count);
        }

        @Override
        public void bind(ClubPhotoDTO dto) {
            if (null == dto) {
                return;
            }

            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    DimensionUtils.dip2px(getContext(), 50));
            this.view.setLayoutParams(lp);
            int year = DateFormatUtil.getYearOfString(dto.getCreateDate());
            int month = DateFormatUtil.getMonthOfString(dto.getCreateDate());

            this.yearTv.setText(String.format(
                    getString(R.string.label_year),
                    year));
            this.monthTv.setText(String.format(
                    getString(R.string.label_month),
                    month));

            this.countTv.setText(String.format(getString(R.string.club_gallery_header_count_label),
                    dto.getHeaderCount()));
            this.dateView.setOnClickListener(this);
            if (null != calendarPopupWindow) {
                calendarPopupWindow.setCurrentYear(year);
                calendarPopupWindow.setCurrentMonth(month);
            }
        }

        @Override
        public void onClick(View v) {
            showCalendarWindow();
        }
    }


    private class GalleryViewHolder extends ViewHolder<ClubPhotoDTO> implements OnCheckedChangeListener,
            View.OnClickListener {

        @IdResource(R.id.club_gallery_list_item_image)
        private ImageView photoIv;

        @IdResource(R.id.club_gallery_list_item_check)
        private CheckBox checkBox;

        private ClubPhotoDTO item;


        public GalleryViewHolder(View view) {
            super(view);
        }

        @Override
        public void bind(ClubPhotoDTO item) {
            if (null == item) {
                return;
            }

            this.item = item;
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) this.photoIv.getLayoutParams();
            lp.width = windowWidth;
            lp.height = windowWidth;
            this.photoIv.setLayoutParams(lp);

            if (!TextUtils.isEmpty(item.getImageUrl())) {

                String url = (item.getImageUrl().startsWith("http://") || item.getImageUrl().startsWith("https://")) ?
                        item.getImageUrl()+"?imageView2/2/w/"+windowWidth : "file://" + item.getImageUrl();

                Picasso.with(getContext()).load(url).placeholder(R.drawable.bg_222222)
                        .error(R.drawable.bg_222222).resize(windowWidth, windowWidth).
                        centerCrop().into(this.photoIv);
            }

            this.checkBox.setFocusable(false);
            this.checkBox.setEnabled(false);
            if (isEdit && (getUserId().equals(item.getUserId()) || getUserId().equals(clubManagerId))) {
                this.checkBox.setVisibility(View.VISIBLE);
                this.checkBox.setOnCheckedChangeListener(this);
                this.checkBox.setChecked(item.isEdit());
            } else {
                this.checkBox.setVisibility(View.GONE);
            }
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (null == this.item) {
                return;
            }

            this.item.setEdit(isChecked);
        }

        @Override
        public void onClick(View v) {
            this.checkBox.setChecked(!this.checkBox.isChecked());
        }
    }


}
