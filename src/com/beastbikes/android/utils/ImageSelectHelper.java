package com.beastbikes.android.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.Toast;

import com.beastbikes.android.R;
import com.beastbikes.android.modules.preferences.ui.CutAvatarActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * @author chenqingfei
 */
public class ImageSelectHelper {

    public static final int EVENT_SELECT_IMAGE = 11123;
    public static final int EVENT_CAMERA_IMAGE = 11124;
    public static final int EVENT_CROP = 11125;

    private String imagePath = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DCIM).getAbsolutePath()
            + File.separator + "Beast" + System.currentTimeMillis();

    private String tmpImagePath = null;
    private SelectImageListener imageListener;
    //
    private Activity activity;
    AlertDialog dialog;
    String[] items = null;
    private boolean isCut = true;

    public interface SelectImageListener {
        void onfinishSelectImage(String path);
    }

    public ImageSelectHelper(Activity activity) {
        this.activity = activity;
        initView();
    }

    public void setSelectImageListener(SelectImageListener imageListener) {
        this.imageListener = imageListener;
    }

    public ImageSelectHelper(Activity activity,
                             SelectImageListener imageListener, boolean isCut) {

        this.activity = activity;
        this.imageListener = imageListener;
        this.isCut = isCut;
        initView();
    }

    private void initView() {
        items = activity.getResources().getStringArray(
                R.array.select_image_view_select_array);
        if (dialog == null) {
            dialog = new AlertDialog.Builder(activity).setItems(items,
                    new OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            switch (which) {
                                case 0: {
                                    selectImage(isCut);
                                    break;
                                }
                                case 1: {
                                    captureImage();
                                    break;
                                }
                                case 2: {
                                    break;
                                }
                            }
                        }
                    }).create();
        }
    }

    public void show() {
        if (dialog != null) {
            dialog.show();
        }
    }

    public void dismissDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    public void selectImage(boolean isCut) {
        this.isCut = isCut;

        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                "image/*");
        activity.startActivityForResult(intent, EVENT_SELECT_IMAGE);

    }

    public void captureImage() {
        if (Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            File tmpFile = new File(Environment.getExternalStorageDirectory(),
                    "tmp_avatar.jpg");
            Uri mImageCaptureUri = Uri.fromFile(tmpFile);
            tmpImagePath = tmpFile.getAbsolutePath();

            intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
                    mImageCaptureUri);

            try {
                intent.putExtra("return-data", true);

                activity.startActivityForResult(intent, EVENT_CAMERA_IMAGE);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(activity, R.string.select_image_view_no_sdcard,
                    Toast.LENGTH_LONG).show();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {

            case EVENT_SELECT_IMAGE:
                if (data != null) {
                    Uri mImageCaptureUri = data.getData();
                    Cursor cursor = null;
                    try {
                        cursor = activity.getContentResolver().query(
                                mImageCaptureUri,
                                new String[]{MediaStore.Images.ImageColumns.DATA,
                                        MediaStore.Images.ImageColumns.SIZE},
                                null, null, null);
                        if (cursor != null) {
                            cursor.moveToFirst();
                            tmpImagePath = cursor.getString(0);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (cursor != null)
                            cursor.close();
                        if (TextUtils.isEmpty(tmpImagePath))
                            tmpImagePath = mImageCaptureUri.getPath();
                    }

                    if (isCut) {
                        doCrop(tmpImagePath);

                    } else {
                        tmpImagePath = compressImage(tmpImagePath);
                        imageListener.onfinishSelectImage(tmpImagePath);
                    }

                }
                break;
            case EVENT_CAMERA_IMAGE:
                if (isCut) {
                    doCrop(tmpImagePath);
                } else {
                    tmpImagePath = compressImage(tmpImagePath);
                    imageListener.onfinishSelectImage(tmpImagePath);
                }
                break;
            case EVENT_CROP:
                if (data != null) {
                    final String path = data
                            .getStringExtra(CutAvatarActivity.EXTRA_AVATAR_PATH);
                    if (null != imageListener)
                        imageListener.onfinishSelectImage(path);
                }
                break;

            default:
                break;
        }
    }

    private void doCrop(String path) {
        if (!TextUtils.isEmpty(path)) {
            Intent i = new Intent(activity, CutAvatarActivity.class);
            i.putExtra(CutAvatarActivity.EXTRA_AVATAR_PATH, path);
            activity.startActivityForResult(i, EVENT_CROP);
        } else {
            Toast.makeText(activity, R.string.select_image_view_no_supprot_cap,
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void doCrop() {
        if (TextUtils.isEmpty(tmpImagePath)) {
            return;
        }
        int length = 600;
        int ow = 0;
        int oh = 0;
        File file = new File(tmpImagePath);
        Options opts = new Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), opts);
        ow = opts.outWidth;
        oh = opts.outHeight;
        int ol = ow < oh ? ow : oh;
        length = ol < length ? ol : length;

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");

        List<ResolveInfo> list = activity.getPackageManager()
                .queryIntentActivities(intent, 0);

        int size = list.size();

        if (size == 0) {
            Toast.makeText(activity, R.string.select_image_view_no_supprot_cap,
                    Toast.LENGTH_SHORT).show();

            return;
        } else {

            Uri mImageCaptureUri = Uri.parse(tmpImagePath);

            intent.setData(mImageCaptureUri);

            intent.putExtra("outputX", length);
            intent.putExtra("outputY", length);
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("scale", true);
            intent.putExtra("return-data", false);
            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(new File(imagePath)));
            intent.putExtra("outputFormat",
                    Bitmap.CompressFormat.JPEG.toString());

            Intent i = new Intent(intent);
            ResolveInfo res = list.get(0);

            i.setComponent(new ComponentName(res.activityInfo.packageName,
                    res.activityInfo.name));

            activity.startActivityForResult(i, EVENT_CROP);

        }
    }

    public String getFilePath() {
        return imagePath;
    }

    private String compressImage(String file) {
        String outPath = null;
        if (Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            File tmpFile = new File(Environment
                    .getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_DCIM).getAbsolutePath(),
                    "beast" + System.currentTimeMillis() + "avatar.jpg");
            try {
                BitmapFactory.Options newOpts = new BitmapFactory.Options();
                newOpts.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(file, newOpts);

                final int minSideLength = Math.min(800, 480);
                newOpts.inSampleSize = computeSampleSize(newOpts,
                        minSideLength, 800 * 480);

                newOpts.inJustDecodeBounds = false;
                newOpts.inInputShareable = true;
                newOpts.inPurgeable = true;

                Bitmap image = BitmapFactory.decodeFile(file, newOpts);

                if (null != image) {

                    OutputStream osm = new FileOutputStream(tmpFile);
                    image.compress(Bitmap.CompressFormat.JPEG, 40, osm);

                    if (!image.isRecycled()) {
                        image.recycle();
                        image = null;
                    }
                    osm.close();
                    osm = null;

                    outPath = tmpFile.getAbsolutePath();
                }

            } catch (Exception e) {
                e.printStackTrace();

            }
        } else {
            Toast.makeText(activity, R.string.select_image_view_no_sdcard,
                    Toast.LENGTH_LONG).show();
        }
        return outPath;
    }

    public static int computeSampleSize(BitmapFactory.Options options,
                                        int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength,
                maxNumOfPixels);

        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }

        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options,
                                                int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;

        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
                .sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
                Math.floor(w / minSideLength), Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }

        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

}
