package com.beastbikes.android.modules.user.ui;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;

import com.avos.avoscloud.AVAnalytics;
import com.beastbikes.android.R;
import com.beastbikes.android.modules.user.filter.other.ImageCropView;
import com.beastbikes.android.widget.MaterialDialog;
import com.beastbikes.framework.android.analytics.annotation.Alias;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.ui.android.BaseFragmentActivity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Alias("水印裁剪页")
@LayoutResource(R.layout.activity_watermark_cropper)
public class WatermarkCropperActivity extends BaseFragmentActivity implements
        OnClickListener {

    public static final String EXTRA_PHOTO_PATH = "path";

    private final DisplayMetrics displayMetrics = new DisplayMetrics();

    private static final Logger logger = LoggerFactory
            .getLogger(WatermarkCropperActivity.class);

    private static final SimpleDateFormat SDF = new SimpleDateFormat(
            "yyyyMMddhhmmss", Locale.getDefault());

    private String avatarPath;

    @IdResource(R.id.activity_watermark_cropper_view)
    private ImageCropView imageCut;

    @IdResource(R.id.activity_watermark_cropper_ok)
    private ImageView ok;

    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.overridePendingTransition(R.anim.activity_in_from_right,
                R.anim.activity_none);
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        this.imageCut.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT, displayMetrics.widthPixels));

        System.gc();

        this.avatarPath = generateAvatarPath();
        this.ok.setOnClickListener(this);

        final Intent intent = getIntent();

        if (null == intent) {
            logger.debug("the bundle is null", getIntent().toString());
            this.finish();
        }

        if (!intent.hasExtra(EXTRA_PHOTO_PATH)) {
            final MaterialDialog dialog = new MaterialDialog(this);
            dialog.setMessage(R.string.user_setting_activity_file_is_null);
            dialog.setNegativeButton(R.string.label_i_know, new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    finish();
                }
            }).show();
        }

        final Uri thumbnailUri = intent.getParcelableExtra(EXTRA_PHOTO_PATH);

        final Options opts = new Options();
        final ContentResolver cr = this.getContentResolver();

        opts.inSampleSize = 2;

        try {
            final InputStream is = cr.openInputStream(thumbnailUri);
            this.bitmap = BitmapFactory.decodeStream(is, null, opts);
        } catch (FileNotFoundException e) {
            this.bitmap = null;
        }

        // BitmapFactory.Options opts = new BitmapFactory.Options();
        // opts.inJustDecodeBounds = true;
        // BitmapFactory.decodeFile(uri, opts);
        // opts.inJustDecodeBounds = false;

        // int degree = this.getPictureDegree(uri);
        //
        // opts.inSampleSize = 2;
        // bitmap = BitmapFactory.decodeFile(uri, opts);

        // if (degree == 90) {
        // Matrix matrix = new Matrix();
        // matrix.reset();
        // matrix.setRotate(90);
        // Bitmap tmpBitmap = Bitmap.createBitmap(bitmap, 0, 0,
        // bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        // if (null != bitmap) {
        // bitmap.recycle();
        // bitmap = null;
        // }
        // this.imageCut.setImageBitmap(tmpBitmap);
        // } else {
        // }
        this.imageCut.setImageBitmap(bitmap);
        this.imageCut.setupView();
    }

    private String generateAvatarPath() {
        final File dcim = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        return new File(dcim, String.format("Beast_%s.png",
                SDF.format(new Date()))).getAbsolutePath();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cut_avatar_btn_ok:
                bitmap2file(imageCut.onClip());
                Intent intent = new Intent(WatermarkCropperActivity.this,
                        WatermarkGalleryActivity.class);
                intent.putExtra(EXTRA_PHOTO_PATH, avatarPath);
                setResult(4, intent);
                finish();
                break;
            case R.id.cut_avatar_btn_cancle:
                this.finish();
                break;
            default:
                break;
        }
    }

    private int getPictureDegree(String path) {
        int degree = 0;
        if (!TextUtils.isEmpty(path)) {
            try {
                ExifInterface exifInterface = new ExifInterface(path);
                int orientation = exifInterface.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL);
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        degree = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        degree = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        degree = 270;
                        break;
                    default:
                        break;
                }
            } catch (IOException e) {
                AVAnalytics.onError(this, "get ExifInterface error.");
                logger.debug("the ExifInterface info is :", path);
            }
        }
        return degree;
    }

    @Override
    public void finish() {
        super.finish();
        super.overridePendingTransition(0, R.anim.activity_out_to_right);
    }

    private boolean bitmap2file(Bitmap bmp) {
        CompressFormat format = Bitmap.CompressFormat.PNG;
        OutputStream stream = null;
        try {
            stream = new FileOutputStream(avatarPath);
            return bmp.compress(format, 100, stream);
        } catch (FileNotFoundException e) {
            return false;
        } finally {
            if (null != stream) {
                try {
                    stream.close();
                    stream = null;
                } catch (IOException e) {
                }
            }
        }

    }
}
