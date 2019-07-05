package com.beastbikes.android.modules.cycling.club.ui.widget;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.beastbikes.android.BeastBikes;
import com.beastbikes.android.R;
import com.beastbikes.android.modules.cycling.club.ui.ClubFeedImageDetailsActivity;
import com.beastbikes.framework.ui.android.lib.pulltorefresh.DensityUtil;
import com.beastbikes.framework.ui.android.lib.view.AutoWrapViewGroup;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Stack;

import com.beastbikes.android.widget.multiimageselector.MultiImageSelectorActivity;


/**
 * 选择照片空间
 */
public class EditPhotoView extends LinearLayout implements View.OnClickListener {

    private static final int REQ_MIN = 1007;
    private static final int REQ_CAPTURE_PHOTO = 1008;
    public static final int REQ_SELECT_IMAGE = 1009;
    private static final int REQ_BROWSE_IMAGE = 1010;
    private static final int REQ_MAX = 1011;

    private static final int DEFAULT_ROW_CONTENT_COUNT = 3;
    private static final int DEFAULT_ROW_COUNT = 3;
    public static final int IMG_SIZE = DensityUtil.getWidth(BeastBikes.getInstance()) * 9 / 38;

    private static final Stack<WeakReference<ImageView>> recycled = new Stack<WeakReference<ImageView>>();
    private ArrayList<String> pathQueue = new ArrayList<String>();
    private Activity context;
    private int rowContentCount;
    private int rowCount;
    private int imgSize;
    private int padding;
    private int horInterval, verInterval;

    private final AutoWrapViewGroup container;
    private final ImageView ivAdd;
    private int resAdd = R.drawable.ic_add_image;
    private String captureImagePath;
    private OnCreateContextMenuListener menuListener;

    private MaxPostImageNumber maxPostImageNumber;

    // ------public static methos-------

    public static boolean isPhotoRequest(int reqCode) {
        return reqCode > REQ_MIN && reqCode < REQ_MAX;
    }

    /**
     * 获取当前imageview展示图片的URL
     *
     * @param view
     * @return
     */
    public static String getUrlFromView(View view) {
        Object obj = view.getTag();
        return obj != null ? obj.toString() : "";
    }

    // ------public static methos-------

    /**
     * 每行3个元素，元素大小：元素间隔为3：1, 四周padding：元素间隔 ＝ 1.2：1
     *
     * @param context
     */
    public EditPhotoView(Activity context, MaxPostImageNumber maxPostImageNumber) {
        super(context);
        this.context = context;
        this.imgSize = IMG_SIZE;
        this.rowContentCount = DEFAULT_ROW_CONTENT_COUNT;
        this.rowCount = DEFAULT_ROW_COUNT;
        this.maxPostImageNumber = maxPostImageNumber;
        this.setOrientation(VERTICAL);

        container = new AutoWrapViewGroup(context);
        this.addView(container);
        this.horInterval = imgSize / 9;
        this.verInterval = imgSize / 9;
        this.config(rowContentCount, rowCount, imgSize, horInterval,
                verInterval);

        ivAdd = new ImageView(context);

        ivAdd.setBackgroundColor(context.getResources().getColor(R.color.common_bg_color));
        ivAdd.setImageResource(resAdd);
        ivAdd.setScaleType(ScaleType.CENTER);
        ivAdd.setOnClickListener(this);
        container.addView(ivAdd);
    }

    /**
     * 设置图片大小和四周pad
     *
     * @param imgSize 图片大小
     * @param
     */
    public void config(int rowContentCount, int rowCount, int imgSize,
                       int horInterval, int verInterval) {
//        padding = (DensityUtil.getWidth(getContext()) - imgSize * rowContentCount - horInterval
//                * (rowContentCount - 1)) / 2;
        this.setPadding(horInterval, 0, 0, horInterval);
        container.setHorizontalInterval(horInterval);
        container.setVerticalInterval(verInterval);
        container.setChildSize(imgSize);
        container.setChildRowCount(rowContentCount);
    }

    /**
     * @param rowContentCount
     * @param rowCount
     */
    public void configRow(int rowContentCount, int rowCount) {
        this.rowContentCount = rowContentCount;
        this.rowCount = rowCount;
    }

    /**
     * 设置添加按钮的显示资源ID
     *
     * @param resource
     */
    public void setAddResource(int resource) {
        this.resAdd = resource;
        if (ivAdd != null) {
            ivAdd.setImageDrawable(context.getResources().getDrawable(resource));
        }
    }

    /**
     * 设置按钮的长按菜单
     *
     * @param menuListener
     */
    public void setMenuListener(OnCreateContextMenuListener menuListener) {
        this.menuListener = menuListener;
    }

    /**
     * 获取内容的高度
     */
    public int getContentHeight() {
        int contentHeight = rowCount * imgSize + (rowCount - 1) * verInterval
                + 2 * padding;
        return contentHeight;
    }

