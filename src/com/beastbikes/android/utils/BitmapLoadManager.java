package com.beastbikes.android.utils;

/**
 * Created by zhangyao on 2016/2/28.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.webkit.URLUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 图片下载工具类
 *
 * @author gaozhibin
 */
public class BitmapLoadManager {
    private static final String TAG = "BtimapUtil";


    /**
     * 根据网址获得图片，优先从本地获取，本地没有则从网络下载
     *
     * @param url     图片网址
     * @param context 上下文
     * @return 图片
     */
    public static Bitmap getBitmapbyFile(final String url, final Context context) {

        final File file = getBitmapFile(url, context);
        if (file != null && file.exists()) {
            return BitmapFactory.decodeFile(file.getPath());
        }
        return null;
    }

    public static void fetchImage(final String url, final Context context) {
        final File file = getBitmapFile(url, context);
        if (file != null) {
            getNetBitmap(url, file, context);
        }
    }

    //删除图片
    public static boolean deleteBitmap(final String url, final Context context) {
        final File file = getBitmapFile(url, context);
        if (file != null && file.exists()) {
            return file.delete();
        }

        return false;
    }

    //获取图片file对象
    private static File getBitmapFile(String url, Context context) {
        File file = null;
        try {
            if (url == null && TextUtils.isEmpty(url) && !URLUtil.isHttpsUrl(url) && !URLUtil.isHttpUrl(url)) {
                return null;
            }
            String path = getPath(context);
            if (!TextUtils.isEmpty(path)) {
                file = new File(path, hashKeyForDisk(url));
            }
        } catch (Exception e) {

        }
        return file;
    }

    //判断是否有缓存的图片
    public static boolean isCache(String url, Context context) {

        return isExistFile(context, url);
    }

    /**
     * “/data/data/本应用包名/cache/separatorChar/”
     *
     * @param context 上下文
     * @return 本地图片存储目录
     */
    public static String getPath(Context context) {
        String path = null;
        try {
            path = context.getDir("images", Context.MODE_PRIVATE).getPath();
            File file = new File(path);
            boolean isExist = file.exists();
            if (!isExist) {
                file.mkdirs();
            }
            return file.getPath();
        } catch (Exception e) {

        }
        return null;
    }

    public static boolean isExistFile(Context context, String url) {

        String path = getPath(context);
        File file = new File(path, hashKeyForDisk(url));
        return file.exists();
    }

    /**
     * 网络可用状态下，下载图片并保存在本地
     *
     * @param strUrl  图片网址
     * @param file    本地保存的图片文件
     * @param context 上下文
     * @return 图片
     */
    private static void getNetBitmap(String strUrl, File file, Context context) {

        if (isConnnected(context)) {
            InputStream in = null;
            FileOutputStream out = null;
            try {
                URL url = new URL(strUrl);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setDoInput(true);
                con.connect();
                in = con.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(in);
                out = new FileOutputStream(file.getAbsolutePath());
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.flush();

                if (!bitmap.isRecycled()) {
                    bitmap.recycle();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (null != in) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (null != out) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    public static boolean isConnnected(Context context) {

        ConnectivityManager manager = (ConnectivityManager) context
                .getApplicationContext().getSystemService(
                        Context.CONNECTIVITY_SERVICE);

        if (manager == null) {
            return false;
        }

        NetworkInfo networkinfo = manager.getActiveNetworkInfo();

        if (networkinfo == null || !networkinfo.isAvailable()) {
            return false;
        }

        return true;
    }


    //将图片url转化为,md5，作为图片名
    public static String hashKeyForDisk(String key) {
        String cacheKey = "";
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }

    public static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

}