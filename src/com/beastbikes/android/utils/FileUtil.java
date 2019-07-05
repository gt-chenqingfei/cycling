package com.beastbikes.android.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import com.beastbikes.android.R;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by chenqingfei on 15/12/14.
 */
public class FileUtil {

    private static ConcurrentHashMap<String, ReentrantReadWriteLock> fileLocks = new ConcurrentHashMap();

    public static byte[] readContentBytesFromFile(File fileForRead) {
        if (fileForRead == null) {
            Log.e("m", "null file object.");
            return null;
        }
        if ((!fileForRead.exists()) || (!fileForRead.isFile())) {

            Log.d("m", new FileNotFoundException().toString());

            return null;
        }

        Lock readLock = getLock(fileForRead.getAbsolutePath()).readLock();
        readLock.lock();
        byte[] data = null;
        InputStream input = null;
        try {
            data = new byte[(int) fileForRead.length()];
            int totalBytesRead = 0;
            input = new BufferedInputStream(new FileInputStream(fileForRead),
                    8192);
            int bytesRemaining;
            while (totalBytesRead < data.length) {
                bytesRemaining = data.length - totalBytesRead;
                int bytesRead = input
                        .read(data, totalBytesRead, bytesRemaining);
                if (bytesRead > 0) {
                    totalBytesRead += bytesRead;
                }
            }
            return data;
        } catch (IOException e) {
            Log.e("m", "Exception during file read", e);
        } finally {
            closeQuietly(input);
            readLock.unlock();
        }
        return null;
    }

    private static ReentrantReadWriteLock getLock(String path) {
        ReentrantReadWriteLock lock = (ReentrantReadWriteLock) fileLocks
                .get(path);
        if (lock == null) {
            lock = new ReentrantReadWriteLock();
            ReentrantReadWriteLock oldLock = (ReentrantReadWriteLock) fileLocks
                    .putIfAbsent(path, lock);
            if (oldLock != null) {
                lock = oldLock;
            }
        }
        return lock;
    }

    public static String readContentFromFile(File fileForRead) {
        byte[] data = readContentBytesFromFile(fileForRead);
        if ((data == null) || (data.length == 0)) {
            return "";
        }
        return new String(data);
    }

    public static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null)
                closeable.close();
        } catch (IOException e) {
            Log.d("m", e.toString());
        }
    }

    public static File archivePath(Context context, String filePath, String fileName) {
        File dir = context.getDir(filePath, 0);
        return new File(dir, fileName);
    }

    public static boolean saveContentToFile(String content, File fileForSave) {
        try {
            return saveContentToFile(content.getBytes("utf-8"), fileForSave);
        } catch (UnsupportedEncodingException e) {
            Log.d("m", e.toString());
        }
        return false;
    }

    public static boolean saveContentToFile(byte[] content, File fileForSave) {
        Lock writeLock = getLock(fileForSave.getAbsolutePath()).writeLock();
        boolean succeed = true;
        FileOutputStream out = null;
        if (writeLock.tryLock()) {
            try {
                out = new FileOutputStream(fileForSave, false);
                out.write(content);
            } catch (Exception e) {
                Log.d("m", e.toString());
                succeed = false;
            } finally {
                if (out != null) {
                    closeQuietly(out);
                }
                writeLock.unlock();
            }
        }
        return succeed;
    }

    public static void removeArchive(Context context, String filePath, String fileName) {
        File file = archivePath(context, filePath, fileName);
        file.delete();

    }

    public static String saveImage(String imgUrl, Context context) {

        String ret = context.getString(R.string.activity_finished_share_sdcard_success);

        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            try {
                Bitmap bitmap = Picasso.with(context).load(imgUrl).get();
                String filePath = MediaStore.Images.Media.insertImage(context.getContentResolver(),
                        bitmap, imgUrl, imgUrl);
                Uri uri = Uri.parse(filePath);
                context.sendBroadcast(new Intent(
                        android.hardware.Camera.ACTION_NEW_PICTURE, uri));
                context.sendBroadcast(new Intent("com.android.camera.NEW_PICTURE",
                        uri));
                if (bitmap != null && !bitmap.isRecycled()) {
                    bitmap.recycle();
                }
            } catch (Exception e) {
                ret = context.getString(R.string.image_save_fail);
            }
        } else {
            ret = context.getString(R.string.select_image_view_no_sdcard);
        }
        return ret;
    }

    /**
     * 删除文件
     *
     * @param path
     */
    public static boolean deleteFile(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }

        File file = new File(path);
        if (null != file && file.exists() && file.isFile()) {
            file.delete();
        }

        return !file.exists();
    }

    public static boolean deleteFolder(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return false;
        } else {
            if (file.isFile()) {
                // 为文件时调用删除文件方法
                return deleteFile(filePath);
            } else {
                // 为目录时调用删除目录方法
                return deleteDirectory(filePath);
            }
        }
    }

    public static boolean deleteDirectory(String filePath) {
        boolean flag = false;
        //如果filePath不以文件分隔符结尾，自动添加文件分隔符
        if (!filePath.endsWith(File.separator)) {
            filePath = filePath + File.separator;
        }
        File dirFile = new File(filePath);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        flag = true;
        File[] files = dirFile.listFiles();
        if(files == null)
            return flag;
        //遍历删除文件夹下的所有文件(包括子目录)
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                //删除子文件
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag) break;
            } else {
                //删除子目录
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag) break;
            }
        }
        if (!flag) return false;
        //删除当前空目录
        return dirFile.delete();
    }

    public static String saveImage(Uri imgUri, Context context) {

        String ret = context.getString(R.string.activity_finished_share_sdcard_success);

        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            try {
                Bitmap bitmap = Picasso.with(context).load(imgUri).get();
                String filePath = MediaStore.Images.Media.insertImage(context.getContentResolver(),
                        bitmap, imgUri.getUserInfo(), imgUri.getUserInfo());
                Uri uri = Uri.parse(filePath);
                context.sendBroadcast(new Intent(
                        android.hardware.Camera.ACTION_NEW_PICTURE, uri));
                context.sendBroadcast(new Intent("com.android.camera.NEW_PICTURE",
                        uri));
                if (bitmap != null && !bitmap.isRecycled()) {
                    bitmap.recycle();
                }
            } catch (Exception e) {
                ret = context.getString(R.string.image_save_fail);
            }
        } else {
            ret = context.getString(R.string.select_image_view_no_sdcard);
        }
        return ret;
    }

}