    /**
     * 去掉出添加按钮外的所有控件
     */
    public void clear() {
        container.removeAllViews();
        container.addView(ivAdd);
    }


    /**
     * 移出指定的view
     *
     * @param url
     */
    public void removeView(String url) {
        int index = getIndexFromTag(url);
        if (index >= 0) {
            View from = container.getChildAt(index);
            container.removeView(from);
        }
    }

    /**
     * 获取选择或者拍摄的相片列表
     */
    public ArrayList<String> getSelectedFiles() {
        int size = container.getChildCount();
        if (size <= 0)
            return null;
        ArrayList<String> filepaths = new ArrayList<String>();
        for (int i = 0; i < size; i++) {
            ImageView iv = (ImageView) container.getChildAt(i);
            if (iv.equals(ivAdd))
                continue;

            String filepath = (String) iv.getTag();
            filepaths.add(filepath);
        }
        return filepaths;
    }

    /**
     * recycle all bitmaps, delete all selected or captured tmp files
     */
    public void recycle() {
        ArrayList<ImageView> toRemoved = new ArrayList<ImageView>();
        for (int i = 0, size = getChildCount(); i < size; i++) {
            ImageView iv = (ImageView) getChildAt(i);
            if (iv.equals(ivAdd))
                continue;

            toRemoved.add(iv);
            recycle(iv);
        }
    }


    private void addImageView(ImageView image) {
        container.addView(image, container.getChildCount() - 1);
        ivAdd.setVisibility(container.getChildCount() > rowContentCount * rowCount ? GONE : VISIBLE);
    }

    /**
     * recycle imageview
     */
    private void recycle(ImageView iv) {
        String filePath = (String) iv.getTag();
        //FileManager.deleteFile(filePath);
        iv.setImageBitmap(null);
        iv.setTag(null);
        //iv.setTag(R.id.arg1, null);
    }

    /**
     * handle photo request
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {

            case REQ_SELECT_IMAGE: {
                if (data != null) {
                    pathQueue = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                    clear();
                    int i = 0;
                    for (String path : pathQueue) {
                        addImage(path, i);
                        i++;
                    }
                }
                break;
            }
        }

    }

    private void addImage(String url, int pos) {
        ImageView image = getReusedImageView();
        image.setTag(url);
        image.setId(pos);
        image.setOnCreateContextMenuListener(menuListener);

        if (!TextUtils.isEmpty(url)) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(url, options);
            float scale = 1;
            int height = imgSize;
            if (options.outWidth > 0) {
                scale = (float) options.outHeight / (float) options.outWidth;
                height = (int) (imgSize * scale);
            }
            Picasso.with(getContext()).load("file://" + url).resize(imgSize, height).error(R.drawable.multi_image_selector_default_error)
                    .placeholder(R.drawable.multi_image_selector_default_error).into(image);
        } else {
            image.setImageResource(R.drawable.multi_image_selector_default_error);
        }

        addImageView(image);

    }

    private void removeSingleImage(int index) {

        if (index < 0)
            return;
        int total = container.getChildCount();

        ImageView iv = (ImageView) container.getChildAt(index);
        if (iv != ivAdd) {
            container.removeView(iv);
            if (total > rowContentCount * rowCount) {
                ivAdd.setVisibility(View.VISIBLE);
            }
            recycle(iv);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == ivAdd) {
            // 打开图库相册
            Intent it = new Intent(context, MultiImageSelectorActivity.class);
            //
            int maxPostImageNum;
            if (maxPostImageNumber.getMaxPostImageNumber() >= 9) {
                maxPostImageNum = 9;
            } else {
                maxPostImageNum = maxPostImageNumber.getMaxPostImageNumber();
                it.putExtra(MultiImageSelectorActivity.EXTRA_GALLERY_FULL, true);
            }
            it.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, maxPostImageNum);
            it.putStringArrayListExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, pathQueue);
            context.startActivityForResult(it,
                    REQ_SELECT_IMAGE);
        } else {
            Intent it = new Intent(context, ClubFeedImageDetailsActivity.class);
            it.putStringArrayListExtra(ClubFeedImageDetailsActivity.EXTRA_IMAGES, pathQueue);
            it.putExtra(ClubFeedImageDetailsActivity.EXTRA_POS, v.getId());
            context.startActivityForResult(it,
                    REQ_SELECT_IMAGE);
        }
    }

    public int getIndexFromTag(String tagPath) {
        for (int i = 0; i < this.getSelectedFiles().size(); i++) {
            if (tagPath.equals(this.getSelectedFiles().get(i))) {
                return i;
            }
        }
        return 0;
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
        iv.setScaleType(ScaleType.CENTER_CROP);
        iv.setOnClickListener(this);
        return iv;
    }

    public interface MaxPostImageNumber {
        int getMaxPostImageNumber();
    }

}
