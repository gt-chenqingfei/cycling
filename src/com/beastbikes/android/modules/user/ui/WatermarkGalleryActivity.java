package com.beastbikes.android.modules.user.ui;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.beastbikes.android.R;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.android.modules.user.dto.ActivityDTO;
import com.beastbikes.android.modules.user.dto.WaterMark;
import com.beastbikes.android.modules.user.dto.WaterMarkImage;
import com.beastbikes.android.modules.user.filter.other.DrawableCenterTextView;
import com.beastbikes.android.modules.user.filter.sticker.DynamicStickerView;
import com.beastbikes.android.modules.user.filter.utils.FilterTools;
import com.beastbikes.android.modules.user.filter.utils.FilterTools.FilterAdjuster;
import com.beastbikes.android.modules.user.filter.utils.FilterTools.FilterType;
import com.beastbikes.android.utils.BitmapUtil;
import com.beastbikes.android.utils.SpeedxAnalytics;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.android.utils.DimensionUtils;
import com.beastbikes.framework.ui.android.utils.Toasts;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import jp.co.cyberagent.android.gpuimage.GPUImage.OnPictureSavedListener;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageView;

@LayoutResource(R.layout.activity_watermark_gallery)
public class WatermarkGalleryActivity extends SessionFragmentActivity implements
        OnSeekBarChangeListener, OnClickListener, OnPictureSavedListener {

    public static final String EXTRA_ACTIVITY_DTO = "dto";

    public static final String EXTRA_PICTURE_PATH = "path";

    private static final int REQ_FINISH_EDIT = 2;

    private final DisplayMetrics displayMetrics = new DisplayMetrics();

    public static final String WATERMARK_URL_ZH = "http://bazaar.speedx.com/watermarks/watermark_zh_android.json?ver=";

    public static final String WATERMARK_URL_EN = "http://bazaar.speedx.com/watermarks/watermark_en_android.json?ver=";

    @IdResource(R.id.activity_watermark_gallery_back)
    private ImageView back;

    @IdResource(R.id.activity_watermark_gallery_reverse)
    private ImageView reverse;

    @IdResource(R.id.activity_watermark_gallery_rotate)
    private ImageView rotate;

    @IdResource(R.id.activity_watermark_gallery_next)
    private TextView next;

    @IdResource(R.id.activity_watermark_gallery_stage)
    private RelativeLayout stage;

    @IdResource(R.id.activity_watermark_gallery_preview)
    private FrameLayout preview;

    @IdResource(R.id.activity_watermark_gallery_gpuimage)
    private GPUImageView gPUImageView;

    @IdResource(R.id.activity_watermark_gallery_save_view)
    private ImageView saveView;

    @IdResource(R.id.activity_watermark_gallery_tab_title1)
    private DrawableCenterTextView tvFilter;

    @IdResource(R.id.activity_watermark_gallery_tab_title2)
    private DrawableCenterTextView tvSticker;

    @IdResource(R.id.activity_watermark_gallery_tab_filter)
    private ViewGroup vgFilter;

    @IdResource(R.id.activity_watermark_gallery_filter_root)
    private LinearLayout rootFilter;

    @IdResource(R.id.activity_watermark_gallery_sticker_root)
    private LinearLayout rootSticker;

    @IdResource(R.id.activity_watermark_gallery_tab_sticker)
    private ViewGroup vgSticker;

    @IdResource(R.id.activity_watermark_gallery_tab_cursor)
    private ImageView cursor;

    @IdResource(R.id.activity_watermark_gallery_filter_filter0)
    private ViewGroup vgFilter0;
    private ImageView filter0Img;
    private TextView filter0Text;

    @IdResource(R.id.activity_watermark_gallery_filter_filter1)
    private ViewGroup vgFilter1;
    private ImageView filter1Img;
    private TextView filter1Text;

    @IdResource(R.id.activity_watermark_gallery_filter_filter2)
    private ViewGroup vgFilter2;
    private ImageView filter2Img;
    private TextView filter2Text;

    @IdResource(R.id.activity_watermark_gallery_filter_filter3)
    private ViewGroup vgFilter3;
    private ImageView filter3Img;
    private TextView filter3Text;

    @IdResource(R.id.activity_watermark_gallery_filter_filter4)
    private ViewGroup vgFilter4;
    private ImageView filter4Img;
    private TextView filter4Text;

    @IdResource(R.id.activity_watermark_gallery_filter_filter5)
    private ViewGroup vgFilter5;
    private ImageView filter5Img;
    private TextView filter5Text;

    @IdResource(R.id.activity_watermark_gallery_filter_filter6)
    private ViewGroup vgFilter6;
    private ImageView filter6Img;
    private TextView filter6Text;

    @IdResource(R.id.activity_watermark_gallery_filter_filter7)
    private ViewGroup vgFilter7;
    private ImageView filter7Img;
    private TextView filter7Text;

    @IdResource(R.id.activity_watermark_gallery_filter_filter8)
    private ViewGroup vgFilter8;
    private ImageView filter8Img;
    private TextView filter8Text;

    @IdResource(R.id.activity_watermark_gallery_filter_filter9)
    private ViewGroup vgFilter9;
    private ImageView filter9Img;
    private TextView filter9Text;

    @IdResource(R.id.activity_watermark_gallery_filter_filter10)
    private ViewGroup vgFilter10;
    private ImageView filter10Img;
    private TextView filter10Text;

    @IdResource(R.id.activity_watermark_gallery_filter_filter11)
    private ViewGroup vgFilter11;
    private ImageView filter11Img;
    private TextView filter11Text;

    @IdResource(R.id.activity_watermark_gallery_filter_filter12)
    private ViewGroup vgFilter12;
    private ImageView filter12Img;
    private TextView filter12Text;

    @IdResource(R.id.activity_watermark_gallery_filter_filter13)
    private ViewGroup vgFilter13;
    private ImageView filter13Img;
    private TextView filter13Text;

    @IdResource(R.id.activity_watermark_gallery_filter_filter14)
    private ViewGroup vgFilter14;
    private ImageView filter14Img;
    private TextView filter14Text;

    @IdResource(R.id.activity_watermark_gallery_filter_filter15)
    private ViewGroup vgFilter15;
    private ImageView filter15Img;
    private TextView filter15Text;

    @IdResource(R.id.activity_watermark_gallery_filter_filter16)
    private ViewGroup vgFilter16;
    private ImageView filter16Img;
    private TextView filter16Text;

    @IdResource(R.id.activity_watermark_gallery_filter_filter17)
    private ViewGroup vgFilter17;
    private ImageView filter17Img;
    private TextView filter17Text;

    @IdResource(R.id.activity_watermark_gallery_filter_filter18)
    private ViewGroup vgFilter18;
    private ImageView filter18Img;
    private TextView filter18Text;

    @IdResource(R.id.activity_watermark_gallery_filter_filter19)
    private ViewGroup vgFilter19;
    private ImageView filter19Img;
    private TextView filter19Text;

    @IdResource(R.id.activity_watermark_gallery_filter_filter20)
    private ViewGroup vgFilter20;
    private ImageView filter20Img;
    private TextView filter20Text;

    private Uri uri;

    private GPUImageFilter imageFilter;
    private FilterAdjuster filterAdjuster;

    private int offset = 0;
    private int bmpW;

    private int rotation = 0;

    private DynamicStickerView currentSticker;
    private DynamicStickerView currentSticker2;

    private List<ViewGroup> filters = new ArrayList<ViewGroup>(21);

    private List<ImageView> stickerbars = new ArrayList<ImageView>(9);

    private ActivityDTO dto;

    private Bitmap thumbnailBmp;

    private RequestQueue mQueue;

    private LayoutParams stickerlp;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.overridePendingTransition(R.anim.activity_in_from_right,
                R.anim.activity_none);

        mQueue = Volley.newRequestQueue(this);

        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        bmpW = BitmapFactory.decodeResource(getResources(),
                R.drawable.offline_map_tab_cursor).getWidth();
        int screenW = displayMetrics.widthPixels;
        offset = (screenW / 2 - bmpW) / 2;
        Matrix matrix = new Matrix();
        matrix.postTranslate(offset, 0);
        cursor.setImageMatrix(matrix);

        final LayoutParams lp = this.gPUImageView.getLayoutParams();
        lp.height = displayMetrics.widthPixels;
        lp.width = displayMetrics.widthPixels;
        this.gPUImageView.setLayoutParams(lp);

        this.back.setOnClickListener(this);
        this.next.setOnClickListener(this);
        this.back.setOnClickListener(this);
        this.reverse.setOnClickListener(this);
        this.rotate.setOnClickListener(this);
        this.tvSticker.setOnClickListener(this);
        this.tvFilter.setOnClickListener(this);
        this.tvFilter.setSelected(true);
        this.reverse.setEnabled(false);

        final Intent i = getIntent();
        if (null != i && i.hasExtra(EXTRA_PICTURE_PATH)) {
            this.uri = i.getParcelableExtra(EXTRA_PICTURE_PATH);
            this.gPUImageView.setImage(this.uri);
        }
        this.initFilterAndSticker();
        SpeedxAnalytics.onEvent(this, "", "click_ridding_history_share_digital_watermarking_chose_filter");
    }

    private void initFilterAndSticker() {
        if (null == this.uri) {
            return;
        }
        final Context ctx = this;
        final Options opts = new Options();
        final ContentResolver cr = ctx.getContentResolver();
        opts.inSampleSize = 6;
        try {
            final InputStream is = cr.openInputStream(this.uri);
            this.thumbnailBmp = BitmapFactory.decodeStream(is, null, opts);
        } catch (FileNotFoundException e) {
        }

        if (null == thumbnailBmp) {
            this.finish();
        }

        this.filter0Img = (ImageView) this.vgFilter0
                .findViewById(R.id.activity_watermark_gallery_lv_item_filter_img);
        this.filter0Text = (TextView) this.vgFilter0
                .findViewById(R.id.activity_watermark_gallery_lv_item_filter_name);
//		this.filter0Img.setImage(thumbnailBmp);
        this.filter0Text.setText(R.string.source_image);
        this.vgFilter0.setOnClickListener(this);
        this.vgFilter0.setTag(FilterType.NO_FILTER);
        this.filter0Img.setImageBitmap(thumbnailBmp);

        this.filter1Img = (ImageView) this.vgFilter1
                .findViewById(R.id.activity_watermark_gallery_lv_item_filter_img);
        this.filter1Text = (TextView) this.vgFilter1
                .findViewById(R.id.activity_watermark_gallery_lv_item_filter_name);
        this.filter1Text.setText("AMARO");
        this.vgFilter1.setOnClickListener(this);
        this.vgFilter1.setTag(FilterType.I_AMARO);

        this.filter2Img = (ImageView) this.vgFilter2
                .findViewById(R.id.activity_watermark_gallery_lv_item_filter_img);
        this.filter2Text = (TextView) this.vgFilter2
                .findViewById(R.id.activity_watermark_gallery_lv_item_filter_name);
        this.filter2Text.setText("HUDSON");
        this.vgFilter2.setOnClickListener(this);
        this.vgFilter2.setTag(FilterType.I_HUDSON);

        this.filter3Img = (ImageView) this.vgFilter3
                .findViewById(R.id.activity_watermark_gallery_lv_item_filter_img);
        this.filter3Text = (TextView) this.vgFilter3
                .findViewById(R.id.activity_watermark_gallery_lv_item_filter_name);
        this.filter3Text.setText("VALENCIA");
        this.vgFilter3.setOnClickListener(this);
        this.vgFilter3.setTag(FilterType.I_VALENCIA);

        this.filter4Img = (ImageView) this.vgFilter4
                .findViewById(R.id.activity_watermark_gallery_lv_item_filter_img);
        this.filter4Text = (TextView) this.vgFilter4
                .findViewById(R.id.activity_watermark_gallery_lv_item_filter_name);
        this.filter4Text.setText("XOROII");
        this.vgFilter4.setOnClickListener(this);
        this.vgFilter4.setTag(FilterType.I_XPROII);

        this.filter5Img = (ImageView) this.vgFilter5
                .findViewById(R.id.activity_watermark_gallery_lv_item_filter_img);
        this.filter5Text = (TextView) this.vgFilter5
                .findViewById(R.id.activity_watermark_gallery_lv_item_filter_name);
        this.filter5Text.setText("EARLYBIRD");
        this.vgFilter5.setOnClickListener(this);
        this.vgFilter5.setTag(FilterType.I_EARLYBIRD);

        this.filter6Img = (ImageView) this.vgFilter6
                .findViewById(R.id.activity_watermark_gallery_lv_item_filter_img);
        this.filter6Text = (TextView) this.vgFilter6
                .findViewById(R.id.activity_watermark_gallery_lv_item_filter_name);
        this.filter6Text.setText("LOMO");
        this.vgFilter6.setOnClickListener(this);
        this.vgFilter6.setTag(FilterType.I_LOMO);

        this.filter7Img = (ImageView) this.vgFilter7
                .findViewById(R.id.activity_watermark_gallery_lv_item_filter_img);
        this.filter7Text = (TextView) this.vgFilter7
                .findViewById(R.id.activity_watermark_gallery_lv_item_filter_name);
        this.filter7Text.setText("BRANNAN");
        this.vgFilter7.setOnClickListener(this);
        this.vgFilter7.setTag(FilterType.I_BRANNAN);

        this.filter8Img = (ImageView) this.vgFilter8
                .findViewById(R.id.activity_watermark_gallery_lv_item_filter_img);
        this.filter8Text = (TextView) this.vgFilter8
                .findViewById(R.id.activity_watermark_gallery_lv_item_filter_name);
        this.filter8Text.setText("INKWELL");
        this.vgFilter8.setOnClickListener(this);
        this.vgFilter8.setTag(FilterType.I_INKWELL);

        this.filter9Img = (ImageView) this.vgFilter9
                .findViewById(R.id.activity_watermark_gallery_lv_item_filter_img);
        this.filter9Text = (TextView) this.vgFilter9
                .findViewById(R.id.activity_watermark_gallery_lv_item_filter_name);
        this.filter9Text.setText("HEFE");
        this.vgFilter9.setOnClickListener(this);
        this.vgFilter9.setTag(FilterType.I_HEFE);

        this.filter10Img = (ImageView) this.vgFilter10
                .findViewById(R.id.activity_watermark_gallery_lv_item_filter_img);
        this.filter10Text = (TextView) this.vgFilter10
                .findViewById(R.id.activity_watermark_gallery_lv_item_filter_name);
        this.filter10Text.setText("NASHVILLE");
        this.vgFilter10.setOnClickListener(this);
        this.vgFilter10.setTag(FilterType.I_NASHVILLE);

        this.filter11Img = (ImageView) this.vgFilter11
                .findViewById(R.id.activity_watermark_gallery_lv_item_filter_img);
        this.filter11Text = (TextView) this.vgFilter11
                .findViewById(R.id.activity_watermark_gallery_lv_item_filter_name);
        this.filter11Text.setText("TONE_CURVE");
        this.vgFilter11.setOnClickListener(this);
        this.vgFilter11.setTag(FilterType.TONE_CURVE);

        this.filter12Img = (ImageView) this.vgFilter12
                .findViewById(R.id.activity_watermark_gallery_lv_item_filter_img);
        this.filter12Text = (TextView) this.vgFilter12
                .findViewById(R.id.activity_watermark_gallery_lv_item_filter_name);
        this.filter12Text.setText("VIGNETTE");
        this.vgFilter12.setOnClickListener(this);
        this.vgFilter12.setTag(FilterType.VIGNETTE);

        this.filter13Img = (ImageView) this.vgFilter13
                .findViewById(R.id.activity_watermark_gallery_lv_item_filter_img);
        this.filter13Text = (TextView) this.vgFilter13
                .findViewById(R.id.activity_watermark_gallery_lv_item_filter_name);
        this.filter13Text.setText("SUTRO");
        this.vgFilter13.setOnClickListener(this);
        this.vgFilter13.setTag(FilterType.I_SUTRO);

        this.filter14Img = (ImageView) this.vgFilter14
                .findViewById(R.id.activity_watermark_gallery_lv_item_filter_img);
        this.filter14Text = (TextView) this.vgFilter14
                .findViewById(R.id.activity_watermark_gallery_lv_item_filter_name);
        this.filter14Text.setText("TOASTER");
        this.vgFilter14.setOnClickListener(this);
        this.vgFilter14.setTag(FilterType.I_TOASTER);

        this.filter15Img = (ImageView) this.vgFilter15
                .findViewById(R.id.activity_watermark_gallery_lv_item_filter_img);
        this.filter15Text = (TextView) this.vgFilter15
                .findViewById(R.id.activity_watermark_gallery_lv_item_filter_name);
        this.filter15Text.setText("WALDEN");
        this.vgFilter15.setOnClickListener(this);
        this.vgFilter15.setTag(FilterType.I_WALDEN);

        this.filter16Img = (ImageView) this.vgFilter16
                .findViewById(R.id.activity_watermark_gallery_lv_item_filter_img);
        this.filter16Text = (TextView) this.vgFilter16
                .findViewById(R.id.activity_watermark_gallery_lv_item_filter_name);
        this.filter16Text.setText("1977");
        this.vgFilter16.setOnClickListener(this);
        this.vgFilter16.setTag(FilterType.I_1977);

        this.filter17Img = (ImageView) this.vgFilter17
                .findViewById(R.id.activity_watermark_gallery_lv_item_filter_img);
        this.filter17Text = (TextView) this.vgFilter17
                .findViewById(R.id.activity_watermark_gallery_lv_item_filter_name);
        this.filter17Text.setText("LORDKELVIN");
        this.vgFilter17.setOnClickListener(this);
        this.vgFilter17.setTag(FilterType.I_LORDKELVIN);

        this.filter18Img = (ImageView) this.vgFilter18
                .findViewById(R.id.activity_watermark_gallery_lv_item_filter_img);
        this.filter18Text = (TextView) this.vgFilter18
                .findViewById(R.id.activity_watermark_gallery_lv_item_filter_name);
        this.filter18Text.setText("CONTRAST");
        this.vgFilter18.setOnClickListener(this);
        this.vgFilter18.setTag(FilterType.CONTRAST);

        this.filter19Img = (ImageView) this.vgFilter19
                .findViewById(R.id.activity_watermark_gallery_lv_item_filter_img);
        this.filter19Text = (TextView) this.vgFilter19
                .findViewById(R.id.activity_watermark_gallery_lv_item_filter_name);
        this.filter19Text.setText("SEPIA");
        this.vgFilter19.setOnClickListener(this);
        this.vgFilter19.setTag(FilterType.SEPIA);

        this.filter20Img = (ImageView) this.vgFilter20
                .findViewById(R.id.activity_watermark_gallery_lv_item_filter_img);
        this.filter20Text = (TextView) this.vgFilter20
                .findViewById(R.id.activity_watermark_gallery_lv_item_filter_name);
        this.filter20Text.setText("Lookup Amatorka");
        this.vgFilter20.setOnClickListener(this);
        this.vgFilter20.setTag(FilterType.LOOKUP_AMATORKA);

        this.filter0Img.setImageBitmap(thumbnailBmp);
        this.filter1Img.setImageBitmap(thumbnailBmp);
        this.filter2Img.setImageBitmap(thumbnailBmp);
        this.filter3Img.setImageBitmap(thumbnailBmp);
        this.filter4Img.setImageBitmap(thumbnailBmp);
        this.filter5Img.setImageBitmap(thumbnailBmp);
        this.filter6Img.setImageBitmap(thumbnailBmp);
        this.filter7Img.setImageBitmap(thumbnailBmp);
        this.filter8Img.setImageBitmap(thumbnailBmp);
        this.filter9Img.setImageBitmap(thumbnailBmp);
        this.filter10Img.setImageBitmap(thumbnailBmp);
        this.filter11Img.setImageBitmap(thumbnailBmp);
        this.filter12Img.setImageBitmap(thumbnailBmp);
        this.filter13Img.setImageBitmap(thumbnailBmp);
        this.filter14Img.setImageBitmap(thumbnailBmp);
        this.filter15Img.setImageBitmap(thumbnailBmp);
        this.filter16Img.setImageBitmap(thumbnailBmp);
        this.filter17Img.setImageBitmap(thumbnailBmp);
        this.filter18Img.setImageBitmap(thumbnailBmp);
        this.filter19Img.setImageBitmap(thumbnailBmp);
        this.filter20Img.setImageBitmap(thumbnailBmp);

        this.filters.add(vgFilter0);
        this.filters.add(vgFilter1);
        this.filters.add(vgFilter2);
        this.filters.add(vgFilter3);
        this.filters.add(vgFilter4);
        this.filters.add(vgFilter5);
        this.filters.add(vgFilter6);
        this.filters.add(vgFilter7);
        this.filters.add(vgFilter8);
        this.filters.add(vgFilter9);
        this.filters.add(vgFilter10);
        this.filters.add(vgFilter11);
        this.filters.add(vgFilter12);
        this.filters.add(vgFilter13);
        this.filters.add(vgFilter14);
        this.filters.add(vgFilter15);
        this.filters.add(vgFilter16);
        this.filters.add(vgFilter17);
        this.filters.add(vgFilter18);
        this.filters.add(vgFilter19);
        this.filters.add(vgFilter20);

        this.setFilterSelected(0);

        final Intent intent = getIntent();
        if (null != intent && intent.hasExtra(EXTRA_ACTIVITY_DTO)) {
            this.dto = (ActivityDTO) intent.getSerializableExtra(EXTRA_ACTIVITY_DTO);
        }

        if (null == this.dto) {
            this.finish();
        }

        stickerlp = new LayoutParams(DimensionUtils.dip2px(this, 90), DimensionUtils.dip2px(this, 90));

        getDynamicSticker();

        final GPUImageView gpu1 = new GPUImageView(ctx);
        gpu1.setImage(thumbnailBmp);
        this.rootFilter.addView(gpu1, new LayoutParams(1, 1));
        final int FILTER_COUNT = 5;
        final List<GPUImageFilter> filters1 = new ArrayList<GPUImageFilter>(
                FILTER_COUNT);
        final List<FilterType> types1 = FilterTools.getFilterTypeList1();
        for (final FilterType type : types1) {
            filters1.add(FilterTools.createFilterForType(ctx, type));
        }
        this.getAsyncTaskQueue().add(new AsyncTask<Void, Void, List<Bitmap>>() {

            @Override
            protected List<Bitmap> doInBackground(Void... params) {
                List<Bitmap> bmps = new ArrayList<Bitmap>(FILTER_COUNT);
                for (GPUImageFilter filter : filters1) {
                    gpu1.setFilter(filter);
                    try {
                        Thread.sleep(400);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    bmps.add(gpu1.getBitmapWithFilterApplied());
                }
                return bmps;
            }

            @Override
            protected void onPostExecute(List<Bitmap> result) {
                if (null == result || result.isEmpty()) {
                    return;
                }
                final ArrayList<Bitmap> bmps = (ArrayList<Bitmap>) result;
                filter1Img.setImageBitmap(bmps.get(0));
                filter2Img.setImageBitmap(bmps.get(1));
                filter3Img.setImageBitmap(bmps.get(2));
                filter4Img.setImageBitmap(bmps.get(3));
                filter5Img.setImageBitmap(bmps.get(4));
                gpu1.setVisibility(View.GONE);
            }

        });

        final GPUImageView gpu2 = new GPUImageView(ctx);
        gpu2.setImage(thumbnailBmp);
        this.rootFilter.addView(gpu2, new LayoutParams(1, 1));
        final List<GPUImageFilter> filters2 = new ArrayList<GPUImageFilter>(
                FILTER_COUNT);
        final List<FilterType> types2 = FilterTools.getFilterTypeList2();
        for (final FilterType type : types2) {
            filters2.add(FilterTools.createFilterForType(ctx, type));
        }

        this.getAsyncTaskQueue().add(new AsyncTask<Void, Void, List<Bitmap>>() {

            @Override
            protected List<Bitmap> doInBackground(Void... params) {
                List<Bitmap> bmps = new ArrayList<Bitmap>(FILTER_COUNT);
                for (GPUImageFilter filter : filters2) {
                    gpu2.setFilter(filter);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    bmps.add(gpu2.getBitmapWithFilterApplied());
                }
                return bmps;
            }

            @Override
            protected void onPostExecute(List<Bitmap> result) {

                final ArrayList<Bitmap> bmps = (ArrayList<Bitmap>) result;
                filter6Img.setImageBitmap(bmps.get(0));
                filter7Img.setImageBitmap(bmps.get(1));
                filter8Img.setImageBitmap(bmps.get(2));
                filter9Img.setImageBitmap(bmps.get(3));
                filter10Img.setImageBitmap(bmps.get(4));
                gpu2.setVisibility(View.GONE);
            }

        });

        final GPUImageView gpu3 = new GPUImageView(ctx);
        gpu3.setImage(thumbnailBmp);
        this.rootFilter.addView(gpu3, new LayoutParams(1, 1));
        final List<GPUImageFilter> filters3 = new ArrayList<GPUImageFilter>(
                FILTER_COUNT);
        final List<FilterType> types3 = FilterTools.getFilterTypeList3();
        for (final FilterType type : types3) {
            filters3.add(FilterTools.createFilterForType(ctx, type));
        }

        this.getAsyncTaskQueue().add(new AsyncTask<Void, Void, List<Bitmap>>() {

            @Override
            protected List<Bitmap> doInBackground(Void... params) {
                List<Bitmap> bmps = new ArrayList<Bitmap>(FILTER_COUNT);
                for (GPUImageFilter filter : filters3) {
                    gpu3.setFilter(filter);
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    bmps.add(gpu3.getBitmapWithFilterApplied());
                }
                return bmps;
            }

            @Override
            protected void onPostExecute(List<Bitmap> result) {
                if (null == result || result.isEmpty()) {
                    return;
                }
                final ArrayList<Bitmap> bmps = (ArrayList<Bitmap>) result;
                filter11Img.setImageBitmap(bmps.get(0));
                filter12Img.setImageBitmap(bmps.get(1));
                filter13Img.setImageBitmap(bmps.get(2));
                filter14Img.setImageBitmap(bmps.get(3));
                filter15Img.setImageBitmap(bmps.get(4));
                gpu3.setVisibility(View.GONE);
            }
        });

        final GPUImageView gpu4 = new GPUImageView(ctx);
        gpu4.setImage(thumbnailBmp);
        this.rootFilter.addView(gpu4, new LayoutParams(1, 1));
        final List<GPUImageFilter> filters4 = new ArrayList<GPUImageFilter>(
                FILTER_COUNT);
        final List<FilterType> types4 = FilterTools.getFilterTypeList4();
        for (final FilterType type : types4) {
            filters4.add(FilterTools.createFilterForType(ctx, type));
        }

        this.getAsyncTaskQueue().add(new AsyncTask<Void, Void, List<Bitmap>>() {

            @Override
            protected List<Bitmap> doInBackground(Void... params) {
                List<Bitmap> bmps = new ArrayList<Bitmap>(FILTER_COUNT);
                for (GPUImageFilter filter : filters4) {
                    gpu4.setFilter(filter);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    bmps.add(gpu4.getBitmapWithFilterApplied());
                }
                return bmps;
            }

            @Override
            protected void onPostExecute(List<Bitmap> result) {
                if (null == result || result.isEmpty()) {
                    return;
                }
                final ArrayList<Bitmap> bmps = (ArrayList<Bitmap>) result;
                filter16Img.setImageBitmap(bmps.get(0));
                filter17Img.setImageBitmap(bmps.get(1));
                filter18Img.setImageBitmap(bmps.get(2));
                filter19Img.setImageBitmap(bmps.get(3));
                filter20Img.setImageBitmap(bmps.get(4));
                gpu4.setVisibility(View.GONE);
            }
        });

    }

    @Override
    public void onClick(final View v) {
        int one = offset * 2 + bmpW;
        switch (v.getId()) {
            case R.id.activity_watermark_gallery_tab_title1:
                SpeedxAnalytics.onEvent(this, "", "click_ridding_history_share_digital_watermarking_chose_filter");
                Animation animation = new TranslateAnimation(one * 0, one * 0, 0, 0);
                animation.setFillAfter(true);
                animation.setDuration(300);
                this.cursor.startAnimation(animation);
                this.tvFilter.setSelected(true);
                this.tvSticker.setSelected(false);
                this.reverse.setEnabled(false);
                switchToFilter();
                break;
            case R.id.activity_watermark_gallery_tab_title2:
                SpeedxAnalytics.onEvent(this, "", "click_ridding_history_share_digital_watermarking_chose_sticker");
                Animation animation2 = new TranslateAnimation(one * 1, one * 1, 0,
                        0);
                animation2.setFillAfter(true);
                animation2.setDuration(300);
                this.cursor.startAnimation(animation2);
                this.tvFilter.setSelected(false);
                this.tvSticker.setSelected(true);
                this.reverse.setEnabled(true);
                this.switchToSticker();
                break;
            case R.id.activity_watermark_gallery_back:
                this.setResult(RESULT_OK);
                this.finish();
                break;
            case R.id.activity_watermark_gallery_next:
                SpeedxAnalytics.onEvent(this, "", "click_ridding_history_share_digital_watermarking_next");
                this.saveEditedImage();
                break;
            case R.id.activity_watermark_gallery_reverse:
                if (currentSticker != null) {
                    currentSticker.setReverseMode(!currentSticker.isReverseMode());
                    currentSticker.performClick();
                }
                break;
            case R.id.activity_watermark_gallery_rotate: {
                this.rotation = (this.rotation + 90) % 360;
                if (currentSticker != null)
                    this.currentSticker.setRotation(this.rotation);
                if (currentSticker2 != null)
                    this.currentSticker2.setRotation(this.rotation);
                this.gPUImageView.rotate(this.rotation);
                break;
            }
            case R.id.activity_watermark_gallery_filter_filter0: {
                this.setFilterSelected(0);
                break;
            }
            case R.id.activity_watermark_gallery_filter_filter1: {
                this.setFilterSelected(1);
                break;
            }
            case R.id.activity_watermark_gallery_filter_filter2: {
                this.setFilterSelected(2);
                break;
            }
            case R.id.activity_watermark_gallery_filter_filter3: {
                this.setFilterSelected(3);
                break;
            }
            case R.id.activity_watermark_gallery_filter_filter4: {
                this.setFilterSelected(4);
                break;
            }
            case R.id.activity_watermark_gallery_filter_filter5: {
                this.setFilterSelected(5);
                break;
            }
            case R.id.activity_watermark_gallery_filter_filter6: {
                this.setFilterSelected(6);
                break;
            }
            case R.id.activity_watermark_gallery_filter_filter7: {
                this.setFilterSelected(7);
                break;
            }
            case R.id.activity_watermark_gallery_filter_filter8: {
                this.setFilterSelected(8);
                break;
            }
            case R.id.activity_watermark_gallery_filter_filter9: {
                this.setFilterSelected(9);
                break;
            }
            case R.id.activity_watermark_gallery_filter_filter10: {
                this.setFilterSelected(10);
                break;
            }
            case R.id.activity_watermark_gallery_filter_filter11: {
                this.setFilterSelected(11);
                break;
            }
            case R.id.activity_watermark_gallery_filter_filter12: {
                this.setFilterSelected(12);
                break;
            }
            case R.id.activity_watermark_gallery_filter_filter13: {
                this.setFilterSelected(13);
                break;
            }
            case R.id.activity_watermark_gallery_filter_filter14: {
                this.setFilterSelected(14);
                break;
            }
            case R.id.activity_watermark_gallery_filter_filter15: {
                this.setFilterSelected(15);
                break;
            }
            case R.id.activity_watermark_gallery_filter_filter16: {
                this.setFilterSelected(16);
                break;
            }
            case R.id.activity_watermark_gallery_filter_filter17: {
                this.setFilterSelected(17);
                break;
            }
            case R.id.activity_watermark_gallery_filter_filter18: {
                this.setFilterSelected(18);
                break;
            }
            case R.id.activity_watermark_gallery_filter_filter19: {
                this.setFilterSelected(19);
                break;
            }
            case R.id.activity_watermark_gallery_filter_filter20: {
                this.setFilterSelected(20);
                break;
            }
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(final int requestCode,
                                    final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_FINISH_EDIT:
                if (RESULT_OK == resultCode) {
                    this.setResult(RESULT_OK, data);
                    this.finish();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        this.saveView.setVisibility(View.INVISIBLE);
        super.onResume();
    }

    private void getDynamicSticker() {
        final List<WaterMark> waterMarks = new ArrayList<>();

        //获取系统当前使用的语言
        String mCurrentLanguage = Locale.getDefault().getLanguage();
        String URL = "";
        if (mCurrentLanguage.equals("zh")) {
            URL = WATERMARK_URL_ZH + System.currentTimeMillis();
        } else {
            URL = WATERMARK_URL_EN + System.currentTimeMillis();
        }
        JsonArrayRequest req = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response == null || response.length() == 0) {
                    return;
                }
                for (int i = 0; i < response.length(); i++) {
                    WaterMark waterMark = new WaterMark(response.optJSONObject(i));
                    waterMarks.add(waterMark);
                }
                addDynamicStickerView(waterMarks);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        req.setShouldCache(false);
        mQueue.add(req);
    }

    public void addDynamicStickerView(final List<WaterMark> waterMarks) {
        if (waterMarks == null || waterMarks.size() == 0)
            return;
        boolean isChineseVersion;
        //获取系统当前使用的语言
        String mCurrentLanguage = Locale.getDefault().getLanguage();
        if (mCurrentLanguage.equals("zh")) {
            isChineseVersion = true;
        } else {
            isChineseVersion = false;
        }
        final boolean isChineseVersion2 = isChineseVersion;
        final LinearLayout.LayoutParams stickerlp = new LinearLayout.LayoutParams(DimensionUtils.dip2px(this, 90), DimensionUtils.dip2px(this, 90));
        stickerlp.setMargins(DimensionUtils.dip2px(this, 4), DimensionUtils.dip2px(this, 4), DimensionUtils.dip2px(this, 4), DimensionUtils.dip2px(this, 4));

        for (int i = 0; i < waterMarks.size(); i++) {
            LinearLayout stickerView = (LinearLayout) LayoutInflater.from(WatermarkGalleryActivity.this).inflate(R.layout.activity_watermark_gallery_item_sticker, null);
            rootSticker.addView(stickerView);
            LinearLayout sticker = (LinearLayout) stickerView.findViewById(R.id.activity_watermark_gallery_lv_item_sticker_sticker);
            ImageView stickerBar = (ImageView) stickerView.findViewById(R.id.activity_watermark_gallery_lv_item_sticker_bar);
            //需要加载的图片列表
            final List<WaterMarkImage> waterMarkImages = waterMarks.get(i).getWaterMarkImages();
            final DynamicStickerView dynamicStickerView = new DynamicStickerView(this);
            dynamicStickerView.setIsChineseVersion(isChineseVersion);
            dynamicStickerView.setActivityDto(dto);
            dynamicStickerView.setLayoutParams(stickerlp);
            dynamicStickerView.setCover(thumbnailBmp);
            dynamicStickerView.setWaterMark(waterMarks.get(i));
            sticker.addView(dynamicStickerView);

            this.stickerbars.add(stickerBar);
            if (waterMarkImages != null && waterMarkImages.size() > 0) {
                for (int j = 0; j < waterMarkImages.size(); j++) {
                    final WaterMarkImage waterMarkImage1 = new WaterMarkImage(waterMarkImages.get(j));
                    loadBitmap(waterMarkImage1, dynamicStickerView);
                }
            }
            final int index = i;
            dynamicStickerView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    stage.removeAllViews();
                    setStickerSelected(index);
                    final LayoutParams lp = new LayoutParams(
                            LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                    currentSticker = dynamicStickerView;
                    final DynamicStickerView dynamicStickerView2 = new DynamicStickerView(WatermarkGalleryActivity.this);
                    dynamicStickerView2.setIsChineseVersion(isChineseVersion2);
                    dynamicStickerView2.setActivityDto(dto);
                    currentSticker2 = dynamicStickerView2;
                    dynamicStickerView2.setLayoutParams(stickerlp);
                    dynamicStickerView2.setReverseMode(dynamicStickerView.isReverseMode());
                    dynamicStickerView2.setWaterMark(waterMarks.get(index));
                    if (waterMarkImages != null && waterMarkImages.size() > 0) {
                        for (int j = 0; j < waterMarkImages.size(); j++) {
                            final WaterMarkImage waterMarkImage1 = new WaterMarkImage(waterMarkImages.get(j));
                            loadBitmap(waterMarkImage1, dynamicStickerView2);
                        }
                    }
                    dynamicStickerView2.setCoverNull();
                    stage.addView(dynamicStickerView2, lp);
                }
            });
        }
    }

    private void setFilterSelected(int n) {
        for (View view : this.filters) {
            view.setSelected(false);
        }
        final View v = this.filters.get(n);
        v.setSelected(true);
        switchFilterTo(FilterTools.createFilterForType(this, (FilterType) v.getTag()));
        this.gPUImageView.requestRender();
    }

    private void setStickerSelected(int n) {
        for (View view : this.stickerbars) {
            view.setSelected(false);
        }
        final View v = this.stickerbars.get(n);
        v.setSelected(true);
    }

    private void saveEditedImage() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage(getString(R.string.saving));
        pd.show();
        this.saveView.setImageBitmap(this.gPUImageView
                .getBitmapWithFilterApplied());
        this.saveView.setVisibility(View.VISIBLE);
        final Bitmap bitmap = BitmapUtil.getViewBitmap(this.preview);
        String filePath = BitmapUtil.saveImage(bitmap);
        pd.dismiss();
        Toasts.show(this, R.string.activity_finished_share_sdcard_success);
        final Intent intent = new Intent(this, WatermarkFinishedActivity.class);
        intent.putExtra(WatermarkFinishedActivity.EXTRA_PICTURE_PATH, filePath);
        startActivityForResult(intent, REQ_FINISH_EDIT);
    }


    @Override
    public void onPictureSaved(final Uri uri) {
        Toast.makeText(this, "Saved: " + uri.toString(), Toast.LENGTH_SHORT)
                .show();
    }

    private void switchToSticker() {
        this.vgFilter.setVisibility(View.GONE);

        final Animation translate = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f);
        final AlphaAnimation alpha = new AlphaAnimation(0f, 1f);
        final AnimationSet as = new AnimationSet(this, null);
        as.addAnimation(translate);
        as.addAnimation(alpha);
        as.setDuration(300L);
        as.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                vgSticker.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                vgFilter.setVisibility(View.GONE);
            }

        });
        this.vgSticker.startAnimation(as);
    }

    private void switchToFilter() {
        this.vgSticker.setVisibility(View.GONE);

        final Animation translate = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f);
        final AlphaAnimation alpha = new AlphaAnimation(0f, 1f);
        final AnimationSet as = new AnimationSet(this, null);
        as.addAnimation(translate);
        as.addAnimation(alpha);
        as.setDuration(300L);
        as.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                vgFilter.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                vgSticker.setVisibility(View.GONE);
            }

        });
        this.vgFilter.startAnimation(as);
    }

    private void switchFilterTo(final GPUImageFilter filter) {
        if (this.imageFilter == null
                || (filter != null && !imageFilter.getClass().equals(
                filter.getClass()))) {
            this.imageFilter = filter;
            this.gPUImageView.setFilter(imageFilter);
            filterAdjuster = new FilterAdjuster(imageFilter);
        }
    }

    @Override
    public void onProgressChanged(final SeekBar seekBar, final int progress,
                                  final boolean fromUser) {
        if (filterAdjuster != null) {
            filterAdjuster.adjust(progress);
        }
        gPUImageView.requestRender();
    }

    @Override
    public void onStartTrackingTouch(final SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(final SeekBar seekBar) {
    }

    public void loadBitmap(final WaterMarkImage waterMarkImage, final DynamicStickerView dynamicStickerView) {
        loadBlackBitmap(waterMarkImage, dynamicStickerView);
        loadWhiteBitmap(waterMarkImage, dynamicStickerView);
    }

    private void loadBlackBitmap(final WaterMarkImage waterMarkImage, final DynamicStickerView dynamicStickerView) {
        Picasso.with(this).load(waterMarkImage.getBlackURL()).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                waterMarkImage.setBlackBitmap(bitmap);
                handleLoadedBitmap(waterMarkImage, dynamicStickerView);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        });
    }

    private void loadWhiteBitmap(final WaterMarkImage waterMarkImage, final DynamicStickerView dynamicStickerView) {
        Picasso.with(this).load(waterMarkImage.getWhiteURL()).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                waterMarkImage.setWhiteBitmap(bitmap);
                handleLoadedBitmap(waterMarkImage, dynamicStickerView);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        });
    }

    public void handleLoadedBitmap(final WaterMarkImage waterMarkImage, final DynamicStickerView dynamicStickerView) {
        if (waterMarkImage == null)
            return;
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dynamicStickerView.addImages(waterMarkImage);
            }
        });
    }

}
