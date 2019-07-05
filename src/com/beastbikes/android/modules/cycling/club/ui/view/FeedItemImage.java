package com.beastbikes.android.modules.cycling.club.ui.view;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.beastbikes.android.BeastBikes;
import com.beastbikes.android.R;
import com.beastbikes.android.modules.cycling.club.dto.ImageInfo;
import com.beastbikes.android.modules.cycling.club.ui.ClubFeedImageDetailsActivity;
import com.beastbikes.framework.ui.android.lib.pulltorefresh.DensityUtil;
import com.beastbikes.framework.ui.android.lib.view.AutoWrapViewGroup;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Stack;

/**
 * Created by chenqingfei on 15/12/3.
 */
public class FeedItemImage extends LinearLayout implements View.OnClickListener {
    private static final int DEFAULT_ROW_CONTENT_COUNT = 3;

    public static final int IMG_SIZE0 = DensityUtil.getWidth(BeastBikes.getInstance()) * 9 / 22;
    public static final int IMG_SIZE1 = DensityUtil.getWidth(BeastBikes.getInstance()) * 9 / 38;
    public static final int IMG_SIZE2 = DensityUtil.getWidth(BeastBikes.getInstance()) * 9 / 31;

    private static final Stack<WeakReference<ImageView>> recycled = new Stack<WeakReference<ImageView>>();
    private ArrayList<String> pathQueue = new ArrayList<String>();
    private Context context;
    private int rowContentCount;
    private int rowCount;
    private int imgSize;
    private int padding;
    private int horInterval, verInterval;

    private final AutoWrapViewGroup container;

    public FeedItemImage(Context context) {
        super(context);
        this.context = context;
        this.setOrientation(VERTICAL);
        container = new AutoWrapViewGroup(context);
        this.addView(container);

    }

    public void bind(ArrayList<ImageInfo> imageInfos) {

        this.imgSize = IMG_SIZE1;
        this.rowContentCount = DEFAULT_ROW_CONTENT_COUNT;
        this.horInterval = imgSize / 20;
        this.verInterval = imgSize / 20;


        if (imageInfos == null) return;

        this.rowCount = imageInfos.size() / 3;
        if (imageInfos.size() == 4 || imageInfos.size() == 2) {

            this.imgSize = IMG_SIZE2;
            this.rowContentCount = 2;
            this.rowCount = 2;
        } else if (imageInfos.size() == 1) {
            this.imgSize = IMG_SIZE0;
            this.rowContentCount = 1;
            this.rowCount = 1;
        }

        this.config(rowContentCount, rowCount, imgSize, horInterval,
                verInterval);
        container.removeAllViews();
        pathQueue.clear();
        for (int i = 0; i < imageInfos.size(); i++) {
            addImage(imageInfos.get(i).getUrl(), i);
            pathQueue.add(imageInfos.get(i).getUrl());
        }
    }


    public View getView() {

        return this;
    }


    public void onClick(View v) {
        Intent it = new Intent(context, ClubFeedImageDetailsActivity.class);
        it.putStringArrayListExtra(ClubFeedImageDetailsActivity.EXTRA_IMAGES, pathQueue);
        it.putExtra(ClubFeedImageDetailsActivity.EXTRA_POS, v.getId());
        it.putExtra(ClubFeedImageDetailsActivity.EXTRA_CANDEL,false);
        context.startActivity(it);
    }


    /**
     * 设置图片大小和四周pad
     */
    public void config(int rowContentCount, int rowCount, int imgSize,
                       int horInterval, int verInterval) {
        padding = 2;
        this.setPadding(0, padding, padding, padding);
        container.setHorizontalInterval(horInterval);
        container.setVerticalInterval(verInterval);
        container.setChildSize(imgSize);
        container.setChildRowCount(rowContentCount);
    }

    private void addImage(String url, int pos) {
        ImageView image = getReusedImageView();
        image.setTag(url);
        image.setId(pos);
        image.setOnClickListener(this);
        int imageSize = this.imgSize * 4 / 5;
        url = url.startsWith("http://") ? url+"?imageView2/2/w/"+imageSize : "file://" + url;
        if (!TextUtils.isEmpty(url)) {
            Picasso.with(context).load(url).fit().error(R.drawable.bg_1b1b1b).
                    placeholder(R.drawable.bg_1b1b1b).centerCrop().into(image);
        }
        container.addView(image, pos);
    }

    private ImageView getReusedImageView() {

        WeakReference<ImageView> weakIv = null;
        if (recycled != null && !recycled.isEmpty()) {
            weakIv = recycled.pop();
            while (weakIv.get() == null && !recycled.isEmpty()) {
                weakIv = recycled.pop();
            }
            if (weakIv.get() != null) {
                return weakIv.get();
            }
        }

        ImageView iv = new ImageView(context);
        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        iv.setOnClickListener(this);
        return iv;
    }

}
