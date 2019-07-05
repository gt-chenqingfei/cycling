package com.beastbikes.android.ble.otadownload;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.text.TextUtils;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.beastbikes.android.Constants;
import com.beastbikes.android.utils.FileUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by zhangyao on 2016/3/4.
 */
public class OTAManage {

    // ble img
    public static final int OTA_BLE_IMG = 1;
    // mcu img
    public static final int OTA_MCU_IMG = 2;
    // ui img
    public static final int OTA_UI_IMG = 3;
    // a_gps img
    public static final int OTA_A_GPS_IMG = 4;
    // font img
    public static final int OTA_FONT_IMG = 5;
    // power img
    public static final int OTA_POWER_IMG = 6;

    public interface DownloadFileListener {
        public void onDownloadFileSuccess(int type, String versionName, String filePath);

        public void onDownloadFileError(int type);
    }

    private static String OTA_API = "http://static.speedx.com/speedforce/update.json";
    private RequestQueue mQueu;
    private Context context;

    private SharedPreferences sp;

    private DownloadFileListener downloadFileListener;

    public OTAManage(Context context) {
        this.mQueu = Volley.newRequestQueue(context);
        this.context = context;
        this.sp = context.getSharedPreferences(context.getPackageName(), 0);
    }

    public OTAManage(Context context, DownloadFileListener downloadFileListener) {
        this.mQueu = Volley.newRequestQueue(context);
        this.context = context;
        this.downloadFileListener = downloadFileListener;
        this.sp = context.getSharedPreferences(context.getPackageName(), 0);
    }

    public void init() {
        //获取dto升级有关的信息
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(OTA_API, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (null == response) {
                            return;
                        } else if (response.optInt("code") == 0) {
                            JSONObject result = response.optJSONObject("result");
                            JSONObject speed_force_v1 = result.optJSONObject("speed-force-v1.0");
                            JSONObject main = speed_force_v1.optJSONObject("main");
                            JSONObject ble = speed_force_v1.optJSONObject("ble");
                            JSONObject ui = speed_force_v1.optJSONObject("ui");
                            String mainAPI = main.optString("location");
                            String bleAPI = ble.optString("location");
                            String uiAPI = ui.optString("location");
                            String bleVersion = ble.optString("version");
                            String mainVersion = main.optString("version");
                            String uiVersion = ui.optString("version");
                            int bleChecksum = ble.optInt("checksum");
                            int mainChecksum = main.optInt("checksum");
                            int uiChecksum = ui.optInt("checksum");

                            if (!getFile(uiAPI).exists()) {
                                downLoadFile(OTA_UI_IMG, uiVersion, uiAPI, bleChecksum);
                            }

                            if (!getFile(mainAPI).exists())
                                downLoadFile(OTA_MCU_IMG, bleVersion, mainAPI, mainChecksum);

                            //若文件未下载则下载
                            if (!getFile(bleAPI).exists())
                                downLoadFile(OTA_BLE_IMG, mainVersion, bleAPI, uiChecksum);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        mQueu.add(jsonObjectRequest);
    }

    //下载数据包
    public void downLoadFile(final int type, final String versionName, final String path, final int checksum) {
        FileRequest request = new FileRequest(path, new Response.Listener<byte[]>() {
            public void onResponse(byte[] bytes) {
                if (bytes == null)
                    return;

                String filePath = saveFile(bytes, path, type);
                if (TextUtils.isEmpty(filePath) && null != downloadFileListener) {
                    downloadFileListener.onDownloadFileError(type);
                } else if (null != downloadFileListener) {
                    saveOTAPath(type, versionName, filePath, checksum);
                    downloadFileListener.onDownloadFileSuccess(type, versionName, filePath);
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                if (null != downloadFileListener) {
                    downloadFileListener.onDownloadFileError(type);
                }
            }
        });
        mQueu.add(request);
    }

    //保存文件到本地
    private String saveFile(byte[] bytes, String path, int type) {
        FileOutputStream fos = null;
        try {
            File file = getFile(path);
            if (null == file) {
                return "";
            }
            fos = new FileOutputStream(file);
            fos.write(bytes);
            fos.flush();
            fos.close();

            return file.getAbsolutePath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();

            try {
                if (fos != null)
                    fos.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
            try {
                if (fos != null)
                    fos.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        return "";
    }


    //自定义的请求
    class FileRequest extends Request<byte[]> {
        Response.Listener<byte[]> listener;

        public FileRequest(String url, Response.Listener<byte[]> listener,
                           Response.ErrorListener errorListener) {
            super(url, errorListener);
            this.listener = listener;
        }

        @Override
        protected Response<byte[]> parseNetworkResponse(NetworkResponse networkResponse) {
            byte[] bytes = networkResponse.data;
            return Response.success(bytes, HttpHeaderParser.parseCacheHeaders(networkResponse));
        }

        @Override
        protected void deliverResponse(byte[] bytes) {
            listener.onResponse(bytes);
        }
    }

    //根据下载的文件对象
    public File getFile(String api) {
        String path = Environment
                .getExternalStorageDirectory().getAbsolutePath() + File.separatorChar + "beast" +
                File.separatorChar + "ota";
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }

        return new File(file.getPath(), hashKeyForDisk(api));
    }

    /**
     * 删除下载的文件
     *
     * @return
     */
    public boolean deleteFolder() {
        String path = Environment
                .getExternalStorageDirectory().getAbsolutePath() + File.separatorChar + "beast" +
                File.separatorChar + "ota";
        return FileUtil.deleteFolder(path);
    }

    //将图片url转化为,md5，作为图片名
    private static String hashKeyForDisk(String key) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }

    private static String bytesToHexString(byte[] bytes) {
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

    /**
     * 保存OTA文件路径
     *
     * @param type
     * @param versionName
     * @param filePath
     */
    public void saveOTAPath(int type, String versionName, String filePath, int checksum) {
        JSONObject json = new JSONObject();
        try {
            json.put("type", type);
            json.put("version", versionName);
            json.put("path", filePath);
            json.put("checksum", checksum);
        } catch (JSONException e) {
        }
        switch (type) {
            case 0x01:
                sp.edit().putString(Constants.BLE.PREF_BLE_IMG_KEY, json.toString()).commit();
                break;
            case 0x02:
                sp.edit().putString(Constants.BLE.PREF_MCU_IMG_KEY, json.toString()).commit();
                break;
            case 0x03:
                sp.edit().putString(Constants.BLE.PREF_UI_IMG_KEY, json.toString()).commit();
                break;
            case 0x04:
                sp.edit().putString(Constants.BLE.PREF_A_GPS_IMG_KEY, json.toString()).commit();
                break;
            case 0x05:
                sp.edit().putString(Constants.BLE.PREF_FONT_IMG_KEY, json.toString()).commit();
                break;
            case 0x06:
                sp.edit().putString(Constants.BLE.PREF_POWER_IMG_KEY, json.toString()).commit();
                break;
        }
    }

}
