package com.beastbikes.android.modules.cycling.club.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.avos.avoscloud.AVAnalytics;
import com.beastbikes.android.R;
import com.beastbikes.android.modules.cycling.club.ui.widget.ImageCut;
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
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

@Alias("图片按裁剪页")
@LayoutResource(R.layout.activity_club_cut_bitmap)
public class ClubCutBitmapActivity extends BaseFragmentActivity implements
        OnClickListener {

    public static final String EXTRA_AVATAR_PATH = "path";

    private static final Logger logger = LoggerFactory.getLogger(ClubCutBitmapActivity.class);

    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat SDF = new SimpleDateFormat(
            "yyyyMMddhhmmss");

    private String avatarPath;

    @IdResource(R.id.cut_avatar_view)
    private ImageCut imageCut;

    @IdResource(R.id.cut_avatar_btn_ok)
    private Button ok;

    @IdResource(R.id.cut_avatar_btn_cancle)
    private Button cancle;

    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.overridePendingTransition(R.anim.activity_in_from_right,
                R.anim.activity_none);

        final android.support.v7.app.ActionBar bar = getSupportActionBar();
        if (null != bar) {
            bar.setDisplayHomeAsUpEnabled(true);
        }

        System.gc();

        this.avatarPath = generateAvatarPath();
        this.ok.setOnClickListener(this);
        this.cancle.setOnClickListener(this);

        final Bundle bundle = getIntent().getExtras();

        if (null == bundle) {
            logger.debug("the bundle is null", getIntent().toString());
            this.finish();
        }

        final String avatarImgPath = bundle.getString(EXTRA_AVATAR_PATH);
        if (TextUtils.isEmpty(avatarImgPath)) {
            new AlertDialog.Builder(this).setMessage(
                    R.string.user_setting_activity_file_is_null).setNegativeButton(R.string.label_i_know,
                    new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                }
            }).show();
        }

        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(avatarImgPath, opts);
        opts.inJustDecodeBounds = false;

        opts.inJustDecodeBounds = false;
        int w = opts.outWidth;
        int h = opts.outHeight;
        // 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 1280f;// 这里设置高度为800f
        float ww = 800f;// 这里设置宽度为480f
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;// be=1表示不缩放
        if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
            be = (int) (opts.outWidth / ww);
        } else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
            be = (int) (opts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        opts.inSampleSize = be;// 设置缩放比例

        int degree = this.getPictureDegree(avatarImgPath);

        bitmap = BitmapFactory.decodeFile(avatarImgPath, opts);

        if (degree == 90) {
            Matrix matrix = new Matrix();
            matrix.reset();
            matrix.setRotate(90);
            Bitmap tmpBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                    bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            if (null != bitmap) {
                bitmap.recycle();
                bitmap = null;
            }
            this.imageCut.setImageBitmap(tmpBitmap);
        } else {
            this.imageCut.setImageBitmap(bitmap);
        }
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
                Intent intent = new Intent();
                intent.putExtra(EXTRA_AVATAR_PATH, avatarPath);
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
        CompressFormat format = CompressFormat.PNG;
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
